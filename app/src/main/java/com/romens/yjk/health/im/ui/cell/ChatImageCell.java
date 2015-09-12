package com.romens.yjk.health.im.ui.cell;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import com.easemob.EMCallBack;
import com.easemob.applib.utils.CommonUtils;
import com.easemob.applib.utils.ImageCache;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.romens.android.AndroidUtilities;
import com.romens.android.core.ImageReceiver;
import com.romens.yjk.health.db.entity.MessageObject;
import com.romens.yjk.health.helper.ImageHelper;
import com.romens.yjk.health.helper.MessageImageFileHelper;

import java.io.File;

/**
 * Created by siery on 15/8/30.
 */
public abstract class ChatImageCell extends ChatCell {
    private ImageReceiver imageReceiver;
    ChatImageDelegate chatImageDelegate;

    public ChatImageCell(Context context) {
        super(context);
        imageReceiver = new ImageReceiver(null);
    }

    public void setChatImageDelegate(ChatImageDelegate delegate) {
        chatImageDelegate = delegate;
    }

    public interface ChatImageDelegate {
        void onImageClick(ChatImageCell cell);

        void onImageLongClick(ChatImageCell cell);
    }

    protected abstract void changeMessageState(int state);

    protected void registerImageClickListener(View imageView) {
        if (imageView != null) {
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatImageDelegate != null) {
                        chatImageDelegate.onImageClick(ChatImageCell.this);
                    }
                }
            });

        }
    }

    protected void registerImageLongClickListener(View imageView) {
        if (imageView != null) {
            imageView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (chatImageDelegate != null) {
                        chatImageDelegate.onImageLongClick(ChatImageCell.this);
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    protected void handleMessage(MessageObject messageObject) {
        if (isImageMessage(messageObject)) {
            if (!messageObject.isOut()) {
                if (messageObject.isSending()) {
                    changeMessageState(1);
                } else {
                    ImageMessageBody imgBody = (ImageMessageBody) messageObject.messageOwner.getBody();
                    if (imgBody.getLocalUrl() != null) {
                        String remotePath = imgBody.getRemoteUrl();
                        String filePath = MessageImageFileHelper.getImagePath(remotePath);
                        String thumbRemoteUrl = imgBody.getThumbnailUrl();
                        if (TextUtils.isEmpty(thumbRemoteUrl) && !TextUtils.isEmpty(remotePath)) {
                            thumbRemoteUrl = remotePath;
                        }
                        String thumbnailPath = MessageImageFileHelper.getThumbnailImagePath(thumbRemoteUrl);
                        bindImage(thumbnailPath, filePath, messageObject);
                    }
                }
            } else {
                ImageMessageBody imgBody = (ImageMessageBody) messageObject.messageOwner.getBody();
                String filePath = imgBody.getLocalUrl();
                if (filePath != null && new File(filePath).exists()) {
                    String thumbPathTemp = MessageImageFileHelper.getThumbnailImagePath(filePath);
                    if (thumbPathTemp != null && new File(thumbPathTemp).exists()) {
                        bindImage(thumbPathTemp, filePath, messageObject);
                    } else {
                        bindImage(filePath, filePath, messageObject);
                    }

                } else {
                    bindImage(MessageImageFileHelper.getThumbnailImagePath(filePath), filePath, messageObject);
                }
                if (messageObject.isSending()) {
                    changeMessageState(1);
                }
            }
        } else if (isVideoMessage(messageObject)) {
            VideoMessageBody videoBody = (VideoMessageBody) messageObject.messageOwner.getBody();
            String localThumb = videoBody.getLocalThumb();
            if (!TextUtils.isEmpty(localThumb)) {
                bindImage(localThumb, videoBody.getThumbnailUrl(), messageObject);
            }
            if (!messageObject.isOut()) {
                if (messageObject.isSending()) {
                    changeMessageState(1);
                } else {
                    bindImage(localThumb, videoBody.getThumbnailUrl(), messageObject);
                }
            } else {
                if (messageObject.isSending()) {
                    changeMessageState(1);
                }
            }
        }
    }

    protected void setImageViewBitmap(Bitmap bitmap) {
        imageReceiver.setImageBitmap(bitmap);
    }

    public ImageReceiver getImageReceiver() {
        return imageReceiver;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        imageReceiver.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        imageReceiver.onAttachedToWindow();
    }

    @Override
    public void setValue(MessageObject message) {
        super.setValue(message);
        imageReceiver.setForcePreview(false);
        imageReceiver.setNeedsQualityThumb(false);
        imageReceiver.setShouldGenerateQualityThumb(false);

    }

    protected void bindImage(String thumbPath, String filePath, MessageObject messageObject) {
        Bitmap photoObject = ImageCache.getInstance().getForImgPath(thumbPath);
        if (photoObject != null) {
            setImageViewBitmap(photoObject);
            changeMessageState(0);
        } else {
            if (isImageMessage(messageObject)) {
                new LoadImageTask().execute(thumbPath, filePath, messageObject.messageOwner);
            } else if (isVideoMessage(messageObject)) {
                new LoadVideoImageTask().execute(thumbPath, filePath, messageObject.messageOwner);
            }
        }
    }

    public boolean isImageMessage(MessageObject messageObject) {
        if (messageObject.contentType == MessageObject.MESSAGE_TYPE_RECV_IMAGE ||
                messageObject.contentType == MessageObject.MESSAGE_TYPE_SENT_IMAGE) {
            return true;
        }
        return false;
    }

    public boolean isVideoMessage(MessageObject messageObject) {
        if (messageObject.contentType == MessageObject.MESSAGE_TYPE_RECV_VIDEO ||
                messageObject.contentType == MessageObject.MESSAGE_TYPE_SENT_VIDEO) {
            return true;
        }
        return false;
    }

    class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
        String localFullSizePath = null;
        String thumbnailPath = null;
        EMMessage message = null;

        @Override
        protected Bitmap doInBackground(Object... args) {
            thumbnailPath = (String) args[0];
            localFullSizePath = (String) args[1];
            message = (EMMessage) args[2];
            File file = new File(thumbnailPath);
            if (file.exists()) {
                return ImageHelper.decodeScaleImage(thumbnailPath, 500, 500);
            } else {
                if (message.direct == EMMessage.Direct.SEND) {
                    return ImageHelper.decodeScaleImage(thumbnailPath, 500, 500);
                } else {
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            changeMessageState(0);
            if (image != null) {
                setImageViewBitmap(image);
                ImageCache.getInstance().putForImgPath(thumbnailPath, image);
            } else {
                if (message.status == EMMessage.Status.FAIL) {
                    if (CommonUtils.isNetWorkConnected(getContext())) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                EMChatManager.getInstance().asyncFetchMessage(message);
                            }
                        }).start();
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    class LoadVideoImageTask extends AsyncTask<Object, Void, Bitmap> {

        String thumbnailPath = null;
        String thumbnailUrl = null;
        EMMessage message;

        @Override
        protected Bitmap doInBackground(Object... params) {
            thumbnailPath = (String) params[0];
            thumbnailUrl = (String) params[1];
            message = (EMMessage) params[2];
            if (new File(thumbnailPath).exists()) {
                return ImageHelper.decodeScaleImage(thumbnailPath, 500, 500);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            changeMessageState(0);
            if (result != null) {
                ImageCache.getInstance().putForImgPath(thumbnailPath, result);
                setImageViewBitmap(result);
            } else {
                if (message.status == EMMessage.Status.FAIL
                        || message.direct == EMMessage.Direct.RECEIVE) {
                    if (CommonUtils.isNetWorkConnected(getContext())) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                EMChatManager.getInstance().asyncFetchMessage(
                                        message);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                            }
                        }.execute();
                    }
                }

            }
        }
    }


    @Override
    public void tryHandleMessageForCreate(final MessageObject message) {
        try {
            EMChatManager.getInstance().sendMessage(message.messageOwner, new EMCallBack() {

                @Override
                public void onSuccess() {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            changeMessageState(0);
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    changeMessageState(0);
                }

                @Override
                public void onProgress(final int progress, String status) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            changeMessageState(1);
                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
