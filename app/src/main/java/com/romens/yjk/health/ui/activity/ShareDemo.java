package com.romens.yjk.health.ui.activity;

/**
 * @author Zhou Lisi
 * @create 16/2/29
 * @description
 */
public class ShareDemo {
//    private void showShareMenu() {
//        shareDialog = new BottomSheet.Builder(this)
//                .setIsGrid(true)
//                .setTitle("分享")
//                .setItems(new CharSequence[]{"发送朋友", "分享朋友圈", "收藏"}, new int[]{R.drawable.icon_wx_logo, R.drawable.icon_wx_moments, R.drawable.icon_wx_collect}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, final int which) {
//                        if (WXHelper.checkWXApp()) {
//                            if (shareArguments != null) {
//                                final String shareURL = shareArguments.getString("url");
//                                final String shareTitle = shareArguments.getString("title");
//                                final String shareDesc = shareArguments.getString("desc");
//                                if (shareArguments.containsKey("thumb")) {
//                                    byte[] shareThumb = shareArguments.getByteArray("thumb");
//                                    shareToWX(which, shareURL, shareTitle, shareDesc, shareThumb);
//                                } else if (shareArguments.containsKey("thumbURL")) {
//                                    new ThumbURLAsyncTask(which, shareURL, shareTitle, shareDesc).execute(shareArguments.getString("thumbURL", ""));
//                                }
//                            }
//                        } else {
//                            ToastCell.toast(WebActivity.this, "未安装微信");
//                        }
//                    }
//                }).create();
//        shareDialog.show();
//    }




//    /**
//     * 异步获取封面图标
//     */
//    private class ThumbURLAsyncTask extends AsyncTask<String, Void, byte[]> {
//        private final int which;
//        private final String shareURL;
//        private final String shareTitle;
//        private final String shareDesc;
//
//        public ThumbURLAsyncTask(int which, String url, String title, String desc) {
//            this.which = which;
//            this.shareURL = url;
//            this.shareTitle = title;
//            this.shareDesc = desc;
//        }
//
//        @Override
//        protected byte[] doInBackground(String... params) {
//            final byte[] shareThumb = WXHelper.getHtmlByteArray(params[0]);
//            return shareThumb;
//        }
//
//        @Override
//        protected void onPostExecute(byte[] result) {
//            shareToWX(which, shareURL, shareTitle, shareDesc, result);
//        }
//    }
//
//    private void shareToWX(int type, String shareURL, String shareTitle, String shareDesc, byte[] shareThumb) {
//        if (type == 0) {
//            WXManager.getInstance(WebActivity.this).shareURLToUser(shareURL, shareTitle, shareDesc, shareThumb);
//        } else if (type == 1) {
//            WXManager.getInstance(WebActivity.this).shareURLToTimeline(shareURL, shareTitle, shareDesc, shareThumb);
//        } else if (type == 2) {
//            WXManager.getInstance(WebActivity.this).shareURLToFavorite(shareURL, shareTitle, shareDesc, shareThumb);
//        }
//    }
}
