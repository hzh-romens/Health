package com.romens.yjk.health.helper;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.easemob.EMCallBack;
import com.easemob.applib.utils.ImageCache;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.ImageMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.im.NotificationCenter;
import com.romens.yjk.health.db.entity.MessageObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by siery on 15/7/28.
 */
public class MessageImageFileHelper {

    private LinkedHashMap<String, Integer> loadingFiles = new LinkedHashMap<>();

    private static volatile MessageImageFileHelper Instance = null;

    public static MessageImageFileHelper getInstance() {
        MessageImageFileHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (MessageImageFileHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MessageImageFileHelper();
                }
            }
        }
        return localInstance;
    }

    public void loadFile(MessageObject messageObject) {

        ImageMessageBody imgBody = (ImageMessageBody) messageObject.messageOwner.getBody();
        final String localFilePath;

        if (imgBody == null || imgBody.getLocalUrl() == null) {
            return;
        }
        if(messageObject.isOut()){
            localFilePath=imgBody.getLocalUrl();
        }else{
            String remotePath = imgBody.getRemoteUrl();
            localFilePath = getImagePath(remotePath);
        }


        if (localFilePath != null && new File(localFilePath).exists()) {
            Bitmap bitmap = ImageCache.getInstance().getForImgPath(localFilePath);
            if (bitmap == null) {
                loadingFiles.put(localFilePath, 0);
                new AsyncTask<Void, Void, Bitmap>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        int degree = ImageUtils.readPictureDegree(localFilePath);
                        if (degree != 0) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageFileLoadProgressChanged, localFilePath, 0);
                        }
                    }

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        Bitmap bitmap = ImageUtils.decodeScaleImage(localFilePath, ImageUtils.SCALE_IMAGE_WIDTH,
                                ImageUtils.SCALE_IMAGE_HEIGHT);
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        super.onPostExecute(result);
                        loadingFiles.remove(localFilePath);
                        if (result != null) {
                            ImageCache.getInstance().putForImgPath(localFilePath, result);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageFileDidLoaded,localFilePath, result);
                        } else {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageFileDidFailedLoad,localFilePath, result);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageFileDidLoaded,localFilePath, bitmap);
                return;
            }
            return;
        }
//        else {
//            // The local full size pic does not exist yet.
//            // ShowBigImage needs to download it from the server
//            // first
//            // intent.putExtra("", message.get);
//            ImageMessageBody body = (ImageMessageBody) message.getBody();
//            intent.putExtra("secret", body.getSecret());
//            intent.putExtra("remotepath", remote);
//        }
        String secret = imgBody.getSecret();
        String remoteFilePath = imgBody.getRemoteUrl();

        final String remoteLocalFilePath = getLocalFilePath(remoteFilePath);
        loadingFiles.put(remoteLocalFilePath, 0);
        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {
                loadingFiles.remove(remoteLocalFilePath);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        int screenWidth = AndroidUtilities.displayMetrics.widthPixels;
                        int screenHeight = AndroidUtilities.displayMetrics.heightPixels;

                        Bitmap bitmap = ImageUtils.decodeScaleImage(remoteLocalFilePath, screenWidth, screenHeight);
                        if (bitmap != null) {
                            ImageCache.getInstance().putForImgPath(remoteLocalFilePath, bitmap);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageFileDidLoaded, remoteLocalFilePath, bitmap);
                        } else {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageFileDidFailedLoad, remoteLocalFilePath);
                        }
                    }
                });
            }

            public void onError(int error, String msg) {
                loadingFiles.remove(remoteLocalFilePath);
                File file = new File(remoteLocalFilePath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageFileDidFailedLoad, remoteLocalFilePath);
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                loadingFiles.put(remoteLocalFilePath, progress);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageFileLoadProgressChanged, remoteLocalFilePath, progress);
                    }
                });

            }
        };
        Map<String, String> headers = new HashMap<>();
        if (!TextUtils.isEmpty(secret)) {
            headers.put("share-secret", secret);
        }
        EMChatManager.getInstance().downloadFile(remoteFilePath, remoteLocalFilePath, headers, callback);
    }


    public static String getImagePath(String remoteUrl) {
        String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
        EMLog.d("msg", "image path:" + path);
        return path;

    }


    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }


    private String getLocalFilePath(String remoteUrl) {
        String localPath;
        if (remoteUrl.contains("/")) {
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
                    + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
        } else {
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
        }
        return localPath;
    }

    public int getFileProgress(String path) {
        if (loadingFiles.containsKey(path)) {
            return loadingFiles.get(path);
        }
        return 0;
    }

    public String getPathToMessage(MessageObject messageObject) {
        ImageMessageBody imgBody = (ImageMessageBody) messageObject.messageOwner.getBody();
        final String localFilePath;
        if (imgBody == null || imgBody.getLocalUrl() == null) {
            return null;
        }
        if(messageObject.isOut()){
            localFilePath=imgBody.getLocalUrl();
        }else{
            String remotePath = imgBody.getRemoteUrl();
            localFilePath = getImagePath(remotePath);
        }
        return localFilePath;
    }

    public File getFileToMessage(MessageObject messageObject) {
        ImageMessageBody imgBody = (ImageMessageBody) messageObject.messageOwner.getBody();
        final String localFilePath;
        if (imgBody == null || imgBody.getLocalUrl() == null) {
            return null;
        }
        if(messageObject.isOut()){
            localFilePath=imgBody.getLocalUrl();
        }else{
            String remotePath = imgBody.getRemoteUrl();
            localFilePath = getImagePath(remotePath);
        }
        return new File(localFilePath);
    }
}
