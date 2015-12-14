package com.romens.yjk.health.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.extend.scanner.CaptureActivity;
import com.romens.extend.scanner.ViewfinderView;
import com.romens.extend.scanner.model.MenuModel;
import com.romens.extend.scanner.params.DecodeParams;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.ScannerMenuCell;

import java.util.ArrayList;
import java.util.List;

public class ScannerActivity extends CaptureActivity {
    private static final int DefaultHighlightColor = 0xff0f9d58;
    private final List<MenuModel> menuModels = new ArrayList<>();
    private final SparseArray<ScannerMenuCell> menuCells = new SparseArray<>();
    private int selectedPosition = -1;

    @Override
    protected void onSetupContentView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        FrameLayout contentView = new FrameLayout(this);
        surfaceView = new SurfaceView(this);
        contentView.addView(surfaceView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        viewfinderView = new ViewfinderView(this, null);
        viewfinderView.setLaserColor(DefaultHighlightColor);
        viewfinderView.setResultPointColor(DefaultHighlightColor);
        viewfinderView.setBorderColor(DefaultHighlightColor);
        contentView.addView(viewfinderView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        ActionBar actionBar = new ActionBar(this);
        actionBar.setBackgroundColor(Color.TRANSPARENT);
        actionBar.setItemsBackground(R.drawable.bar_selector);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle("");
        contentView.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });

        LinearLayout bottomLayout = new LinearLayout(this);
        bottomLayout.setBackgroundColor(Color.BLACK);
        bottomLayout.setAlpha(0.8f);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setGravity(Gravity.CENTER_VERTICAL);
        contentView.addView(bottomLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 100, Gravity.BOTTOM));
        setContentView(contentView);

        menuModels.clear();
        menuCells.clear();
        initMenuData();
        int size = menuModels.size();
        for (int i = 0; i < size; i++) {
            addMenuCell(bottomLayout, i);
        }
        onMenuCellSelected(0);
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

        MenuModel streetModel = new MenuModel();
        streetModel.setResId(R.drawable.ic_scanner_drug_code);
        streetModel.setName("电子监管码");
        streetModel.setDecodeType(DecodeParams.TYPE_BAR_CODE);
        menuModels.add(streetModel);
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
        }
    }

    @Override
    public boolean onScannerResultCompleted(String data) {
        if (selectedPosition == 0) {
            Intent intent = new Intent(ScannerActivity.this, SearchActivity.class);
            intent.putExtra(SearchActivity.ARGUMENTS_SEARCH_QUERY_TEXT, data);
            intent.putExtra(SearchActivity.ARGUMENTS_SEARCH_TYPE, SearchActivity.SEARCH_TYPE_BARCODE);
            startActivity(intent);
            return true;
        }
        //Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        return false;
    }
}
