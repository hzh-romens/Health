package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.EditTagCell;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.components.FlowLayoutCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/14.
 */
public class FeedBackActivity extends BaseActivity implements View.OnClickListener {

    private FlowLayout tagFlowLayout;
    private FlowLayout selectTagFlowLayout;
    private EditTagCell editTagTxt;
    private EditText feedBackInfo;
    private ImageView firstUpLoadImg;
    private ImageView secondUpLoadImg;
    private ImageView threeUpLoadImg;
    private TextView submitBtn;
    private TextView resetBtn;
    private ActionBar actionBar;
    private FrameLayout feedBackSelectTagLayout;

    private List<String> selectTagTxtList;
    private List<String> tagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back, R.id.feed_back_action);
        actionBarEvent();
        initData();
        tagFlowLayout = (FlowLayout) findViewById(R.id.feed_back_flow_layout);
//        selectTagFlowLayout = (FlowLayout) findViewById(R.id.feed_back_flow_layout_select);
        feedBackSelectTagLayout = (FrameLayout) findViewById(R.id.feed_back_select_tag_layout);
        editTagTxt = (EditTagCell) findViewById(R.id.feed_back_edit_tag);
        feedBackInfo = (EditText) findViewById(R.id.feed_back_info);
        firstUpLoadImg = (ImageView) findViewById(R.id.feed_back_first_img);
        secondUpLoadImg = (ImageView) findViewById(R.id.feed_back_second_img);
        threeUpLoadImg = (ImageView) findViewById(R.id.feed_back_three_img);
        submitBtn = (TextView) findViewById(R.id.feed_back_submit);
        resetBtn = (TextView) findViewById(R.id.feed_back_reset);

        selectTagFlowLayout = new FlowLayout(this);
        flowlayoutEvent(tagFlowLayout, selectTagFlowLayout);
//        flowlayoutEvent(tagFlowLayout);
        editTagEvent();
        firstUpLoadImg.setOnClickListener(this);
        secondUpLoadImg.setOnClickListener(this);
        threeUpLoadImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_back_first_img:
                Toast.makeText(FeedBackActivity.this, "firstImgClick", Toast.LENGTH_SHORT).show();
                break;
            case R.id.feed_back_second_img:
                Toast.makeText(FeedBackActivity.this, "secondImgClick", Toast.LENGTH_SHORT).show();
                break;
            case R.id.feed_back_three_img:
                Toast.makeText(FeedBackActivity.this, "threeImgClick", Toast.LENGTH_SHORT).show();
                break;
            case R.id.feed_back_submit:
                Toast.makeText(FeedBackActivity.this, "submitClick", Toast.LENGTH_SHORT).show();
                break;
            case R.id.feed_back_reset:
                initData();
                feedBackSelectTagLayout.removeAllViews();
                tagFlowLayout.updateLayout();
                selectTagFlowLayout.updateLayout();
                editTagTxt.setEditViewText("");
                feedBackInfo.setText("");
                break;
        }
    }

    private void editTagEvent() {
        editTagTxt.setRightViewText("贴上", false);
        editTagTxt.setOnBtnClickListener(new EditTagCell.onBtnClickListener() {
            @Override
            public void onClick() {
                selectTagTxtList.add(editTagTxt.getEditText());
                selectTagFlowLayout.updateLayout();
                editTagTxt.setEditViewText("");
            }

            @Override
            public void editTextChange() {
            }
        });
    }

    private void flowlayoutEvent(final FlowLayout tagFlowLayout, final FlowLayout selectTaglayout) {
        tagFlowLayout.setHorizontalSpacing(AndroidUtilities.dp(4));
        tagFlowLayout.setVerticalSpacing(AndroidUtilities.dp(4));
        tagFlowLayout.setAdapter(new FlowLayoutCallback() {
            @Override
            public int getCount() {
                return tagList.size();
            }

            @Override
            public View getView(final int position, ViewGroup container) {
                TextView textView = new TextView(FeedBackActivity.this);
                textView.setBackgroundResource(R.drawable.bg_light_gray);
                textView.setText(tagList.get(position));
                textView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tagList.get(position).equals("      ")) {
                            return;
                        }
                        if (feedBackSelectTagLayout.getChildCount() < 1) {
                            feedBackSelectTagLayout.addView(selectTaglayout);
                        }
                        selectTagTxtList.add(tagList.get(position));
                        if (selectTagTxtList.get(0).equals("      ")) {
                            selectTagTxtList.remove(0);
                        }
                        selectTagLayoutEvent(selectTaglayout);
                        tagList.remove(position);
                        if (tagList.size() <= 0) {
                            tagList.add("      ");
                        }
                        tagFlowLayout.updateLayout();
                    }
                });
                return textView;
            }
        });
        tagFlowLayout.updateLayout();
    }

    private void selectTagLayoutEvent(final FlowLayout selectTaglayout) {
        selectTaglayout.setHorizontalSpacing(AndroidUtilities.dp(4));
        selectTaglayout.setVerticalSpacing(AndroidUtilities.dp(4));
        selectTaglayout.setAdapter(new FlowLayoutCallback() {
            @Override
            public int getCount() {
                return selectTagTxtList.size();
            }

            @Override
            public View getView(final int position, ViewGroup container) {
                TextView textView = new TextView(FeedBackActivity.this);
                textView.setBackgroundResource(R.drawable.btn_primary_default);
                textView.setText(selectTagTxtList.get(position));
                textView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectTagTxtList.get(position).equals("      ")) {
                            return;
                        }
                        tagList.add(selectTagTxtList.get(position));
                        if (tagList.get(0).equals("      ")) {
                            tagList.remove(0);
                        }
                        selectTagTxtList.remove(position);
                        if (selectTagTxtList.size() <= 0) {
                            selectTagTxtList.add("      ");
                        }
                        selectTaglayout.updateLayout();
                        tagFlowLayout.updateLayout();
                    }
                });
                return textView;
            }
        });
        selectTaglayout.updateLayout();
    }

    private void initData() {
        selectTagTxtList = new ArrayList<>();
        tagList = new ArrayList<>();
        tagList.add("服务");
        tagList.add("药品");
        tagList.add("药师");
        tagList.add("医师");
    }

    private void actionBarEvent() {
        actionBar = getMyActionBar();
        actionBar.setTitle("意见反馈");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (i == -1) {
                    finish();
                }
            }
        });
    }


}
