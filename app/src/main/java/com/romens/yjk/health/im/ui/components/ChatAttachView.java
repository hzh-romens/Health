package com.romens.yjk.health.im.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.im.ui.IMChatActivity;
import com.romens.yjk.health.ui.components.AttachView.AttachButton;

import java.util.ArrayList;

public class ChatAttachView extends FrameLayout{

    public interface ChatAttachViewDelegate {
        void didPressedButton(int button);
    }
    private AttachButton sendPhotosButton;
    private View views[] = new View[20];

    private float[] distCache = new float[20];

    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    private boolean loading;

    private ChatAttachViewDelegate delegate;

    private IMChatActivity baseFragment;

    public ChatAttachView(Context context) {
        super(context);
        CharSequence[] items = new CharSequence[]{
                "拍照",
                "相册",
                "视频",
                "音频",
                "文件",
                "名片",
                "位置",
                ""
        };
        int itemIcons[] = new int[]{
                R.drawable.attach_camera_states,
                R.drawable.attach_gallery_states,
                R.drawable.attach_video_states,
                R.drawable.attach_audio_states,
                R.drawable.attach_file_states,
                R.drawable.attach_contact_states,
                R.drawable.attach_location_states,
                R.drawable.attach_hide_states,
        };
        for (int a = 0; a < 8; a++) {
            AttachButton attachButton = new AttachButton(context);
            attachButton.setTextAndIcon(items[a], itemIcons[a]);
            addView(attachButton, LayoutHelper.createFrame(85, 90, Gravity.LEFT | Gravity.TOP));
            attachButton.setTag(a);
            views[a] = attachButton;
            if (a == 7) {
                sendPhotosButton = attachButton;
                sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4), 0, 0);
            }
            attachButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delegate != null) {
                        delegate.didPressedButton((Integer) v.getTag());
                    }
                }
            });
        }
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(294), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;

        int t = AndroidUtilities.dp(8);
        int diff = (width - AndroidUtilities.dp(85 * 4 + 20)) / 3;
        for (int a = 0; a < 8; a++) {
            int y = AndroidUtilities.dp(t + 95 * (a / 4));
            int x = AndroidUtilities.dp(10) + (a % 4) * (AndroidUtilities.dp(85) + diff);
            views[a].layout(x, y, x + views[a].getMeasuredWidth(), y + views[a].getMeasuredHeight());
        }
    }

    public void updatePhotosButton() {
        sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4), 0, 0);
        sendPhotosButton.imageView.setBackgroundResource(R.drawable.attach_hide_states);
        sendPhotosButton.imageView.setImageResource(R.drawable.attach_hide2);
        sendPhotosButton.textView.setText("");
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        delegate = chatAttachViewDelegate;
    }

    @SuppressLint("NewApi")
    public void onRevealAnimationStart(boolean open) {
        if (!open) {
            return;
        }
        int count = Build.VERSION.SDK_INT <= 19 ? 11 : 8;
        for (int a = 0; a < count; a++) {
            if (Build.VERSION.SDK_INT <= 19) {
                if (a < 8) {
                    views[a].setScaleX(0.1f);
                    views[a].setScaleY(0.1f);
                }
                views[a].setAlpha(0.0f);
            } else {
                views[a].setScaleX(0.7f);
                views[a].setScaleY(0.7f);
            }
            views[a].setTag(R.string.app_name, null);
            distCache[a] = 0;
        }
    }

    @SuppressLint("NewApi")
    public void onRevealAnimationProgress(boolean open, float radius, int x, int y) {
        if (!open) {
            return;
        }
        int count = Build.VERSION.SDK_INT <= 19 ? 11 : 8;
        for (int a = 0; a < count; a++) {
            if (views[a].getTag(R.string.app_name) == null) {
                if (distCache[a] == 0) {
                    int buttonX = views[a].getLeft() + views[a].getMeasuredWidth() / 2;
                    int buttonY = views[a].getTop() + views[a].getMeasuredHeight() / 2;
                    distCache[a] = (float) Math.sqrt((x - buttonX) * (x - buttonX) + (y - buttonY) * (y - buttonY));
                    float vecX = (x - buttonX) / distCache[a];
                    float vecY = (y - buttonY) / distCache[a];
                    views[a].setPivotX(views[a].getMeasuredWidth() / 2 + vecX * AndroidUtilities.dp(20));
                    views[a].setPivotY(views[a].getMeasuredHeight() / 2 + vecY * AndroidUtilities.dp(20));
                }
                if (distCache[a] > radius + AndroidUtilities.dp(27)) {
                    continue;
                }

                views[a].setTag(R.string.app_name, 1);
                final ArrayList<Animator> animators = new ArrayList<>();
                final ArrayList<Animator> animators2 = new ArrayList<>();
                if (a < 8) {
                    animators.add(ObjectAnimator.ofFloat(views[a], "scaleX", 0.7f, 1.05f));
                    animators.add(ObjectAnimator.ofFloat(views[a], "scaleY", 0.7f, 1.05f));
                    animators2.add(ObjectAnimator.ofFloat(views[a], "scaleX", 1.0f));
                    animators2.add(ObjectAnimator.ofFloat(views[a], "scaleY", 1.0f));
                }
                if (Build.VERSION.SDK_INT <= 19) {
                    animators.add(ObjectAnimator.ofFloat(views[a], "alpha", 1.0f));
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animators);
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(decelerateInterpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(animators2);
                        animatorSet.setDuration(100);
                        animatorSet.setInterpolator(decelerateInterpolator);
                        animatorSet.start();
                    }
                });
                animatorSet.start();
            }
        }
    }

    public void onRevealAnimationEnd(boolean open) {
        if (open && Build.VERSION.SDK_INT <= 19) {
        }
    }

    public void init(IMChatActivity parentFragment) {
        updatePhotosButton();
    }

    public void onDestroy() {
        baseFragment = null;
    }
}
