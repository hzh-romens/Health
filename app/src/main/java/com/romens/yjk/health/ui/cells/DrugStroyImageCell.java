package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/19.
 */
public class DrugStroyImageCell extends FrameLayout {

    private RecyclerView recyclerView;
    private ImageCellAdapter adapter;
    private List<ImageEntity> entities;

    public DrugStroyImageCell(Context context) {
        super(context);
        recyclerView = new RecyclerView(context);
        addView(recyclerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        entities = new ArrayList<>();
        initData();
        adapter = new ImageCellAdapter(context, entities);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            entities.add(new ImageEntity("testetesseess", R.drawable.ic_launcher));
        }
    }

    class ImageEntity {
        public String name;
        public int imageUrl;

        public ImageEntity(String name, int imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
        }
    }

    class ImageCellAdapter extends RecyclerView.Adapter<ADHolder> {

        private Context context;
        private List<ImageEntity> entities;

        public ImageCellAdapter(Context context, List<ImageEntity> entities) {
            this.context = context;
            this.entities = entities;
        }

        @Override
        public ADHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ADHolder(new ImageAndTextCell(context));
        }

        @Override
        public void onBindViewHolder(ADHolder holder, int position) {
            ImageAndTextCell cell = (ImageAndTextCell) holder.itemView;
            ImageEntity entity = entities.get(position);
            cell.setImageAndText(entity.imageUrl, entity.name);
        }

        @Override
        public int getItemCount() {
            return entities.size();
        }
    }

}
