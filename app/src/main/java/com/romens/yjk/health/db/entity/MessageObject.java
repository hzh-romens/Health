package com.romens.yjk.health.db.entity;

import android.graphics.Paint;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.util.Linkify;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.db.IMMessagesController;
import com.romens.yjk.health.im.Constant;
import com.romens.yjk.health.im.Emoji;
import com.romens.yjk.health.ui.components.URLSpanNoUnderline;
import com.romens.yjk.health.ui.components.URLSpanNoUnderlineBold;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageObject {

    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;

    public EMMessage messageOwner;
    public CharSequence messageText;
    public CharSequence linkDescription;
    public CharSequence caption;
    public int contentType;
    public String dateKey;
    public String monthKey;
    public boolean deleted;
    public int audioProgress;
    public int audioProgressSec;

    public static TextPaint textPaint;
    public int lastLineWidth;
    public int textWidth;
    public int textHeight;
    public int blockHeight = Integer.MAX_VALUE;

    public static Pattern urlPattern;

    public static class TextLayoutBlock {
        public StaticLayout textLayout;
        public float textXOffset = 0;
        public float textYOffset = 0;
        public int charactersOffset = 0;
    }

    private static final int LINES_PER_BLOCK = 10;

    public ArrayList<TextLayoutBlock> textLayoutBlocks;

    public MessageObject(EMMessage message, boolean generateLayout) {
        if (textPaint == null) {
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(0xff000000);
            textPaint.linkColor = 0xff316f9f;
        }

        textPaint.setTextSize(AndroidUtilities.dp(IMMessagesController.getInstance().fontSize));

        messageOwner = message;
        if (!isMediaEmpty()) {
            if (message.getType() == EMMessage.Type.LOCATION) {
                messageText = "位置分享";
            }
//            if (message.getType() == EMMessage.Type.IMAGE) {
//                messageText = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
//            } else if (message.getType() == EMMessage.Type.VIDEO) {
//                messageText = LocaleController.getString("AttachVideo", R.string.AttachVideo);
//            } else if (message.media instanceof TLRPC.TL_messageMediaGeo || message.media instanceof TLRPC.TL_messageMediaVenue) {
//                messageText = LocaleController.getString("AttachLocation", R.string.AttachLocation);
//            } else if (message.media instanceof TLRPC.TL_messageMediaContact) {
//                messageText = LocaleController.getString("AttachContact", R.string.AttachContact);
//            } else if (message.media instanceof TLRPC.TL_messageMediaUnsupported) {
//                messageText = LocaleController.getString("UnsuppotedMedia", R.string.UnsuppotedMedia);
//            } else if (message.media instanceof TLRPC.TL_messageMediaDocument) {
//                if (isSticker()) {
//                    String sch = getStrickerChar();
//                    if (sch != null && sch.length() > 0) {
//                        messageText = String.format("%s %s", sch, LocaleController.getString("AttachSticker", R.string.AttachSticker));
//                    } else {
//                        messageText = LocaleController.getString("AttachSticker", R.string.AttachSticker);
//                    }
//                } else {
//                    String name = FileLoader.getDocumentFileName(message.media.document);
//                    if (name != null && name.length() > 0) {
//                        messageText = name;
//                    } else {
//                        messageText = LocaleController.getString("AttachDocument", R.string.AttachDocument);
//                    }
//                }
//            } else if (message.media instanceof TLRPC.TL_messageMediaAudio) {
//                messageText = LocaleController.getString("AttachAudio", R.string.AttachAudio);
//            }
        } else {
            TextMessageBody txtBody = (TextMessageBody) message.getBody();
            messageText = txtBody.getMessage();
        }
        messageText = Emoji.replaceEmoji(messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20), false);
        contentType = getContentType(messageOwner);
//        if (message instanceof TLRPC.TL_message || message instanceof TLRPC.TL_messageForwarded_old2) {
//            if (isMediaEmpty()) {
//                contentType = type = 0;
//                if (messageText.length() == 0) {
//                    messageText = "Empty message";
//                }
//            } else if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
//                contentType = type = 1;
//            } else if (message.media instanceof TLRPC.TL_messageMediaGeo || message.media instanceof TLRPC.TL_messageMediaVenue) {
//                contentType = 1;
//                type = 4;
//            } else if (message.media instanceof TLRPC.TL_messageMediaVideo) {
//                contentType = 1;
//                type = 3;
//            } else if (message.media instanceof TLRPC.TL_messageMediaContact) {
//                contentType = 3;
//                type = 12;
//            } else if (message.media instanceof TLRPC.TL_messageMediaUnsupported) {
//                contentType = type = 0;
//            } else if (message.media instanceof TLRPC.TL_messageMediaDocument) {
//                contentType = 1;
//                if (message.media.document.mime_type != null) {
//                    if (message.media.document.mime_type.equals("image/gif") && message.media.document.thumb != null && !(message.media.document.thumb instanceof TLRPC.TL_photoSizeEmpty)) {
//                        type = 8;
//                    } else if (message.media.document.mime_type.equals("image/webp") && isSticker()) {
//                        type = 13;
//                    } else {
//                        type = 9;
//                    }
//                } else {
//                    type = 9;
//                }
//            } else if (message.media instanceof TLRPC.TL_messageMediaAudio) {
//                contentType = type = 2;
//            }
//        }

        Calendar rightNow = new GregorianCalendar();
        rightNow.setTimeInMillis(messageOwner.getMsgTime() * 1000);
        int dateDay = rightNow.get(Calendar.DAY_OF_YEAR);
        int dateYear = rightNow.get(Calendar.YEAR);
        int dateMonth = rightNow.get(Calendar.MONTH);
        dateKey = String.format("%d_%02d_%02d", dateYear, dateMonth, dateDay);
        if (contentType != 0 && contentType != 1) {
            monthKey = String.format("%d_%02d", dateYear, dateMonth);
        }

        generateCaption();
        if (generateLayout) {
            generateLayout();
        }
        generateThumbs(false);
    }

    public void generateThumbs(boolean update) {
//        if (messageOwner.media != null && !(messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) {
//            if (messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
//                if (!update) {
//                    photoThumbs = new ArrayList<>(messageOwner.media.photo.sizes);
//                } else if (photoThumbs != null && !photoThumbs.isEmpty()) {
//                    for (TLRPC.PhotoSize photoObject : photoThumbs) {
//                        for (TLRPC.PhotoSize size : messageOwner.media.photo.sizes) {
//                            if (size instanceof TLRPC.TL_photoSizeEmpty) {
//                                continue;
//                            }
//                            if (size.type.equals(photoObject.type)) {
//                                photoObject.location = size.location;
//                                break;
//                            }
//                        }
//                    }
//                }
//            } else if (messageOwner.media instanceof TLRPC.TL_messageMediaVideo) {
//                if (!update) {
//                    photoThumbs = new ArrayList<>();
//                    photoThumbs.add(messageOwner.media.video.thumb);
//                } else if (photoThumbs != null && !photoThumbs.isEmpty() && messageOwner.media.video.thumb != null) {
//                    TLRPC.PhotoSize photoObject = photoThumbs.get(0);
//                    photoObject.location = messageOwner.media.video.thumb.location;
//                }
//            } else if (messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
//                if (!(messageOwner.media.document.thumb instanceof TLRPC.TL_photoSizeEmpty)) {
//                    if (!update) {
//                        photoThumbs = new ArrayList<>();
//                        photoThumbs.add(messageOwner.media.document.thumb);
//                    } else if (photoThumbs != null && !photoThumbs.isEmpty() && messageOwner.media.document.thumb != null) {
//                        TLRPC.PhotoSize photoObject = photoThumbs.get(0);
//                        photoObject.location = messageOwner.media.document.thumb.location;
//                    }
//                }
//            } else if (messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
//                if (messageOwner.media.webpage.photo != null) {
//                    if (!update || photoThumbs == null) {
//                        photoThumbs = new ArrayList<>(messageOwner.media.webpage.photo.sizes);
//                    } else if (!photoThumbs.isEmpty()) {
//                        for (TLRPC.PhotoSize photoObject : photoThumbs) {
//                            for (TLRPC.PhotoSize size : messageOwner.media.webpage.photo.sizes) {
//                                if (size instanceof TLRPC.TL_photoSizeEmpty) {
//                                    continue;
//                                }
//                                if (size.type.equals(photoObject.type)) {
//                                    photoObject.location = size.location;
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    private static boolean containsUrls(CharSequence message) {
        if (message == null || message.length() < 2 || message.length() > 1024 * 20) {
            return false;
        }

        int length = message.length();

        int digitsInRow = 0;
        int schemeSequence = 0;
        int dotSequence = 0;

        char lastChar = 0;

        for (int i = 0; i < length; i++) {
            char c = message.charAt(i);

            if (c >= '0' && c <= '9') {
                digitsInRow++;
                if (digitsInRow >= 6) {
                    return true;
                }
                schemeSequence = 0;
                dotSequence = 0;
            } else if (!(c != ' ' && digitsInRow > 0)) {
                digitsInRow = 0;
            }
            if ((c == '@' || c == '#' || c == '/') && i == 0 || i != 0 && (message.charAt(i - 1) == ' ' || message.charAt(i - 1) == '\n')) {
                return true;
            }
            if (c == ':') {
                if (schemeSequence == 0) {
                    schemeSequence = 1;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '/') {
                if (schemeSequence == 2) {
                    return true;
                }
                if (schemeSequence == 1) {
                    schemeSequence++;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '.') {
                if (dotSequence == 0 && lastChar != ' ') {
                    dotSequence++;
                } else {
                    dotSequence = 0;
                }
            } else if (c != ' ' && lastChar == '.' && dotSequence == 1) {
                return true;
            } else {
                dotSequence = 0;
            }
            lastChar = c;
        }
        return false;
    }

    public void generateLinkDescription() {
        if (linkDescription != null) {
            return;
        }
//        if (messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && messageOwner.media.webpage instanceof TLRPC.TL_webPage && messageOwner.media.webpage.description != null) {
//            linkDescription = Spannable.Factory.getInstance().newSpannable(messageOwner.media.webpage.description);
//            if (containsUrls(linkDescription)) {
//                Linkify.addLinks((Spannable) linkDescription, Linkify.WEB_URLS);
//            }
//        }
    }

    public void generateCaption() {
        if (caption != null) {
            return;
        }
//        if (messageOwner.media != null && messageOwner.media.caption != null && messageOwner.media.caption.length() > 0) {
//            caption = Emoji.replaceEmoji(messageOwner.media.caption, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20));
//            if (containsUrls(caption)) {
//                try {
//                    Linkify.addLinks((Spannable) caption, Linkify.WEB_URLS);
//                } catch (Exception e) {
//                    FileLog.e("tmessages", e);
//                }
//                addUsernamesAndHashtags(caption);
//            }
//        }
    }

    private static void addUsernamesAndHashtags(CharSequence charSequence) {
        try {
            if (urlPattern == null) {
                urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{5,32}|(^|\\s)#[\\w\\.]+");
            }
            Matcher matcher = urlPattern.matcher(charSequence);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (charSequence.charAt(start) != '@' && charSequence.charAt(start) != '#' && charSequence.charAt(start) != '/') {
                    start++;
                }
                URLSpanNoUnderline url = new URLSpanNoUnderline(charSequence.subSequence(start, end).toString());
                ((Spannable) charSequence).setSpan(url, start, end, 0);
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    public static void addLinks(CharSequence messageText) {
        if (messageText instanceof Spannable && containsUrls(messageText)) {
            if (messageText.length() < 100) {
                try {
                    Linkify.addLinks((Spannable) messageText, Linkify.WEB_URLS | Linkify.PHONE_NUMBERS);
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            } else {
                try {
                    Linkify.addLinks((Spannable) messageText, Linkify.WEB_URLS);
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
            addUsernamesAndHashtags(messageText);
        }
    }

    private void generateLayout() {
        if (TextUtils.isEmpty(messageOwner.getTo()) || messageText == null || messageText.length() == 0) {
            return;
        }

        generateLinkDescription();
        textLayoutBlocks = new ArrayList<>();

        addLinks(messageText);

        int maxWidth;
        if ((!TextUtils.isEmpty(messageOwner.getTo())) && !isOut()) {
            maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(122);
        } else {
            maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(80);
        }

        StaticLayout textLayout;

        try {
            textLayout = new StaticLayout(messageText, textPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Exception e) {
            FileLog.e("tmessages", e);
            return;
        }

        textHeight = textLayout.getHeight();
        int linesCount = textLayout.getLineCount();

        int blocksCount = (int) Math.ceil((float) linesCount / LINES_PER_BLOCK);
        int linesOffset = 0;
        float prevOffset = 0;

        for (int a = 0; a < blocksCount; a++) {
            int currentBlockLinesCount = Math.min(LINES_PER_BLOCK, linesCount - linesOffset);
            TextLayoutBlock block = new TextLayoutBlock();

            if (blocksCount == 1) {
                block.textLayout = textLayout;
                block.textYOffset = 0;
                block.charactersOffset = 0;
                blockHeight = textHeight;
            } else {
                int startCharacter = textLayout.getLineStart(linesOffset);
                int endCharacter = textLayout.getLineEnd(linesOffset + currentBlockLinesCount - 1);
                if (endCharacter < startCharacter) {
                    continue;
                }
                block.charactersOffset = startCharacter;
                try {
                    CharSequence str = messageText.subSequence(startCharacter, endCharacter);
                    block.textLayout = new StaticLayout(str, textPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    block.textYOffset = textLayout.getLineTop(linesOffset);
                    if (a != 0) {
                        blockHeight = Math.min(blockHeight, (int) (block.textYOffset - prevOffset));
                    }
                    prevOffset = block.textYOffset;
                    /*if (a != blocksCount - 1) {
                        int height = block.textLayout.getHeight();
                        blockHeight = Math.min(blockHeight, block.textLayout.getHeight());
                        prevOffset = block.textYOffset;
                    } else {
                        blockHeight = Math.min(blockHeight, (int)(block.textYOffset - prevOffset));
                    }*/
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                    continue;
                }
            }

            textLayoutBlocks.add(block);

            float lastLeft = block.textXOffset = 0;
            try {
                lastLeft = block.textXOffset = block.textLayout.getLineLeft(currentBlockLinesCount - 1);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }

            float lastLine = 0;
            try {
                lastLine = block.textLayout.getLineWidth(currentBlockLinesCount - 1);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }

            int linesMaxWidth = (int) Math.ceil(lastLine);
            int lastLineWidthWithLeft;
            int linesMaxWidthWithLeft;
            boolean hasNonRTL = false;

            if (a == blocksCount - 1) {
                lastLineWidth = linesMaxWidth;
            }

            linesMaxWidthWithLeft = lastLineWidthWithLeft = (int) Math.ceil(lastLine + lastLeft);
            if (lastLeft == 0) {
                hasNonRTL = true;
            }

            if (currentBlockLinesCount > 1) {
                float textRealMaxWidth = 0, textRealMaxWidthWithLeft = 0, lineWidth, lineLeft;
                for (int n = 0; n < currentBlockLinesCount; ++n) {
                    try {
                        lineWidth = block.textLayout.getLineWidth(n);
                    } catch (Exception e) {
                        FileLog.e("tmessages", e);
                        lineWidth = 0;
                    }

                    if (lineWidth > maxWidth + 100) {
                        lineWidth = maxWidth;
                    }

                    try {
                        lineLeft = block.textLayout.getLineLeft(n);
                    } catch (Exception e) {
                        FileLog.e("tmessages", e);
                        lineLeft = 0;
                    }

                    block.textXOffset = Math.min(block.textXOffset, lineLeft);

                    if (lineLeft == 0) {
                        hasNonRTL = true;
                    }
                    textRealMaxWidth = Math.max(textRealMaxWidth, lineWidth);
                    textRealMaxWidthWithLeft = Math.max(textRealMaxWidthWithLeft, lineWidth + lineLeft);
                    linesMaxWidth = Math.max(linesMaxWidth, (int) Math.ceil(lineWidth));
                    linesMaxWidthWithLeft = Math.max(linesMaxWidthWithLeft, (int) Math.ceil(lineWidth + lineLeft));
                }
                if (hasNonRTL) {
                    textRealMaxWidth = textRealMaxWidthWithLeft;
                    if (a == blocksCount - 1) {
                        lastLineWidth = lastLineWidthWithLeft;
                    }
                } else if (a == blocksCount - 1) {
                    lastLineWidth = linesMaxWidth;
                }
                textWidth = Math.max(textWidth, (int) Math.ceil(textRealMaxWidth));
            } else {
                textWidth = Math.max(textWidth, Math.min(maxWidth, linesMaxWidth));
            }

            if (hasNonRTL) {
                block.textXOffset = 0;
            }

            linesOffset += currentBlockLinesCount;
        }
        if (blockHeight == 0) {
            blockHeight = 1;
        }
    }

    public boolean isOut() {
        return messageOwner.direct == EMMessage.Direct.SEND;
    }

    public boolean isUnread() {
        return messageOwner.isUnread();
    }

    public boolean isContentUnread() {
        return messageOwner.isAcked();
    }


    public void setAcked(boolean isAcked) {
        messageOwner.setAcked(isAcked);
    }

    /**
     * 设置消息已读回执
     */
    public void setContentAcked() {
        boolean isAcked = true;
        try {
            EMChatManager.getInstance().ackMessageRead(messageOwner.getFrom(), messageOwner.getMsgId());
        } catch (EaseMobException e) {
            isAcked = false;
        }
        // 发送已读回执
        messageOwner.isAcked = isAcked;
    }

    public String getId() {
        return messageOwner.getMsgId();
    }


    public static void setUnreadFlags(EMMessage message, boolean unRead) {
        if (message != null) {
            message.setUnread(unRead);
        }
    }

    public static boolean isUnread(EMMessage message) {
        return message != null && message.isUnread();
    }

    public static boolean isContentUnread(EMMessage message) {
        return message != null && message.isAcked();
    }

    public static boolean isOut(EMMessage message) {
        return message != null && message.direct == EMMessage.Direct.SEND;
    }

    public String getDialogId() {
        return messageOwner.getMsgId();
    }

    public boolean isSending() {
        return messageOwner.status == EMMessage.Status.INPROGRESS;
    }

    public boolean isUnSend() {
        return messageOwner.status == EMMessage.Status.CREATE;
    }

    public boolean isSendError() {
        return messageOwner.status == EMMessage.Status.FAIL;
    }

    public boolean isSent() {
        return messageOwner.status == EMMessage.Status.SUCCESS;
    }

    public String getDocumentName() {
//        if (messageOwner.media != null && messageOwner.media.document != null) {
//            return FileLoader.getDocumentFileName(messageOwner.media.document);
//        }
        return "";
    }

    public String getStateDesc(){
        String state;
        if (isUnread()) {
            state = "未读";
        } else if (isDelivered()) {
            state = "已读";
        } else if (isSendError()) {
            state = "失败";
        } else if (isSending()) {
            state = "发送中...";
        }else {
            state = "";
        }
        return state;
    }

    public int getApproximateHeight() {
        if (contentType == 0 || contentType == 1) {
            return textHeight;
        } else if (contentType == 6 || contentType == 7 || contentType == 12 || contentType == 13 || contentType == 14 || contentType == 15) {
            return AndroidUtilities.dp(68);
        } else if (contentType == 3 || contentType == 4) {
            return AndroidUtilities.dp(100);
        } else if (contentType == 8 || contentType == 9 || contentType == 10 || contentType == 11) {
            return AndroidUtilities.dp(114);
        } else if (contentType == 2 || contentType == 5) {
            float maxHeight = AndroidUtilities.displaySize.y * 0.4f;
            float maxWidth;
            if (AndroidUtilities.isTablet()) {
                maxWidth = AndroidUtilities.getMinTabletSide() * 0.5f;
            } else {
                maxWidth = AndroidUtilities.displaySize.x * 0.5f;
            }
            int photoHeight = 0;
            int photoWidth = 0;
            if (photoWidth == 0) {
                photoHeight = (int) maxHeight;
                photoWidth = photoHeight + AndroidUtilities.dp(100);
            }
            if (photoHeight > maxHeight) {
                photoWidth *= maxHeight / photoHeight;
                photoHeight = (int) maxHeight;
            }
            if (photoWidth > maxWidth) {
                photoHeight *= maxWidth / photoWidth;
            }
            return photoHeight + AndroidUtilities.dp(14);
        } else {
            int photoHeight;
            int photoWidth;

            if (AndroidUtilities.isTablet()) {
                photoWidth = (int) (AndroidUtilities.getMinTabletSide() * 0.7f);
            } else {
                photoWidth = (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7f);
            }
            photoHeight = photoWidth + AndroidUtilities.dp(100);
            if (photoWidth > AndroidUtilities.getPhotoSize()) {
                photoWidth = AndroidUtilities.getPhotoSize();
            }
            if (photoHeight > AndroidUtilities.getPhotoSize()) {
                photoHeight = AndroidUtilities.getPhotoSize();
            }
            return photoHeight + AndroidUtilities.dp(14);
        }
    }

    public boolean isForwarded() {
        return false;//messageOwner.isDelivered();
    }

    /**
     * 是否送达
     *
     * @return
     */
    public boolean isDelivered() {
        return messageOwner.isDelivered();
    }


    public boolean isMediaEmpty() {
        return isMediaEmpty(messageOwner);
    }

    public static boolean isMediaEmpty(EMMessage message) {
        boolean isEmpty = true;
        if (message == null) {
            return isEmpty;
        }
        if (message.getType() == EMMessage.Type.TXT) {
            return isEmpty;
        } else {
            isEmpty = false;
        }
        return isEmpty;
    }

    public static int getContentType(EMMessage message) {
        if (message == null) {
            return -1;
        }
        if (message.getType() == EMMessage.Type.TXT) {
            if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
                return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
            else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.VIDEO) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }

        return -1;// invalid
    }

    public static final int MESSAGE_TYPE_RECV_TXT = 0;
    public static final int MESSAGE_TYPE_SENT_TXT = 1;
    public static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    public static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    public static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    public static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    public static final int MESSAGE_TYPE_SENT_VOICE = 6;
    public static final int MESSAGE_TYPE_RECV_VOICE = 7;
    public static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    public static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    public static final int MESSAGE_TYPE_SENT_FILE = 10;
    public static final int MESSAGE_TYPE_RECV_FILE = 11;
    public static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
    public static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
    public static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
    public static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;

}
