package com.romens.yjk.health.im;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.text.TextUtils;

import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.MyApplication;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by siery on 15/7/10.
 */
public class VoicePlayController {
    private static volatile VoicePlayController Instance = null;

    public static VoicePlayController getInstance() {
        VoicePlayController localInstance = Instance;
        if (localInstance == null) {
            synchronized (VoicePlayController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new VoicePlayController();
                }
            }
        }
        return localInstance;
    }

    private String playMsgId;
    private int totalTime = 0;
    private VoicePlayCallback playCallback;

    private MediaPlayer mediaPlayer = null;
    private static boolean isPlaying = false;
    private PowerManager.WakeLock mWakeLock;

    private void updateAudioRecordInterface() {
        if (isPlaying) {
            try {
                if (mWakeLock == null) {
                    PowerManager pm = (PowerManager) ApplicationLoader.applicationContext.getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "audio record lock");
                    mWakeLock.acquire();
                }
            } catch (Exception e) {
                FileLog.e("romens", e);
            }
        } else {
            if (mWakeLock != null) {
                try {
                    mWakeLock.release();
                    mWakeLock = null;
                } catch (Exception e) {
                    FileLog.e("romens", e);
                }
            }
        }
    }


    public boolean playVoice(EMMessage message, VoicePlayCallback callback) {
        if (message == null || callback == null) {
            return false;
        }
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        String localFilePath = voiceBody.getLocalUrl();
        if (!(new File(localFilePath).exists())) {
            return false;
        }
        final String targetPlayMsgId = message.getMsgId();
        if (!TextUtils.equals(playMsgId, targetPlayMsgId)) {
            if (isPlaying) {
                stopPlayVoice();
            }
        }
        playMsgId = targetPlayMsgId;
        playCallback = callback;
        totalTime = voiceBody.getLength() * 1000;
        AudioManager audioManager = (AudioManager) MyApplication.applicationContext.getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
        if (IMHXSDKHelper.getInstance().getModel().getSettingMsgSpeaker()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
        try {
            mediaPlayer.setDataSource(localFilePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }

            });
            isPlaying = true;
            updateAudioRecordInterface();
            playCallback.onStarted(playMsgId, totalTime);
            createTimer();
            mediaPlayer.start();
        } catch (Exception e) {
            stopPlayVoice();
            return false;
        }
        return true;
    }

    public void stopPlayVoice() {
        destroyTimer();
        // stop play voice
        if (isPlaying && mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();

        }
        if (isPlaying) {
            isPlaying = false;
            if (playCallback != null) {
                playCallback.onStopped(playMsgId);
            }
        }
        time = 0;
        totalTime = 0;
        playMsgId = null;
        playCallback = null;
        updateAudioRecordInterface();

    }

    private Timer timeTimer;
    private final Object timerSync = new Object();
    private int time = 0;
    private long lastCurrentTime;

    private void createTimer() {
        if (timeTimer != null) {
            return;
        }
        time = 0;
        lastCurrentTime = System.currentTimeMillis();
        timeTimer = new Timer();
        timeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                time = (int) (currentTime - lastCurrentTime);
                //Log.e("progress", String.format("time:%d  total:%d", time, totalTime));

                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (time <= totalTime) {
                            playCallback.onProgress(playMsgId, time / 1000, totalTime == 0 ? 0 : (time * 100 / totalTime));
                        } else {
                            playCallback.onProgress(playMsgId, time / 1000, 100);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void destroyTimer() {
        try {
            synchronized (timerSync) {
                if (timeTimer != null) {
                    timeTimer.cancel();
                    timeTimer = null;
                }
                time = 0;
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    public void onDestroy() {
        if (!TextUtils.isEmpty(playMsgId)) {
            stopPlayVoice();
        }
    }

    public boolean isPlaying(EMMessage message) {
        if (message != null && (!TextUtils.isEmpty(playMsgId)) && TextUtils.equals(playMsgId, message.getMsgId())) {
            return isPlaying;
        }
        return false;
    }

    public interface VoicePlayCallback {
        void onStarted(String messageId, int totalTime);

        void onStopped(String messageId);

        void onProgress(String messageId, int time, int progress);
    }


}
