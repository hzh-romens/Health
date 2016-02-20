package com.romens.yjk.health.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.extend.scanner.CaptureActivity;
import com.romens.extend.scanner.ViewfinderView;
import com.romens.extend.scanner.model.MenuModel;
import com.romens.extend.scanner.params.DecodeParams;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.ScannerMenuCell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Lisi
 * @create 16/2/20
 * @description
 */
public class ScannerNewActivity extends CaptureActivity {
    private static final int PRIMARY_COLOR = 0Xff0f9d58;
    private static final int DefaultHighlightColor = 0xff2baf2b;
    private final List<MenuModel> menuModels = new ArrayList<>();
    private final SparseArray<ScannerMenuCell> menuCells = new SparseArray<>();
    private int selectedPosition = -1;
    //private ImageView helperImageView;
    private TextView helperTextView;

    private ActionBar actionBar;
    private ActionBarMenuItem inputSearchItem;

    @Override
    protected void onSetupContentView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(PRIMARY_COLOR);
        }
        FrameLayout content = new FrameLayout(this);
        surfaceView = new SurfaceView(this);
        content.addView(surfaceView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        viewfinderView = new ViewfinderView(this, null);
        viewfinderView.setLaserColor(DefaultHighlightColor);
        viewfinderView.setResultPointColor(DefaultHighlightColor);
        viewfinderView.setBorderColor(DefaultHighlightColor);
        content.addView(viewfinderView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));


        LinearLayout topContainer = new LinearLayout(this);
        topContainer.setOrientation(LinearLayout.VERTICAL);
        content.addView(topContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));


        actionBar = new ActionBar(this);
        actionBar.setBackgroundColor(PRIMARY_COLOR);
        actionBar.setItemsBackground(R.drawable.bar_selector);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setTitle("扫码识药");
        topContainer.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });

        ActionBarMenu actionBarMenu = actionBar.createMenu();
        inputSearchItem = actionBarMenu.addItem(0, R.drawable.ic_edit_white_24dp).setIsSearchField(true, true);
        inputSearchItem.getSearchField().setHint("请输入...");
        inputSearchItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

            @Override
            public boolean canCollapseSearch() {
                return true;
            }

            @Override
            public void onSearchPressed(EditText var1) {
                String searchText = var1.getText().toString().trim();
                if (!searchText.equals("") && searchText != null) {
                    if (onScannerResultCompleted(searchText)) {
                        finish();
                    }
                }
            }
        });

        FrameLayout helperContainer = new FrameLayout(this);
        helperContainer.setBackgroundColor(Color.WHITE);
//        helperImageView = new ImageView(this);
//        helperImageView.setScaleType(ImageView.ScaleType.CENTER);
//        helperImageView.setVisibility(View.INVISIBLE);
//        helperContainer.addView(helperImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 96, Gravity.TOP, 16, 8, 16, 8));

        helperTextView = new TextView(this);
        helperTextView.setTextColor(0xff808080);
        helperTextView.setTextSize(18);
        helperTextView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        helperTextView.setGravity(Gravity.CENTER);
        helperContainer.addView(helperTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 80, Gravity.TOP, 16, 8, 16, 8));

        topContainer.addView(helperContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));


        LinearLayout bottomLayout = new LinearLayout(this);
        bottomLayout.setBackgroundColor(Color.WHITE);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setGravity(Gravity.CENTER_VERTICAL);
        bottomLayout.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10));
        content.addView(bottomLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
        setContentView(content);

        menuModels.clear();
        menuCells.clear();
        initMenuData();
        int size = menuModels.size();
        for (int i = 0; i < size; i++) {
            addMenuCell(bottomLayout, i);
        }
        onMenuCellSelected(0);
    }

    @Override
    public void onBackPressed() {
        if (actionBar.isSearchFieldVisible()) {
            actionBar.closeSearchField();
        } else {
            finish();
        }
    }

    /**
     * 初始化需要添加的菜单选项集合
     */
    private void initMenuData() {
        MenuModel barModel = new MenuModel();
        barModel.setResId(R.drawable.ic_scanner_bar_code);
        barModel.setName("条形码");
        barModel.setDecodeType(DecodeParams.TYPE_BAR_CODE);
        menuModels.add(barModel);

        MenuModel qrModel = new MenuModel();
        qrModel.setResId(R.drawable.ic_scanner_qrcode);
        qrModel.setName("二维码");
        qrModel.setDecodeType(DecodeParams.TYPE_QR_CODE);
        menuModels.add(qrModel);

//        MenuModel streetModel = new MenuModel();
//        streetModel.setResId(R.drawable.ic_scanner_drug_code);
//        streetModel.setName("电子监管码");
//        streetModel.setDecodeType(DecodeParams.TYPE_BAR_CODE);
//        menuModels.add(streetModel);
    }

    /**
     * 添加按钮
     *
     * @param bottomLayout
     */
    private void addMenuCell(LinearLayout bottomLayout, final int position) {
        ScannerMenuCell menuCell = new ScannerMenuCell(this);
        bottomLayout.addView(menuCell);
        LinearLayout.LayoutParams childParams = (LinearLayout.LayoutParams) menuCell.getLayoutParams();
        childParams.width = 0;
        childParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        childParams.weight = 1;
        menuCell.setLayoutParams(childParams);
        menuCell.setDefaultColor(0xff656565);
        menuCell.setHighlightColor(DefaultHighlightColor);

        MenuModel menuModel = menuModels.get(position);
        menuCell.setValue(menuModel.getResId(), menuModel.getName());
        menuCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuCellSelected(position);
            }
        });
        menuCells.append(position, menuCell);
    }

    private void onMenuCellSelected(int position) {
        if (selectedPosition != position) {
            MenuModel menuModel = menuModels.get(position);
            viewfinderView.changeDecodeType(menuModel.getDecodeType());
            if (selectedPosition >= 0) {
                menuCells.get(selectedPosition).setChecked(false);
            }
            selectedPosition = position;
            menuCells.get(selectedPosition).setChecked(true);
            changeHelperText(position);
        }

    }

    private void changeHelperText(int position) {
        if (position == 1) {
            inputSearchItem.setVisibility(View.GONE);
            helperTextView.setText("扫描药品标识二维码");
        } else {
            inputSearchItem.setVisibility(View.VISIBLE);
            String helperText;
            if (position == 2) {
                helperText = "点击右上角 # 手动输入药盒上的电子监管码";
            } else {
                helperText = "点击右上角 # 手动输入药盒上的条码";
            }
            int index = helperText.indexOf("#");
            SpannableString helperSpan = new SpannableString(helperText);
            helperSpan.setSpan(new ImageSpan(this, R.drawable.ic_edit_grey600_24dp), index, index + 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            helperTextView.setText(helperSpan);
        }
    }

    @Override
    public boolean onScannerResultCompleted(String data) {
        if (selectedPosition == 0) {
            Intent intent = new Intent(ScannerNewActivity.this, SearchActivity.class);
            intent.putExtra(SearchActivity.ARGUMENTS_SEARCH_QUERY_TEXT, data);
            intent.putExtra(SearchActivity.ARGUMENTS_SEARCH_TYPE, SearchActivity.SEARCH_TYPE_BARCODE);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
