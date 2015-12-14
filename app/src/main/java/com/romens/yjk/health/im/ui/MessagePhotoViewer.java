package com.romens.yjk.health.im.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.core.ImageReceiver;
import com.romens.yjk.health.im.NotificationCenter;
import com.romens.android.io.FileHelper;
import com.romens.android.log.FileLog;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.AnimationCompat.AnimatorListenerAdapterProxy;
import com.romens.android.ui.AnimationCompat.AnimatorSetProxy;
import com.romens.android.ui.AnimationCompat.ObjectAnimatorProxy;
import com.romens.android.ui.AnimationCompat.ViewProxy;
import com.romens.android.ui.Components.CheckBox;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.IMMessagesController;
import com.romens.yjk.health.db.entity.MessageObject;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.MessageImageFileHelper;
import com.romens.yjk.health.im.MediaController;
import com.romens.yjk.health.ui.components.ClippingImageView;
import com.romens.yjk.health.ui.components.SizeNotifierFrameLayoutPhoto;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessagePhotoViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private PhotoViewerProvider placeProvider;
    private boolean isVisible;

    private Activity parentActivity;

    private ActionBar actionBar;
    private boolean isActionBarVisible = true;

    private static Drawable[] progressDrawables;

    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayoutDrawer containerView;
    private FrameLayoutTouchListener windowView;
    private ClippingImageView animatingImageView;
    private FrameLayout bottomLayout;
    private TextView nameTextView;
    private TextView dateTextView;
    private ActionBarMenuItem menuItem;
    private ImageView shareButton;
    private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(0xff000000);
    private CheckBox checkImageView;
    private RadialProgressView radialProgressViews;
    private AnimatorSetProxy currentActionBarAnimation;
    private AlertDialog visibleDialog;
    private TextView captionTextView;
    private TextView captionTextViewOld;
    private TextView captionTextViewNew;
    private boolean canShowBottom = true;
    private int sendPhotoType = 0;
    private boolean needCaptionLayout;

    private float animationValues[][] = new float[2][8];

    private IMChatActivity parentChatActivity;

    private int animationInProgress = 0;
    private long transitionAnimationStartTime = 0;
    private Runnable animationEndRunnable = null;
    private PlaceProviderObject showAfterAnimation;
    private PlaceProviderObject hideAfterAnimation;
    private boolean disableShowCheck = false;

    private String lastTitle;

    private ImageReceiver centerImage = new ImageReceiver();
    private MessageObject currentMessageObject;
    private String currentFileNames;
    private PlaceProviderObject currentPlaceObject;
    private String currentPathObject;
    private Bitmap currentThumb = null;

    private int avatarsUserId;
    private long currentDialogId;

    private boolean draggingDown = false;
    private float dragY;
    private float translationX = 0;
    private float translationY = 0;
    private float scale = 1;
    private float animateToX;
    private float animateToY;
    private float animateToScale;
    private float animationValue;
    private long animationStartTime;
    private AnimatorSetProxy imageMoveAnimation;
    private AnimatorSetProxy changeModeAnimation;
    private GestureDetector gestureDetector;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private float pinchStartDistance = 0;
    private float pinchStartScale = 1;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartX;
    private float pinchStartY;
    private float moveStartX;
    private float moveStartY;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private boolean canZoom = true;
    private boolean zooming = false;
    private boolean moving = false;
    private boolean doubleTap = false;
    private boolean invalidCoords = false;
    private boolean canDragDown = true;
    private boolean zoomAnimation = false;
    private boolean discardTap = false;
    private int switchImageAfterAnimation = 0;
    private VelocityTracker velocityTracker = null;
    private Scroller scroller = null;

    private ArrayList<MessageObject> imagesArrTemp = new ArrayList<>();
    private HashMap<Integer, MessageObject> imagesByIdsTemp = new HashMap<>();
    private HashMap<Integer, MessageObject> imagesByIds = new HashMap<>();

    private final static int gallery_menu_save = 1;
    private final static int gallery_menu_showall = 2;
    private final static int gallery_menu_send = 3;
    private final static int gallery_menu_crop = 4;
    private final static int gallery_menu_delete = 6;
    private final static int gallery_menu_tune = 7;
    private final static int gallery_menu_caption = 8;
    private final static int gallery_menu_caption_done = 9;

    private final static int PAGE_SPACING = AndroidUtilities.dp(30);

    private static DecelerateInterpolator decelerateInterpolator = null;
    private static Paint progressPaint = null;

    private class BackgroundDrawable extends ColorDrawable {

        private Runnable drawRunnable;

        public BackgroundDrawable(int color) {
            super(color);
        }

        @Override
        public void setAlpha(int alpha) {
            super.setAlpha(alpha);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != 0) {
                if (drawRunnable != null) {
                    drawRunnable.run();
                    drawRunnable = null;
                }
            }
        }
    }

    private class RadialProgressView {

        private long lastUpdateTime = 0;
        private float radOffset = 0;
        private float currentProgress = 0;
        private float animationProgressStart = 0;
        private long currentProgressTime = 0;
        private float animatedProgressValue = 0;
        private RectF progressRect = new RectF();
        private int backgroundState = -1;
        private View parent = null;
        private int size = AndroidUtilities.dp(64);
        private int previousBackgroundState = -2;
        private float animatedAlphaValue = 1.0f;
        private float alpha = 1.0f;
        private float scale = 1.0f;

        public RadialProgressView(Context context, View parentView) {
            if (decelerateInterpolator == null) {
                decelerateInterpolator = new DecelerateInterpolator(1.5f);
                progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                progressPaint.setStyle(Paint.Style.STROKE);
                progressPaint.setStrokeCap(Paint.Cap.ROUND);
                progressPaint.setStrokeWidth(AndroidUtilities.dp(2));
                progressPaint.setColor(0xffffffff);
            }
            parent = parentView;
        }

        private void updateAnimation() {
            long newTime = System.currentTimeMillis();
            long dt = newTime - lastUpdateTime;
            lastUpdateTime = newTime;

            if (animatedProgressValue != 1) {
                radOffset += 360 * dt / 3000.0f;
                float progressDiff = currentProgress - animationProgressStart;
                if (progressDiff > 0) {
                    currentProgressTime += dt;
                    if (currentProgressTime >= 300) {
                        animatedProgressValue = currentProgress;
                        animationProgressStart = currentProgress;
                        currentProgressTime = 0;
                    } else {
                        animatedProgressValue = animationProgressStart + progressDiff * decelerateInterpolator.getInterpolation(currentProgressTime / 300.0f);
                    }
                }
                parent.invalidate();
            }
            if (animatedProgressValue >= 1 && previousBackgroundState != -2) {
                animatedAlphaValue -= dt / 200.0f;
                if (animatedAlphaValue <= 0) {
                    animatedAlphaValue = 0.0f;
                    previousBackgroundState = -2;
                }
                parent.invalidate();
            }
        }

        public void setProgress(float value, boolean animated) {
            if (!animated) {
                animatedProgressValue = value;
                animationProgressStart = value;
            } else {
                animationProgressStart = animatedProgressValue;
            }
            currentProgress = value;
            currentProgressTime = 0;
        }

        public void setBackgroundState(int state, boolean animated) {
            lastUpdateTime = System.currentTimeMillis();
            if (animated && backgroundState != state) {
                previousBackgroundState = backgroundState;
                animatedAlphaValue = 1.0f;
            } else {
                previousBackgroundState = -2;
            }
            backgroundState = state;
            parent.invalidate();
        }

        public void setAlpha(float value) {
            alpha = value;
        }

        public void setScale(float value) {
            scale = value;
        }

        public void onDraw(Canvas canvas) {
            int sizeScaled = (int) (size * scale);
            int x = (getContainerViewWidth() - sizeScaled) / 2;
            int y = (getContainerViewHeight() - sizeScaled) / 2;

            if (previousBackgroundState >= 0 && previousBackgroundState < 4) {
                Drawable drawable = progressDrawables[previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) (255 * animatedAlphaValue * alpha));
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }

            if (backgroundState >= 0 && backgroundState < 4) {
                Drawable drawable = progressDrawables[backgroundState];
                if (drawable != null) {
                    if (previousBackgroundState != -2) {
                        drawable.setAlpha((int) (255 * (1.0f - animatedAlphaValue) * alpha));
                    } else {
                        drawable.setAlpha((int) (255 * alpha));
                    }
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }

            if (backgroundState == 0 || backgroundState == 1 || previousBackgroundState == 0 || previousBackgroundState == 1) {
                int diff = AndroidUtilities.dp(1);
                if (previousBackgroundState != -2) {
                    progressPaint.setAlpha((int) (255 * animatedAlphaValue * alpha));
                } else {
                    progressPaint.setAlpha((int) (255 * alpha));
                }
                progressRect.set(x + diff, y + diff, x + sizeScaled - diff, y + sizeScaled - diff);
                canvas.drawArc(progressRect, -90 + radOffset, Math.max(4, 360 * animatedProgressValue), false, progressPaint);
                updateAnimation();
            }
        }
    }

    public static class PlaceProviderObject {
        public ImageReceiver imageReceiver;
        public int viewX;
        public int viewY;
        public View parentView;
        public Bitmap thumb;
        public int user_id;
        public int index;
        public int size;
        public int radius;
    }

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        @Override
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject) {
            return null;
        }

        @Override
        public Bitmap getThumbForPhoto(MessageObject messageObject) {
            return null;
        }

        @Override
        public void willSwitchFromPhoto(MessageObject messageObject) {

        }

        @Override
        public void willHidePhotoViewer() {

        }

        @Override
        public boolean isPhotoChecked() {
            return false;
        }

        @Override
        public void setPhotoChecked() {

        }

        @Override
        public void cancelButtonPressed() {

        }

        @Override
        public void sendButtonPressed() {

        }

        @Override
        public int getSelectedCount() {
            return 0;
        }

        @Override
        public void updatePhotoAtIndex() {

        }
    }

    public interface PhotoViewerProvider {
        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject);

        Bitmap getThumbForPhoto(MessageObject messageObject);

        void willSwitchFromPhoto(MessageObject messageObject);

        void willHidePhotoViewer();

        boolean isPhotoChecked();

        void setPhotoChecked();

        void cancelButtonPressed();

        void sendButtonPressed();

        int getSelectedCount();

        void updatePhotoAtIndex();
    }

    private class FrameLayoutTouchListener extends FrameLayout {

        private boolean attachedToWindow;
        private Runnable attachRunnable;

        public FrameLayoutTouchListener(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return getInstance().onTouchEvent(event);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            getInstance().onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            attachedToWindow = true;
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            attachedToWindow = false;
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            setMeasuredDimension(widthSize, heightSize);

            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            final int count = getChildCount();

            int paddingBottom = 0;

            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = Gravity.TOP | Gravity.LEFT;
                }

                final int absoluteGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = (r - l - width) / 2 + lp.leftMargin - lp.rightMargin;
                        break;
                    case Gravity.RIGHT:
                        childLeft = r - width - lp.rightMargin;
                        break;
                    case Gravity.LEFT:
                    default:
                        childLeft = lp.leftMargin;
                }

                switch (verticalGravity) {
                    case Gravity.TOP:
                        childTop = lp.topMargin;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = ((b - paddingBottom) - t - height) / 2 + lp.topMargin - lp.bottomMargin;
                        break;
                    case Gravity.BOTTOM:
                        childTop = ((b - paddingBottom) - t) - height - lp.bottomMargin;
                        break;
                    default:
                        childTop = lp.topMargin;
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }

            notifyHeightChanged();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            getInstance().onDraw(canvas);
        }
    }

    private static volatile MessagePhotoViewer Instance = null;

    public static MessagePhotoViewer getInstance() {
        MessagePhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (MessagePhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MessagePhotoViewer();
                }
            }
        }
        return localInstance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.messageFileDidFailedLoad) {
            String location = (String) args[0];
            if (currentFileNames != null && currentFileNames.equals(location)) {
                radialProgressViews.setProgress(1.0f, true);
                checkProgress(true);
            }
        } else if (id == NotificationCenter.messageFileDidLoaded) {
            String location = (String) args[0];
            if (currentFileNames != null && currentFileNames.equals(location)) {
                radialProgressViews.setProgress(1.0f, true);
                checkProgress(true);
                Bitmap image = (Bitmap) args[1];
                centerImage.setImageBitmap(image);
            }
        } else if (id == NotificationCenter.messageFileLoadProgressChanged) {
            String location = (String) args[0];
            if (currentFileNames != null && currentFileNames.equals(location)) {
                int progress = (int) args[1];
                radialProgressViews.setProgress(progress, true);
            }
        }
        //else if (key == NotificationCenter.userPhotosLoaded) {
//            int guid = (Integer) args[4];
//            int uid = (Integer) args[0];
//            if (avatarsUserId == uid && classGuid == guid) {
//                boolean fromCache = (Boolean) args[3];
//
//                int setToImage = -1;
//                ArrayList<TLRPC.Photo> photos = (ArrayList<TLRPC.Photo>) args[5];
//                if (photos.isEmpty()) {
//                    return;
//                }
//                imagesArrLocations.clear();
//                imagesArrLocationsSizes.clear();
//                avatarsArr.clear();
//                for (TLRPC.Photo photo : photos) {
//                    if (photo == null || photo instanceof TLRPC.TL_photoEmpty || photo.sizes == null) {
//                        continue;
//                    }
//                    TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
//                    if (sizeFull != null) {
//                        if (currentFileLocation != null) {
//                            for (TLRPC.PhotoSize size : photo.sizes) {
//                                if (size.location.local_id == currentFileLocation.local_id && size.location.volume_id == currentFileLocation.volume_id) {
//                                    setToImage = imagesArrLocations.size();
//                                    break;
//                                }
//                            }
//                        }
//                        imagesArrLocations.add(sizeFull.location);
//                        imagesArrLocationsSizes.add(sizeFull.size);
//                        avatarsArr.add(photo);
//                    }
//                }
//                if (!avatarsArr.isEmpty()) {
//                    menuItem.showSubItem(gallery_menu_delete);
//                } else {
//                    menuItem.hideSubItem(gallery_menu_delete);
//                }
//                needSearchImageInArr = false;
//                currentIndex = -1;
//                if (setToImage != -1) {
//                    setImageIndex(setToImage, true);
//                } else {
//                    avatarsArr.add(0, new TLRPC.TL_photoEmpty());
//                    imagesArrLocations.add(0, currentFileLocation);
//                    imagesArrLocationsSizes.add(0, 0);
//                    setImageIndex(0, true);
//                }
//                if (fromCache) {
//                    //MessagesController.getInstance().loadUserPhotos(avatarsUserId, 0, 80, 0, false, classGuid);
//                }
//            }
//        } else if (key == NotificationCenter.mediaCountDidLoaded) {
//            long uid = (Long) args[0];
//            if (uid == currentDialogId) {
//                if ((int) currentDialogId != 0 && (Boolean) args[2]) {
//                    SharedMediaQuery.getMediaCount(currentDialogId, SharedMediaQuery.MEDIA_PHOTOVIDEO, classGuid, false);
//                }
//                totalImagesCount = (Integer) args[1];
//                if (needSearchImageInArr && isFirstLoading) {
//                    isFirstLoading = false;
//                    loadingMoreImages = true;
//                    SharedMediaQuery.loadMedia(currentDialogId, 0, 100, 0, SharedMediaQuery.MEDIA_PHOTOVIDEO, true, classGuid);
//                } else if (!imagesArr.isEmpty()) {
//                    if (opennedFromMedia) {
//                        actionBar.setTitle(String.format("%1$d / %2$d", currentIndex + 1, totalImagesCount));
//                    } else {
//                        actionBar.setTitle(String.format("%1$d / %2$d", (totalImagesCount - imagesArr.size()) + currentIndex + 1, totalImagesCount));
//                    }
//                }
//            }
//        } else if (key == NotificationCenter.mediaDidLoaded) {
//            long uid = (Long) args[0];
//            int guid = (Integer) args[4];
//            if (uid == currentDialogId && guid == classGuid) {
//                loadingMoreImages = false;
//                ArrayList<MessageObject> arr = (ArrayList<MessageObject>) args[2];
//                boolean fromCache = (Boolean) args[3];
//                cacheEndReached = !fromCache;
//                if (needSearchImageInArr) {
//                    if (arr.isEmpty()) {
//                        needSearchImageInArr = false;
//                        return;
//                    }
//                    int foundIndex = -1;
//
//                    MessageObject currentMessage = imagesArr.get(currentIndex);
//
//                    int added = 0;
//                    for (MessageObject message : arr) {
//                        if (!imagesByIdsTemp.containsKey(message.getId())) {
//                            imagesByIdsTemp.put(message.getId(), message);
//                            if (opennedFromMedia) {
//                                imagesArrTemp.add(message);
//                                if (message.getId() == currentMessage.getId()) {
//                                    foundIndex = added;
//                                }
//                                added++;
//                            } else {
//                                added++;
//                                imagesArrTemp.add(0, message);
//                                if (message.getId() == currentMessage.getId()) {
//                                    foundIndex = arr.size() - added;
//                                }
//                            }
//                        }
//                    }
//                    if (added == 0) {
//                        totalImagesCount = imagesArr.size();
//                    }
//
//                    if (foundIndex != -1) {
//                        imagesArr.clear();
//                        imagesArr.addAll(imagesArrTemp);
//                        imagesByIds.clear();
//                        imagesByIds.putAll(imagesByIdsTemp);
//                        imagesArrTemp.clear();
//                        imagesByIdsTemp.clear();
//                        needSearchImageInArr = false;
//                        currentIndex = -1;
//                        if (foundIndex >= imagesArr.size()) {
//                            foundIndex = imagesArr.size() - 1;
//                        }
//                        setImageIndex(foundIndex, true);
//                    } else {
//                        if (!cacheEndReached || !arr.isEmpty() && added != 0) {
//                            loadingMoreImages = true;
//                            if (opennedFromMedia) {
//                                SharedMediaQuery.loadMedia(currentDialogId, 0, 100, imagesArrTemp.get(imagesArrTemp.size() - 1).getId(), SharedMediaQuery.MEDIA_PHOTOVIDEO, true, classGuid);
//                            } else {
//                                SharedMediaQuery.loadMedia(currentDialogId, 0, 100, imagesArrTemp.get(0).getId(), SharedMediaQuery.MEDIA_PHOTOVIDEO, true, classGuid);
//                            }
//                        }
//                    }
//                } else {
//                    int added = 0;
//                    for (MessageObject message : arr) {
//                        if (!imagesByIds.containsKey(message.getId())) {
//                            added++;
//                            if (opennedFromMedia) {
//                                imagesArr.add(message);
//                            } else {
//                                imagesArr.add(0, message);
//                            }
//                            imagesByIds.put(message.getId(), message);
//                        }
//                    }
//                    if (arr.isEmpty() && !fromCache) {
//                        totalImagesCount = arr.size();
//                    }
//                    if (opennedFromMedia) {
//                        if (added == 0) {
//                            totalImagesCount = imagesArr.size();
//                        }
//                    } else {
//                        if (added != 0) {
//                            int index = currentIndex;
//                            currentIndex = -1;
//                            setImageIndex(index + added, true);
//                        } else {
//                            totalImagesCount = imagesArr.size();
//                        }
//                    }
//                }
//            }
//        } else if (key == NotificationCenter.emojiDidLoaded) {
//            if (captionTextView != null) {
//                captionTextView.invalidate();
//            }
//        }
    }

    public void setParentActivity(final Activity activity) {
        if (parentActivity == activity) {
            return;
        }
        parentActivity = activity;

        if (progressDrawables == null) {
            progressDrawables = new Drawable[4];
            progressDrawables[0] = parentActivity.getResources().getDrawable(R.drawable.circle_big);
            progressDrawables[1] = parentActivity.getResources().getDrawable(R.drawable.cancel_big);
            progressDrawables[2] = parentActivity.getResources().getDrawable(R.drawable.load_big);
            progressDrawables[3] = parentActivity.getResources().getDrawable(R.drawable.play_big);
        }

        scroller = new Scroller(activity);

        windowView = new FrameLayoutTouchListener(activity);
//        {
//            @Override
//            public boolean dispatchKeyEventPreIme(KeyEvent event) {
//                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//                    MessagePhotoViewer.getInstance().closePhoto(true, false);
//                    return true;
//                }
//                return super.dispatchKeyEventPreIme(event);
//            }
//        };
        windowView.setBackgroundDrawable(backgroundDrawable);
        windowView.setFocusable(false);

        animatingImageView = new ClippingImageView(activity);
        animatingImageView.setAnimationValues(animationValues);
        windowView.addView(animatingImageView, LayoutHelper.createFrame(40, 40));

        containerView = new FrameLayoutDrawer(activity);
        containerView.setFocusable(false);
        windowView.addView(containerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.format = PixelFormat.TRANSLUCENT;
        windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.gravity = Gravity.TOP;
        windowLayoutParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        actionBar = new ActionBar(activity);
        actionBar.setBackgroundColor(0x7F000000);
        actionBar.setOccupyStatusBar(false);
        actionBar.setItemsBackground(R.drawable.bar_selector_white);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(String.format("%1$d / %2$d", 1, 1));
        containerView.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    closePhoto(true, false);
                } else if (id == gallery_menu_save) {
                    File f = null;
                    if (currentMessageObject != null) {
                        //f = FileLoader.getPathToMessage(currentMessageObject.messageOwner);
                    }
                    if (f != null && f.exists()) {
                        MediaController.saveFile(f.toString(), parentActivity, currentFileNames.endsWith("mp4") ? 1 : 0, null);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                        builder.setTitle(parentActivity.getString(R.string.app_name));
                        builder.setPositiveButton("确定", null);
                        builder.setMessage("请下载这个文件");
                        showAlertDialog(builder);
                    }
                } else if (id == gallery_menu_send) {
                    /*Intent intent = new Intent(this, MessagesActivity.class);
                    intent.putExtra("onlySelect", true);
                    startActivityForResult(intent, 10);
                    if (requestCode == 10) {
                        int chatId = data.getIntExtra("chatId", 0);
                        int userId = data.getIntExtra("userId", 0);
                        int dialog_id = 0;
                        if (chatId != 0) {
                            dialog_id = -chatId;
                        } else if (userId != 0) {
                            dialog_id = userId;
                        }
                        TLRPC.FileLocation location = getCurrentFile();
                        if (dialog_id != 0 && location != null) {
                            Intent intent = new Intent(GalleryImageViewer.this, ChatActivity.class);
                            if (chatId != 0) {
                                intent.putExtra("chatId", chatId);
                            } else {
                                intent.putExtra("userId", userId);
                            }
                            startActivity(intent);
                            NotificationCenter.getInstance().postNotificationName(MessagesController.closeChats);
                            finish();
                            if (withoutBottom) {
                                MessagesController.getInstance().sendMessage(location, dialog_id);
                            } else {
                                int item = mViewPager.getCurrentItem();
                                MessageObject obj = localPagerAdapter.imagesArr.get(item);
                                MessagesController.getInstance().sendMessage(obj, dialog_id);
                            }
                        }
                    }*/
                } else if (id == gallery_menu_crop) {
                } else if (id == gallery_menu_tune) {
                } else if (id == gallery_menu_delete) {
                    if (parentActivity == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                    if (currentFileNames != null && currentFileNames.endsWith("mp4")) {
                        builder.setMessage("是否确定删除这个视频?");
                    } else {
                        builder.setMessage("是否确定删除这个图片?");
                    }
                    builder.setTitle(parentActivity.getString(R.string.app_name));
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            if (!imagesArr.isEmpty()) {
//                                if (currentIndex < 0 || currentIndex >= imagesArr.size()) {
//                                    return;
//                                }
//                                MessageObject obj = imagesArr.get(currentIndex);
//                                if (obj.isSent()) {
//                                    ArrayList<Integer> arr = new ArrayList<>();
//                                    arr.add(obj.getId());
//
//                                    ArrayList<Long> random_ids = null;
//                                    TLRPC.EncryptedChat encryptedChat = null;
//                                    if ((int) obj.getDialogId() == 0 && obj.messageOwner.random_id != 0) {
//                                        random_ids = new ArrayList<>();
//                                        random_ids.add(obj.messageOwner.random_id);
//                                        encryptedChat = MessagesController.getInstance().getEncryptedChat((int) (obj.getDialogId() >> 32));
//                                    }
//
//                                    MessagesController.getInstance().deleteMessages(arr, random_ids, encryptedChat);
//                                    closePhoto(false, false);
//                                }
//                            } else if (!avatarsArr.isEmpty()) {
//                                if (currentIndex < 0 || currentIndex >= avatarsArr.size()) {
//                                    return;
//                                }
//                                TLRPC.Photo photo = avatarsArr.get(currentIndex);
//                                TLRPC.FileLocation currentLocation = imagesArrLocations.get(currentIndex);
//                                if (photo instanceof TLRPC.TL_photoEmpty) {
//                                    photo = null;
//                                }
//                                boolean current = false;
//                                if (currentUserAvatarLocation != null) {
//                                    if (photo != null) {
//                                        for (TLRPC.PhotoSize size : photo.sizes) {
//                                            if (size.location.local_id == currentUserAvatarLocation.local_id && size.location.volume_id == currentUserAvatarLocation.volume_id) {
//                                                current = true;
//                                                break;
//                                            }
//                                        }
//                                    } else if (currentLocation.local_id == currentUserAvatarLocation.local_id && currentLocation.volume_id == currentUserAvatarLocation.volume_id) {
//                                        current = true;
//                                    }
//                                }
//                                if (current) {
//                                    MessagesController.getInstance().deleteUserPhoto(null);
//                                    closePhoto(false, false);
//                                } else if (photo != null) {
//                                    TLRPC.TL_inputPhoto inputPhoto = new TLRPC.TL_inputPhoto();
//                                    inputPhoto.key = photo.key;
//                                    inputPhoto.access_hash = photo.access_hash;
//                                    MessagesController.getInstance().deleteUserPhoto(inputPhoto);
//                                    MessagesStorage.getInstance().clearUserPhoto(avatarsUserId, photo.key);
//                                    imagesArrLocations.remove(currentIndex);
//                                    imagesArrLocationsSizes.remove(currentIndex);
//                                    avatarsArr.remove(currentIndex);
//                                    if (imagesArrLocations.isEmpty()) {
//                                        closePhoto(false, false);
//                                    } else {
//                                        int index = currentIndex;
//                                        if (index >= avatarsArr.size()) {
//                                            index = avatarsArr.size() - 1;
//                                        }
//                                        currentIndex = -1;
//                                        setImageIndex(index, true);
//                                    }
//                                }
//                            }
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    showAlertDialog(builder);
                } else if (id == gallery_menu_caption) {
                    if (imageMoveAnimation != null || changeModeAnimation != null) {
                        return;
                    }
                    checkImageView.setVisibility(View.GONE);
                    captionTextView.clearAnimation();
                    captionTextView.setVisibility(View.INVISIBLE);
                    lastTitle = actionBar.getTitle();
                    actionBar.setTitle("图片说明");
                }
            }

            @Override
            public boolean canOpenMenu() {
//                if (currentMessageObject != null) {
//                    File f = FileLoader.getPathToMessage(currentMessageObject.messageOwner);
//                    if (f.exists()) {
//                        return true;
//                    }
//                } else if (currentFileLocation != null) {
//                    File f = FileLoader.getPathToAttach(currentFileLocation, avatarsUserId != 0);
//                    if (f.exists()) {
//                        return true;
//                    }
//                }
                return false;
            }
        });

        ActionBarMenu menu = actionBar.createMenu();

        menuItem = menu.addItem(0, R.drawable.ic_ab_other);
        menuItem.addSubItem(gallery_menu_showall, "显示所有媒体", 0);
        menuItem.addSubItem(gallery_menu_save, "保存到相册", 0);
        menuItem.addSubItem(gallery_menu_delete, "删除", 0);

        bottomLayout = new FrameLayout(parentActivity);
        bottomLayout.setBackgroundColor(0x7f000000);
        containerView.addView(bottomLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM | Gravity.LEFT));

        captionTextViewOld = new TextView(parentActivity);
        captionTextViewOld.setMaxLines(10);
        captionTextViewOld.setBackgroundColor(0x7f000000);
        captionTextViewOld.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(8), AndroidUtilities.dp(16), AndroidUtilities.dp(8));
        captionTextViewOld.setLinkTextColor(0xffffffff);
        captionTextViewOld.setTextColor(0xffffffff);
        captionTextViewOld.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        captionTextViewOld.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        captionTextViewOld.setVisibility(View.INVISIBLE);
        containerView.addView(captionTextViewOld, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 0, 48));

        captionTextView = captionTextViewNew = new TextView(parentActivity);
        captionTextViewNew.setMaxLines(10);
        captionTextViewNew.setBackgroundColor(0x7f000000);
        captionTextViewNew.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(8), AndroidUtilities.dp(16), AndroidUtilities.dp(8));
        captionTextViewNew.setLinkTextColor(0xffffffff);
        captionTextViewNew.setTextColor(0xffffffff);
        captionTextViewNew.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        captionTextViewNew.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        captionTextViewNew.setVisibility(View.INVISIBLE);
        containerView.addView(captionTextViewNew, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 0, 48));

        radialProgressViews = new RadialProgressView(containerView.getContext(), containerView);
        radialProgressViews.setBackgroundState(0, false);

        shareButton = new ImageView(containerView.getContext());
        shareButton.setImageResource(R.drawable.share);
        shareButton.setScaleType(ImageView.ScaleType.CENTER);
        shareButton.setBackgroundResource(R.drawable.bar_selector_white);
        bottomLayout.addView(shareButton, LayoutHelper.createFrame(50, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.RIGHT));
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentActivity == null) {
                    return;
                }
                try {
                    File f = null;

//                    if (currentMessageObject != null) {
//                        f = FileLoader.getPathToMessage(currentMessageObject.messageOwner);
//                    } else if (currentFileLocation != null) {
//                        f = FileLoader.getPathToAttach(currentFileLocation, avatarsUserId != 0);
//                    }

                    if (f.exists()) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        if (f.toString().endsWith("mp4")) {
                            intent.setType("video/mp4");
                        } else {
                            intent.setType("image/jpeg");
                        }
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

                        parentActivity.startActivityForResult(Intent.createChooser(intent, "分享"), 500);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                        builder.setTitle(parentActivity.getString(R.string.app_name));
                        builder.setPositiveButton("确定", null);
                        builder.setMessage("请先下载这个文件");
                        showAlertDialog(builder);
                    }
                } catch (Exception e) {
                    FileLog.e("romens", e);
                }
            }
        });

        nameTextView = new TextView(containerView.getContext());
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setSingleLine(true);
        nameTextView.setMaxLines(1);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setTextColor(0xffffffff);
        nameTextView.setGravity(Gravity.LEFT);
        bottomLayout.addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 16, 5, 60, 0));

        dateTextView = new TextView(containerView.getContext());
        dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        dateTextView.setSingleLine(true);
        dateTextView.setMaxLines(1);
        dateTextView.setEllipsize(TextUtils.TruncateAt.END);
        dateTextView.setTextColor(0xffffffff);
        dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        dateTextView.setGravity(Gravity.LEFT);
        bottomLayout.addView(dateTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 16, 25, 50, 0));

        gestureDetector = new GestureDetector(containerView.getContext(), this);
        gestureDetector.setOnDoubleTapListener(this);

        centerImage.setParentView(containerView);
        centerImage.setCrossfadeAlpha((byte) 2);
        centerImage.setInvalidateAll(true);

        WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService(Activity.WINDOW_SERVICE);
        int rotation = manager.getDefaultDisplay().getRotation();

        checkImageView = new CheckBox(containerView.getContext(), R.drawable.selectphoto_large);
        checkImageView.setDrawBackground(true);
        checkImageView.setSize(45);
        checkImageView.setCheckOffset(AndroidUtilities.dp(1));
        checkImageView.setColor(0xff3ccaef);
        checkImageView.setVisibility(View.GONE);
        containerView.addView(checkImageView, LayoutHelper.createFrame(45, 45, Gravity.RIGHT | Gravity.TOP, 0, rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_90 ? 58 : 68, 10, 0));
        checkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeProvider != null) {
//                    placeProvider.setPhotoChecked(currentIndex);
//                    checkImageView.setChecked(placeProvider.isPhotoChecked(currentIndex), true);
                }
            }
        });
    }


    private void showAlertDialog(AlertDialog.Builder builder) {
        if (parentActivity == null) {
            return;
        }
        try {
            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        try {
            visibleDialog = builder.show();
            visibleDialog.setCanceledOnTouchOutside(true);
            visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    visibleDialog = null;
                }
            });
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    private void toggleCheckImageView(boolean show) {
        AnimatorSetProxy animatorSet = new AnimatorSetProxy();
        ArrayList<Object> arrayList = new ArrayList<>();
        if (needCaptionLayout) {
            arrayList.add(ObjectAnimatorProxy.ofFloat(captionTextView, "alpha", show ? 1.0f : 0.0f));
        }
        if (sendPhotoType == 0) {
            arrayList.add(ObjectAnimatorProxy.ofFloat(checkImageView, "alpha", show ? 1.0f : 0.0f));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void toggleActionBar(boolean show, final boolean animated) {
        if (show) {
            actionBar.setVisibility(View.VISIBLE);
            if (canShowBottom) {
                bottomLayout.setVisibility(View.VISIBLE);
                if (captionTextView.getTag() != null) {
                    captionTextView.setVisibility(View.VISIBLE);
                }
            }
        }
        isActionBarVisible = show;
        actionBar.setEnabled(show);
        bottomLayout.setEnabled(show);

        if (animated) {
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(ObjectAnimatorProxy.ofFloat(actionBar, "alpha", show ? 1.0f : 0.0f));
            arrayList.add(ObjectAnimatorProxy.ofFloat(bottomLayout, "alpha", show ? 1.0f : 0.0f));
            if (captionTextView.getTag() != null) {
                arrayList.add(ObjectAnimatorProxy.ofFloat(captionTextView, "alpha", show ? 1.0f : 0.0f));
            }
            currentActionBarAnimation = new AnimatorSetProxy();
            currentActionBarAnimation.playTogether(arrayList);
            if (!show) {
                currentActionBarAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    @Override
                    public void onAnimationEnd(Object animation) {
                        if (currentActionBarAnimation.equals(animation)) {
                            actionBar.setVisibility(View.GONE);
                            if (canShowBottom) {
                                bottomLayout.clearAnimation();
                                bottomLayout.setVisibility(View.GONE);
                                if (captionTextView.getTag() != null) {
                                    captionTextView.clearAnimation();
                                    captionTextView.setVisibility(View.INVISIBLE);
                                }
                            }
                            currentActionBarAnimation = null;
                        }
                    }
                });
            }

            currentActionBarAnimation.setDuration(200);
            currentActionBarAnimation.start();
        } else {
            ViewProxy.setAlpha(actionBar, show ? 1.0f : 0.0f);
            ViewProxy.setAlpha(bottomLayout, show ? 1.0f : 0.0f);
            if (captionTextView.getTag() != null) {
                ViewProxy.setAlpha(captionTextView, show ? 1.0f : 0.0f);
            }
            if (!show) {
                actionBar.setVisibility(View.GONE);
                if (canShowBottom) {
                    bottomLayout.clearAnimation();
                    bottomLayout.setVisibility(View.GONE);
                    if (captionTextView.getTag() != null) {
                        captionTextView.clearAnimation();
                        captionTextView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    private void onPhotoShow(final MessageObject messageObject, final PlaceProviderObject object) {
        currentMessageObject = null;
        currentPathObject = null;
        currentFileNames = null;
        avatarsUserId = 0;
        currentDialogId = 0;
        needCaptionLayout = false;
        canShowBottom = true;
        imagesByIds.clear();
        imagesArrTemp.clear();
        imagesByIdsTemp.clear();
        containerView.setPadding(0, 0, 0, 0);
        currentThumb = object != null ? object.thumb : null;
        menuItem.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.GONE);
        menuItem.hideSubItem(gallery_menu_showall);
        ViewProxy.setTranslationY(actionBar, 0);
        ViewProxy.setAlpha(checkImageView, 1.0f);
        checkImageView.clearAnimation();
        checkImageView.setVisibility(View.GONE);
        captionTextView.setTag(null);
        captionTextView.clearAnimation();
        captionTextView.setVisibility(View.INVISIBLE);

        if (radialProgressViews != null) {
            radialProgressViews.setBackgroundState(-1, false);
        }

        if (messageObject != null) {
            menuItem.hideSubItem(gallery_menu_showall);
            setImageIndex(messageObject, true);
        }
    }

    private void setImages() {
        if (animationInProgress == 0) {
            setIndexToImage(centerImage, currentMessageObject);
        }
    }

    private void setImageIndex(MessageObject messageObject, boolean init) {
        if (!init) {
            currentThumb = null;
        }
        currentMessageObject = messageObject;
        currentFileNames = MessageImageFileHelper.getInstance().getPathToMessage(currentMessageObject);
        placeProvider.willSwitchFromPhoto(currentMessageObject);
        boolean sameImage = false;

        if (currentMessageObject != null) {
            menuItem.showSubItem(gallery_menu_delete);

            UserEntity user = IMMessagesController.getInstance().getUser(currentMessageObject.messageOwner.getFrom());
            if (user != null) {
                nameTextView.setText(user.getName());
            } else {
                nameTextView.setText("");
            }
            long date = currentMessageObject.messageOwner.getMsgTime();
            String dateString = String.format("%1$s / %2$s", new SimpleDateFormat("yyyy-MM-dd").format(new Date(date)), new SimpleDateFormat("HH:mm:ss").format(new Date(date)));
            if (currentFileNames != null && currentFileNames.endsWith("mp4")) {
                //dateTextView.setText(String.format("%s (%s)", dateString, AndroidUtilities.formatFileSize(currentMessageObject.messageOwner.media.video.size)));
            } else {
                dateTextView.setText(dateString);
            }
            CharSequence caption = currentMessageObject.caption;
            setCurrentCaption(caption);

            menuItem.showSubItem(gallery_menu_save);
            shareButton.setVisibility(View.VISIBLE);
        }


        if (currentPlaceObject != null) {
            if (animationInProgress == 0) {
                currentPlaceObject.imageReceiver.setVisible(true, true);
            } else {
                showAfterAnimation = currentPlaceObject;
            }
        }
        currentPlaceObject = placeProvider.getPlaceForPhoto(currentMessageObject);
        if (currentPlaceObject != null) {
            if (animationInProgress == 0) {
                currentPlaceObject.imageReceiver.setVisible(false, true);
            } else {
                hideAfterAnimation = currentPlaceObject;
            }
        }

        if (!sameImage) {
            draggingDown = false;
            translationX = 0;
            translationY = 0;
            scale = 1;
            animateToX = 0;
            animateToY = 0;
            animateToScale = 1;
            animationStartTime = 0;
            imageMoveAnimation = null;
            changeModeAnimation = null;

            pinchStartDistance = 0;
            pinchStartScale = 1;
            pinchCenterX = 0;
            pinchCenterY = 0;
            pinchStartX = 0;
            pinchStartY = 0;
            moveStartX = 0;
            moveStartY = 0;
            zooming = false;
            moving = false;
            doubleTap = false;
            invalidCoords = false;
            canDragDown = true;
            switchImageAfterAnimation = 0;
            canZoom = (currentFileNames != null && !currentFileNames.endsWith("mp4") && radialProgressViews.backgroundState != 0);
            updateMinMax(scale);
        }
        setImages();
        checkProgress(false);

    }

    private void setCurrentCaption(final CharSequence caption) {
        if (caption != null && caption.length() > 0) {
            captionTextView = captionTextViewOld;
            captionTextViewOld = captionTextViewNew;
            captionTextViewNew = captionTextView;

            CharSequence oldText = captionTextView.getText();
            captionTextView.setTag(caption);
            captionTextView.setText(caption);
            ViewProxy.setAlpha(captionTextView, bottomLayout.getVisibility() == View.VISIBLE ? 1.0f : 0.0f);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    captionTextViewOld.setTag(null);
                    captionTextViewOld.clearAnimation();
                    captionTextViewOld.setVisibility(View.INVISIBLE);
                    captionTextViewNew.setVisibility(bottomLayout.getVisibility() == View.VISIBLE ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            captionTextView.setTag(null);
            captionTextView.clearAnimation();
            captionTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void checkProgress(boolean animated) {
        if (currentFileNames != null) {
            File f = null;
            if (currentMessageObject != null) {
                f = MessageImageFileHelper.getInstance().getFileToMessage(currentMessageObject);
            } else if (currentPathObject != null) {
                f = new File(FileHelper.getInstance().getDirectory(FileHelper.MEDIA_DIR_CACHE), currentFileNames);
                if (!f.exists()) {
                    f = new File(FileHelper.getInstance().getDirectory(FileHelper.MEDIA_DIR_CACHE), currentFileNames);
                }
            }
            if (f != null && f.exists()) {
                radialProgressViews.setBackgroundState(-1, animated);
            } else {
                radialProgressViews.setBackgroundState(0, animated);
                int progress = MessageImageFileHelper.getInstance().getFileProgress(currentFileNames);
                radialProgressViews.setProgress(progress, false);
            }
            canZoom = true;
        } else {
            radialProgressViews.setBackgroundState(-1, animated);
        }
    }

    private void setIndexToImage(ImageReceiver imageReceiver, MessageObject messageObject) {
        imageReceiver.setOrientation(0, false);
        int size[] = new int[1];
        File f = MessageImageFileHelper.getInstance().getFileToMessage(messageObject);

        if (f != null) {
            //imageReceiver.setParentMessageObject(messageObject);
            if (messageObject != null) {
                imageReceiver.setShouldGenerateQualityThumb(true);
            }
            imageReceiver.setNeedsQualityThumb(false);
            Bitmap placeHolder = null;
            if (currentThumb != null) {
                placeHolder = currentThumb;
            }
            if (size[0] == 0) {
                size[0] = -1;
            }
            imageReceiver.setImage(f.getPath(), null, placeHolder != null ? new BitmapDrawable(null, placeHolder) : null);
        } else {
            imageReceiver.setNeedsQualityThumb(false);
            if (size[0] == 0) {
                imageReceiver.setImageBitmap((Bitmap) null);
            } else {
                imageReceiver.setImageBitmap(parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
            }
        }
        MessageImageFileHelper.getInstance().loadFile(messageObject);
    }

    public boolean isShowingImage(MessageObject object) {
        return isVisible && !disableShowCheck && object != null && currentMessageObject != null && currentMessageObject.getId() == object.getId();
    }

    public boolean isShowingImage(String object) {
        return isVisible && !disableShowCheck && object != null && currentPathObject != null && object.equals(currentPathObject);
    }

    public void openPhoto(final MessageObject messageObject, final PhotoViewerProvider provider) {
        openPhoto(messageObject, provider, null);
    }

    private boolean checkAnimation() {
        if (animationInProgress != 0) {
            if (Math.abs(transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
                if (animationEndRunnable != null) {
                    animationEndRunnable.run();
                    animationEndRunnable = null;
                }
                animationInProgress = 0;
            }
        }
        return animationInProgress != 0;
    }

    public void openPhoto(final MessageObject messageObject, final PhotoViewerProvider provider, IMChatActivity chatActivity) {
        if (parentActivity == null || isVisible || provider == null && checkAnimation() || messageObject == null) {
            return;
        }

        final PlaceProviderObject object = provider.getPlaceForPhoto(messageObject);
        if (object == null) {
            return;
        }

        WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
        if (windowView.attachedToWindow) {
            try {
                wm.removeView(windowView);
            } catch (Exception e) {
                //don't promt
            }
        }

        try {
            windowLayoutParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
            windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            windowLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED;
            windowView.setFocusable(false);
            containerView.setFocusable(false);
            wm.addView(windowView, windowLayoutParams);
        } catch (Exception e) {
            FileLog.e("tmessages", e);
            return;
        }

        parentChatActivity = chatActivity;

        actionBar.setTitle(String.format("%1$d / %2$d", 1, 1));
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageFileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageFileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageFileLoadProgressChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.userPhotosLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);

        placeProvider = provider;

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        isVisible = true;
        toggleActionBar(true, false);

        if (object != null) {
            disableShowCheck = true;
            animationInProgress = 1;
            onPhotoShow(messageObject, object);

            final Rect drawRegion = object.imageReceiver.getDrawRegion();
            int orientation = object.imageReceiver.getOrientation();

            animatingImageView.setVisibility(View.VISIBLE);
            animatingImageView.setRadius(object.radius);
            animatingImageView.setOrientation(orientation);
            animatingImageView.setNeedRadius(object.radius != 0);
            animatingImageView.setImageBitmap(object.thumb);

            ViewProxy.setAlpha(animatingImageView, 1.0f);
            ViewProxy.setPivotX(animatingImageView, 0.0f);
            ViewProxy.setPivotY(animatingImageView, 0.0f);
            ViewProxy.setScaleX(animatingImageView, 1.0f);
            ViewProxy.setScaleY(animatingImageView, 1.0f);
            ViewProxy.setTranslationX(animatingImageView, object.viewX + drawRegion.left);
            ViewProxy.setTranslationY(animatingImageView, object.viewY + drawRegion.top);
            final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
            layoutParams.width = drawRegion.right - drawRegion.left;
            layoutParams.height = drawRegion.bottom - drawRegion.top;
            animatingImageView.setLayoutParams(layoutParams);

            float scaleX = (float) AndroidUtilities.displaySize.x / layoutParams.width;
            float scaleY = (float) (AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight) / layoutParams.height;
            float scale = scaleX > scaleY ? scaleY : scaleX;
            float width = layoutParams.width * scale;
            float height = layoutParams.height * scale;
            float xPos = (AndroidUtilities.displaySize.x - width) / 2.0f;
            float yPos = (AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight - height) / 2.0f;
            int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
            int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());

            int coords2[] = new int[2];
            object.parentView.getLocationInWindow(coords2);
            int clipTop = coords2[1] - AndroidUtilities.statusBarHeight - (object.viewY + drawRegion.top);
            if (clipTop < 0) {
                clipTop = 0;
            }
            int clipBottom = (object.viewY + drawRegion.top + layoutParams.height) - (coords2[1] + object.parentView.getHeight() - AndroidUtilities.statusBarHeight);
            if (clipBottom < 0) {
                clipBottom = 0;
            }
            clipTop = Math.max(clipTop, clipVertical);
            clipBottom = Math.max(clipBottom, clipVertical);

            animationValues[0][0] = ViewProxy.getScaleX(animatingImageView);
            animationValues[0][1] = ViewProxy.getScaleY(animatingImageView);
            animationValues[0][2] = ViewProxy.getTranslationX(animatingImageView);
            animationValues[0][3] = ViewProxy.getTranslationY(animatingImageView);
            animationValues[0][4] = clipHorizontal;
            animationValues[0][5] = clipTop;
            animationValues[0][6] = clipBottom;
            animationValues[0][7] = animatingImageView.getRadius();

            animationValues[1][0] = scale;
            animationValues[1][1] = scale;
            animationValues[1][2] = xPos;
            animationValues[1][3] = yPos;
            animationValues[1][4] = 0;
            animationValues[1][5] = 0;
            animationValues[1][6] = 0;
            animationValues[1][7] = 0;

            animatingImageView.setAnimationProgress(0);
            backgroundDrawable.setAlpha(0);
            ViewProxy.setAlpha(containerView, 0);

            final AnimatorSetProxy animatorSet = new AnimatorSetProxy();
            animatorSet.playTogether(
                    ObjectAnimatorProxy.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f),
                    ObjectAnimatorProxy.ofInt(backgroundDrawable, "alpha", 0, 255),
                    ObjectAnimatorProxy.ofFloat(containerView, "alpha", 0.0f, 1.0f)
            );

            animationEndRunnable = new Runnable() {
                @Override
                public void run() {
                    if (containerView == null) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= 18) {
                        containerView.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                    animationInProgress = 0;
                    transitionAnimationStartTime = 0;
                    setImages();
                    containerView.invalidate();
                    animatingImageView.setVisibility(View.GONE);
                    if (showAfterAnimation != null) {
                        showAfterAnimation.imageReceiver.setVisible(true, true);
                    }
                    if (hideAfterAnimation != null) {
                        hideAfterAnimation.imageReceiver.setVisible(false, true);
                    }
                }
            };

            animatorSet.setDuration(200);
            animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                @Override
                public void onAnimationEnd(Object animation) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            NotificationCenter.getInstance().setAnimationInProgress(false);
                            if (animationEndRunnable != null) {
                                animationEndRunnable.run();
                                animationEndRunnable = null;
                            }
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Object animation) {
                    onAnimationEnd(animation);
                }
            });
            transitionAnimationStartTime = System.currentTimeMillis();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    NotificationCenter.getInstance().setAnimationInProgress(true);
                    animatorSet.start();
                }
            });
            if (Build.VERSION.SDK_INT >= 18) {
                containerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            backgroundDrawable.drawRunnable = new Runnable() {
                @Override
                public void run() {
                    disableShowCheck = false;
                    object.imageReceiver.setVisible(false, true);
                }
            };
        } else {
            backgroundDrawable.setAlpha(255);
            ViewProxy.setAlpha(containerView, 1.0f);
            onPhotoShow(messageObject, object);
        }
    }

    public void closePhoto(boolean animated, boolean fromEditMode) {
        try {
            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }

        if (parentActivity == null || !isVisible || checkAnimation() || placeProvider == null) {
            return;
        }
        parentChatActivity = null;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageFileDidFailedLoad);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageFileDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageFileLoadProgressChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.userPhotosLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);

        isActionBarVisible = false;

        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }

        final PlaceProviderObject object = placeProvider.getPlaceForPhoto(currentMessageObject);

        if (animated) {
            animationInProgress = 1;
            int visibility = animatingImageView.getVisibility();
            animatingImageView.setVisibility(View.VISIBLE);
            containerView.invalidate();

            AnimatorSetProxy animatorSet = new AnimatorSetProxy();

            final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
            Rect drawRegion = null;
            animatingImageView.setOrientation(centerImage.getOrientation());
            if (object != null) {
                animatingImageView.setNeedRadius(object.radius != 0);
                drawRegion = object.imageReceiver.getDrawRegion();
                layoutParams.width = drawRegion.right - drawRegion.left;
                layoutParams.height = drawRegion.bottom - drawRegion.top;
                animatingImageView.setImageBitmap(object.thumb);
            } else {
                animatingImageView.setNeedRadius(false);
                layoutParams.width = centerImage.getImageWidth();
                layoutParams.height = centerImage.getImageHeight();
                animatingImageView.setImageBitmap(centerImage.getBitmap());
            }
            animatingImageView.setLayoutParams(layoutParams);

            float scaleX = (float) AndroidUtilities.displaySize.x / layoutParams.width;
            float scaleY = (float) (AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight) / layoutParams.height;
            float scale2 = scaleX > scaleY ? scaleY : scaleX;
            float width = layoutParams.width * scale * scale2;
            float height = layoutParams.height * scale * scale2;
            float xPos = (AndroidUtilities.displaySize.x - width) / 2.0f;
            float yPos = (AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight - height) / 2.0f;
            ViewProxy.setTranslationX(animatingImageView, xPos + translationX);
            ViewProxy.setTranslationY(animatingImageView, yPos + translationY);
            ViewProxy.setScaleX(animatingImageView, scale * scale2);
            ViewProxy.setScaleY(animatingImageView, scale * scale2);

            if (object != null) {
                object.imageReceiver.setVisible(false, true);
                int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());

                int coords2[] = new int[2];
                object.parentView.getLocationInWindow(coords2);
                int clipTop = coords2[1] - AndroidUtilities.statusBarHeight - (object.viewY + drawRegion.top);
                if (clipTop < 0) {
                    clipTop = 0;
                }
                int clipBottom = (object.viewY + drawRegion.top + (drawRegion.bottom - drawRegion.top)) - (coords2[1] + object.parentView.getHeight() - AndroidUtilities.statusBarHeight);
                if (clipBottom < 0) {
                    clipBottom = 0;
                }

                clipTop = Math.max(clipTop, clipVertical);
                clipBottom = Math.max(clipBottom, clipVertical);

                animationValues[0][0] = ViewProxy.getScaleX(animatingImageView);
                animationValues[0][1] = ViewProxy.getScaleY(animatingImageView);
                animationValues[0][2] = ViewProxy.getTranslationX(animatingImageView);
                animationValues[0][3] = ViewProxy.getTranslationY(animatingImageView);
                animationValues[0][4] = 0;
                animationValues[0][5] = 0;
                animationValues[0][6] = 0;
                animationValues[0][7] = 0;

                animationValues[1][0] = 1;
                animationValues[1][1] = 1;
                animationValues[1][2] = object.viewX + drawRegion.left;
                animationValues[1][3] = object.viewY + drawRegion.top;
                animationValues[1][4] = clipHorizontal;
                animationValues[1][5] = clipTop;
                animationValues[1][6] = clipBottom;
                animationValues[1][7] = object.radius;

                animatorSet.playTogether(
                        ObjectAnimatorProxy.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f),
                        ObjectAnimatorProxy.ofInt(backgroundDrawable, "alpha", 0),
                        ObjectAnimatorProxy.ofFloat(containerView, "alpha", 0.0f)
                );
            } else {
                animatorSet.playTogether(
                        ObjectAnimatorProxy.ofInt(backgroundDrawable, "alpha", 0),
                        ObjectAnimatorProxy.ofFloat(animatingImageView, "alpha", 0.0f),
                        ObjectAnimatorProxy.ofFloat(animatingImageView, "translationY", translationY >= 0 ? AndroidUtilities.displaySize.y : -AndroidUtilities.displaySize.y),
                        ObjectAnimatorProxy.ofFloat(containerView, "alpha", 0.0f)
                );
            }

            animationEndRunnable = new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= 18) {
                        containerView.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                    animationInProgress = 0;
                    onPhotoClosed(object);
                }
            };

            animatorSet.setDuration(200);
            animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                @Override
                public void onAnimationEnd(Object animation) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (animationEndRunnable != null) {
                                animationEndRunnable.run();
                                animationEndRunnable = null;
                            }
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Object animation) {
                    onAnimationEnd(animation);
                }
            });
            transitionAnimationStartTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 18) {
                containerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            animatorSet.start();
        } else {
            AnimatorSetProxy animatorSet = new AnimatorSetProxy();
            animatorSet.playTogether(
                    ObjectAnimatorProxy.ofFloat(containerView, "scaleX", 0.9f),
                    ObjectAnimatorProxy.ofFloat(containerView, "scaleY", 0.9f),
                    ObjectAnimatorProxy.ofInt(backgroundDrawable, "alpha", 0),
                    ObjectAnimatorProxy.ofFloat(containerView, "alpha", 0.0f)
            );
            animationInProgress = 2;
            animationEndRunnable = new Runnable() {
                @Override
                public void run() {
                    if (containerView == null) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= 18) {
                        containerView.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                    animationInProgress = 0;
                    onPhotoClosed(object);
                    ViewProxy.setScaleX(containerView, 1.0f);
                    ViewProxy.setScaleY(containerView, 1.0f);
                    containerView.clearAnimation();
                }
            };
            animatorSet.setDuration(200);
            animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                @Override
                public void onAnimationEnd(Object animation) {
                    if (animationEndRunnable != null) {
                        animationEndRunnable.run();
                        animationEndRunnable = null;
                    }
                }
            });
            transitionAnimationStartTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 18) {
                containerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            animatorSet.start();
        }
    }

    public void destroyPhotoViewer() {
        if (parentActivity == null || windowView == null) {
            return;
        }
        try {
            if (windowView.getParent() != null) {
                WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(windowView);
            }
            windowView = null;
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        Instance = null;
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        isVisible = false;
        disableShowCheck = true;
        currentMessageObject = null;
        currentPathObject = null;
        currentThumb = null;
        if (radialProgressViews != null) {
            radialProgressViews.setBackgroundState(-1, false);
        }
        centerImage.setImageBitmap((Bitmap) null);
        containerView.post(new Runnable() {
            @Override
            public void run() {
                animatingImageView.setImageBitmap(null);
                try {
                    if (windowView.getParent() != null) {
                        WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
                        wm.removeView(windowView);
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
        if (placeProvider != null) {
            placeProvider.willHidePhotoViewer();
        }
        placeProvider = null;
        disableShowCheck = false;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
    }

    private void redraw(final int count) {
        if (count < 6) {
            if (containerView != null) {
                containerView.invalidate();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        redraw(count + 1);
                    }
                }, 100);
            }
        }
    }

    public void onResume() {
        redraw(0); //workaround for camera bug
    }

    public boolean isVisible() {
        return isVisible && placeProvider != null;
    }

    private void updateMinMax(float scale) {
        int maxW = (int) (centerImage.getImageWidth() * scale - getContainerViewWidth()) / 2;
        int maxH = (int) (centerImage.getImageHeight() * scale - getContainerViewHeight()) / 2;
        if (maxW > 0) {
            minX = -maxW;
            maxX = maxW;
        } else {
            minX = maxX = 0;
        }
        if (maxH > 0) {
            minY = -maxH;
            maxY = maxH;
        } else {
            minY = maxY = 0;
        }
    }

    private int getAdditionX() {
        return 0;
    }

    private int getAdditionY() {
        return 0;
    }

    private int getContainerViewWidth() {
        return getContainerViewWidth(0);
    }

    private int getContainerViewWidth(int mode) {
        int width = containerView.getWidth();
        if (mode != 0) {
            width -= AndroidUtilities.dp(28);
        }
        return width;
    }

    private int getContainerViewHeight() {
        return getContainerViewHeight(0);
    }

    private int getContainerViewHeight(int mode) {
        //int height = containerView.getHeight();
        int height = AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight;
        if (mode == 1) {
            height -= AndroidUtilities.dp(76);
        } else if (mode == 2) {
            height -= AndroidUtilities.dp(154);
        }
        return height;
    }

    private boolean onTouchEvent(MotionEvent ev) {
        if (animationInProgress != 0 || animationStartTime != 0) {
            return false;
        }


        if (ev.getPointerCount() == 1 && gestureDetector.onTouchEvent(ev)) {
            if (doubleTap) {
                doubleTap = false;
                moving = false;
                zooming = false;
                checkMinMax(false);
                return true;
            }
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            discardTap = false;
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
            }
            if (!draggingDown) {
                if (canZoom && ev.getPointerCount() == 2) {
                    pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                    pinchStartScale = scale;
                    pinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                    pinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                    pinchStartX = translationX;
                    pinchStartY = translationY;
                    zooming = true;
                    moving = false;
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                    }
                } else if (ev.getPointerCount() == 1) {
                    moveStartX = ev.getX();
                    dragY = moveStartY = ev.getY();
                    draggingDown = false;
                    canDragDown = true;
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                    }
                }
            }
        } else if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (canZoom && ev.getPointerCount() == 2 && !draggingDown && zooming) {
                discardTap = true;
                scale = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0)) / pinchStartDistance * pinchStartScale;
                translationX = (pinchCenterX - getContainerViewWidth() / 2) - ((pinchCenterX - getContainerViewWidth() / 2) - pinchStartX) * (scale / pinchStartScale);
                translationY = (pinchCenterY - getContainerViewHeight() / 2) - ((pinchCenterY - getContainerViewHeight() / 2) - pinchStartY) * (scale / pinchStartScale);
                updateMinMax(scale);
                containerView.invalidate();
            } else if (ev.getPointerCount() == 1) {
                if (velocityTracker != null) {
                    velocityTracker.addMovement(ev);
                }
                float dx = Math.abs(ev.getX() - moveStartX);
                float dy = Math.abs(ev.getY() - dragY);
                if (dx > AndroidUtilities.dp(3) || dy > AndroidUtilities.dp(3)) {
                    discardTap = true;
                }
                if (!(placeProvider instanceof EmptyPhotoViewerProvider) && canDragDown && !draggingDown && scale == 1 && dy >= AndroidUtilities.dp(30) && dy / 2 > dx) {
                    draggingDown = true;
                    moving = false;
                    dragY = ev.getY();
                    if (isActionBarVisible && canShowBottom) {
                        toggleActionBar(false, true);
                    }
                    return true;
                } else if (draggingDown) {
                    translationY = ev.getY() - dragY;
                    containerView.invalidate();
                } else if (!invalidCoords && animationStartTime == 0) {
                    float moveDx = moveStartX - ev.getX();
                    float moveDy = moveStartY - ev.getY();
                    if (moving || scale == 1 && Math.abs(moveDy) + AndroidUtilities.dp(12) < Math.abs(moveDx) || scale != 1) {
                        if (!moving) {
                            moveDx = 0;
                            moveDy = 0;
                            moving = true;
                            canDragDown = false;
                        }

                        moveStartX = ev.getX();
                        moveStartY = ev.getY();
                        updateMinMax(scale);
                        if (translationX < minX && translationX > maxX) {
                            moveDx /= 3.0f;
                        }
                        if (maxY == 0 && minY == 0) {
                            if (translationY - moveDy < minY) {
                                translationY = minY;
                                moveDy = 0;
                            } else if (translationY - moveDy > maxY) {
                                translationY = maxY;
                                moveDy = 0;
                            }
                        } else {
                            if (translationY < minY || translationY > maxY) {
                                moveDy /= 3.0f;
                            }
                        }

                        translationX -= moveDx;
                        if (scale != 1) {
                            translationY -= moveDy;
                        }

                        containerView.invalidate();
                    }
                } else {
                    invalidCoords = false;
                    moveStartX = ev.getX();
                    moveStartY = ev.getY();
                }
            }
        } else if (ev.getActionMasked() == MotionEvent.ACTION_CANCEL || ev.getActionMasked() == MotionEvent.ACTION_UP || ev.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            if (zooming) {
                invalidCoords = true;
                if (scale < 1.0f) {
                    updateMinMax(1.0f);
                    animateTo(1.0f, 0, 0, true);
                } else if (scale > 3.0f) {
                    float atx = (pinchCenterX - getContainerViewWidth() / 2) - ((pinchCenterX - getContainerViewWidth() / 2) - pinchStartX) * (3.0f / pinchStartScale);
                    float aty = (pinchCenterY - getContainerViewHeight() / 2) - ((pinchCenterY - getContainerViewHeight() / 2) - pinchStartY) * (3.0f / pinchStartScale);
                    updateMinMax(3.0f);
                    if (atx < minX) {
                        atx = minX;
                    } else if (atx > maxX) {
                        atx = maxX;
                    }
                    if (aty < minY) {
                        aty = minY;
                    } else if (aty > maxY) {
                        aty = maxY;
                    }
                    animateTo(3.0f, atx, aty, true);
                } else {
                    checkMinMax(true);
                }
                zooming = false;
            } else if (draggingDown) {
                if (Math.abs(dragY - ev.getY()) > getContainerViewHeight() / 6.0f) {
                    closePhoto(true, false);
                } else {
                    animateTo(1, 0, 0, false);
                }
                draggingDown = false;
            } else if (moving) {
                float moveToX = translationX;
                float moveToY = translationY;
                updateMinMax(scale);
                moving = false;
                canDragDown = true;
                float velocity = 0;
                if (velocityTracker != null && scale == 1) {
                    velocityTracker.computeCurrentVelocity(1000);
                    velocity = velocityTracker.getXVelocity();
                }

                if (translationX < minX) {
                    moveToX = minX;
                } else if (translationX > maxX) {
                    moveToX = maxX;
                }
                if (translationY < minY) {
                    moveToY = minY;
                } else if (translationY > maxY) {
                    moveToY = maxY;
                }
                animateTo(scale, moveToX, moveToY, false);
            }
        }
        return false;
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = translationX;
        float moveToY = translationY;
        updateMinMax(scale);
        if (translationX < minX) {
            moveToX = minX;
        } else if (translationX > maxX) {
            moveToX = maxX;
        }
        if (translationY < minY) {
            moveToY = minY;
        } else if (translationY > maxY) {
            moveToY = maxY;
        }
        animateTo(scale, moveToX, moveToY, zoom);
    }

    private void goToNext() {
        float extra = 0;
        if (scale != 1) {
            extra = (getContainerViewWidth() - centerImage.getImageWidth()) / 2 * scale;
        }
        switchImageAfterAnimation = 1;
        animateTo(scale, minX - getContainerViewWidth() - extra - PAGE_SPACING / 2, translationY, false);
    }

    private void goToPrev() {
        float extra = 0;
        if (scale != 1) {
            extra = (getContainerViewWidth() - centerImage.getImageWidth()) / 2 * scale;
        }
        switchImageAfterAnimation = 2;
        animateTo(scale, maxX + getContainerViewWidth() + extra + PAGE_SPACING / 2, translationY, false);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, 250);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (scale == newScale && translationX == newTx && translationY == newTy) {
            return;
        }
        zoomAnimation = isZoom;
        animateToScale = newScale;
        animateToX = newTx;
        animateToY = newTy;
        animationStartTime = System.currentTimeMillis();
        imageMoveAnimation = new AnimatorSetProxy();
        imageMoveAnimation.playTogether(
                ObjectAnimatorProxy.ofFloat(this, "animationValue", 0, 1)
        );
        imageMoveAnimation.setInterpolator(interpolator);
        imageMoveAnimation.setDuration(duration);
        imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy() {
            @Override
            public void onAnimationEnd(Object animation) {
                imageMoveAnimation = null;
                containerView.invalidate();
            }
        });
        imageMoveAnimation.start();
    }

    public void setAnimationValue(float value) {
        animationValue = value;
        containerView.invalidate();
    }

    public float getAnimationValue() {
        return animationValue;
    }

    private void onDraw(Canvas canvas) {
        if (animationInProgress == 1 || !isVisible && animationInProgress != 2) {
            return;
        }

        float currentTranslationY;
        float currentTranslationX;
        float currentScale;
        float aty = -1;

        if (imageMoveAnimation != null) {
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
            }

            float ts = scale + (animateToScale - scale) * animationValue;
            float tx = translationX + (animateToX - translationX) * animationValue;
            float ty = translationY + (animateToY - translationY) * animationValue;

            if (animateToScale == 1 && scale == 1 && translationX == 0) {
                aty = ty;
            }
            currentScale = ts;
            currentTranslationY = ty;
            currentTranslationX = tx;
            containerView.invalidate();
        } else {
            if (animationStartTime != 0) {
                translationX = animateToX;
                translationY = animateToY;
                scale = animateToScale;
                animationStartTime = 0;
                updateMinMax(scale);
                zoomAnimation = false;
            }
            if (!scroller.isFinished()) {
                if (scroller.computeScrollOffset()) {
                    if (scroller.getStartX() < maxX && scroller.getStartX() > minX) {
                        translationX = scroller.getCurrX();
                    }
                    if (scroller.getStartY() < maxY && scroller.getStartY() > minY) {
                        translationY = scroller.getCurrY();
                    }
                    containerView.invalidate();
                }
            }
            if (switchImageAfterAnimation != 0) {
                switchImageAfterAnimation = 0;
            }
            currentScale = scale;
            currentTranslationY = translationY;
            currentTranslationX = translationX;
            if (!moving) {
                aty = translationY;
            }
        }

        if (scale == 1 && aty != -1 && !zoomAnimation) {
            float maxValue = getContainerViewHeight() / 4.0f;
            backgroundDrawable.setAlpha((int) Math.max(127, 255 * (1.0f - (Math.min(Math.abs(aty), maxValue) / maxValue))));
        } else {
            backgroundDrawable.setAlpha(255);
        }
        float tranlateX = currentTranslationX;
        float scaleDiff = 0;
        float alpha = 1;
        if (!zoomAnimation && tranlateX > maxX) {
            alpha = Math.min(1.0f, (tranlateX - maxX) / canvas.getWidth());
            scaleDiff = alpha * 0.3f;
            alpha = 1.0f - alpha;
            tranlateX = maxX;
        }
        if (centerImage.getBitmap() != null) {
            canvas.save();
            canvas.translate(getContainerViewWidth() / 2 + getAdditionX(), getContainerViewHeight() / 2 + getAdditionY());
            canvas.translate(tranlateX, currentTranslationY);
            canvas.scale(currentScale - scaleDiff, currentScale - scaleDiff);

            int bitmapWidth = centerImage.getBitmapWidth();
            int bitmapHeight = centerImage.getBitmapHeight();

            float scaleX = (float) getContainerViewWidth() / (float) bitmapWidth;
            float scaleY = (float) getContainerViewHeight() / (float) bitmapHeight;
            float scale = scaleX > scaleY ? scaleY : scaleX;
            int width = (int) (bitmapWidth * scale);
            int height = (int) (bitmapHeight * scale);

            centerImage.setAlpha(alpha);
            centerImage.setImageCoords(-width / 2, -height / 2, width, height);
            centerImage.draw(canvas);
            canvas.restore();
        }
        canvas.save();
        canvas.translate(tranlateX, currentTranslationY / currentScale);
        radialProgressViews.setScale(1.0f - scaleDiff);
        radialProgressViews.setAlpha(alpha);
        radialProgressViews.onDraw(canvas);
        canvas.restore();

        canvas.save();
        canvas.translate(currentTranslationX, currentTranslationY / currentScale);
        canvas.translate(-(canvas.getWidth() * (scale + 1) + PAGE_SPACING) / 2, -currentTranslationY / currentScale);
        canvas.restore();
    }

    @SuppressLint("DrawAllocation")
    private void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            scale = 1;
            translationX = 0;
            translationY = 0;
            updateMinMax(scale);

            if (checkImageView != null) {
                checkImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) checkImageView.getLayoutParams();
                        WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService(Activity.WINDOW_SERVICE);
                        int rotation = manager.getDefaultDisplay().getRotation();
                        layoutParams.topMargin = AndroidUtilities.dp(rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_90 ? 58 : 68);
                        checkImageView.setLayoutParams(layoutParams);
                    }
                });
            }
        }
    }

    private void onActionClick() {
        if (currentMessageObject == null || currentFileNames == null) {
            return;
        }
//        if (currentMessageObject.messageOwner.attachPath != null && currentMessageObject.messageOwner.attachPath.length() != 0) {
//            File f = new File(currentMessageObject.messageOwner.attachPath);
//            if (f.exists()) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.fromFile(f), "video/mp4");
//                parentActivity.startActivityForResult(intent, 500);
//            } else {
//                loadFile = true;
//            }
//        } else {
//            File cacheFile = FileLoader.getPathToMessage(currentMessageObject.messageOwner);
//            if (cacheFile.exists()) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.fromFile(cacheFile), "video/mp4");
//                parentActivity.startActivityForResult(intent, 500);
//            } else {
//                loadFile = true;
//            }
//        }
//        if (loadFile) {
//            if (!FileLoader.getInstance().isLoadingFile(currentFileNames[0])) {
//                FileLoader.getInstance().loadFile(currentMessageObject.messageOwner.media.video, true);
//            } else {
//                FileLoader.getInstance().cancelLoadFile(currentMessageObject.messageOwner.media.video);
//            }
//        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (scale != 1) {
            scroller.abortAnimation();
            scroller.fling(Math.round(translationX), Math.round(translationY), Math.round(velocityX), Math.round(velocityY), (int) minX, (int) maxX, (int) minY, (int) maxY);
            containerView.postInvalidate();
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (discardTap) {
            return false;
        }
        if (canShowBottom) {
            if (radialProgressViews != null && containerView != null) {
                int state = radialProgressViews.backgroundState;
                if (state > 0 && state <= 3) {
                    float x = e.getX();
                    float y = e.getY();
                    if (x >= (getContainerViewWidth() - AndroidUtilities.dp(64)) / 2.0f && x <= (getContainerViewWidth() + AndroidUtilities.dp(64)) / 2.0f &&
                            y >= (getContainerViewHeight() - AndroidUtilities.dp(64)) / 2.0f && y <= (getContainerViewHeight() + AndroidUtilities.dp(64)) / 2.0f) {
                        onActionClick();
                        checkProgress(true);
                        return true;
                    }
                }
            }
            toggleActionBar(!isActionBarVisible, true);
        } else if (sendPhotoType == 0) {
            checkImageView.performClick();
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (!canZoom || scale == 1.0f && (translationY != 0 || translationX != 0)) {
            return false;
        }
        if (animationStartTime != 0 || animationInProgress != 0) {
            return false;
        }
        if (scale == 1.0f) {
            float atx = (e.getX() - getContainerViewWidth() / 2) - ((e.getX() - getContainerViewWidth() / 2) - translationX) * (3.0f / scale);
            float aty = (e.getY() - getContainerViewHeight() / 2) - ((e.getY() - getContainerViewHeight() / 2) - translationY) * (3.0f / scale);
            updateMinMax(3.0f);
            if (atx < minX) {
                atx = minX;
            } else if (atx > maxX) {
                atx = maxX;
            }
            if (aty < minY) {
                aty = minY;
            } else if (aty > maxY) {
                aty = maxY;
            }
            animateTo(3.0f, atx, aty, true);
        } else {
            animateTo(1.0f, 0, 0, true);
        }
        doubleTap = true;
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
