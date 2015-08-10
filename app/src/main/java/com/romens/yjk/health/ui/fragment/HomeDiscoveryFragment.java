package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.DiscoveryCollection;
import com.romens.yjk.health.db.entity.DiscoveryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by siery on 15/8/10.
 */
public class HomeDiscoveryFragment extends BaseFragment {
    private RecyclerView gridView;
    private DiscoveryAdapter discoveryAdapter;

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_discovery, container, false);
        gridView = (RecyclerView) root.findViewById(R.id.list);
        int count = AndroidUtilities.getRealScreenSize().x / AndroidUtilities.dp(96);
        gridView.setLayoutManager(new GridLayoutManager(getActivity(), count, GridLayoutManager.VERTICAL, false));
        return root;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        gridView.addItemDecoration(new GridItemDecoration());
    }

    class GridItemDecoration extends RecyclerView.ItemDecoration {
        final Paint paint = new Paint();

        public GridItemDecoration() {
            paint.setColor(0xffdcdcdc);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                float x = child.getWidth() + child.getX();
                float y = child.getHeight() + child.getY();
                c.drawLine(child.getX(),
                        y,
                        child.getX() + child.getWidth(),
                        y,
                        paint);
                c.drawLine(x,
                        child.getY(),
                        x,
                        child.getY() + child.getHeight(),
                        paint);
            }
        }
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        discoveryAdapter = new DiscoveryAdapter(getActivity());
        gridView.setAdapter(discoveryAdapter);
        bindData();
        //requestDiscoveryDataChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class DiscoveryAdapter extends RecyclerView.Adapter<DiscoveryItemHolder> {

        private Context mContext;
        private final List<DiscoveryEntity> entities = new ArrayList<>();

        public DiscoveryAdapter(Context context) {
            this.mContext = context;
        }

        public void clear() {
            entities.clear();
        }

        public void putDefaultEntities(List<DiscoveryEntity> list) {
            entities.clear();
            if (list != null && list.size() > 0) {
                entities.addAll(list);
            }
        }

        public DiscoveryEntity getItem(int position) {
            return entities.get(position);
        }

        @Override
        public DiscoveryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_discovery, parent, false);
            return new DiscoveryItemHolder(view);
        }

        @Override
        public void onBindViewHolder(DiscoveryItemHolder holder, int position) {
            final DiscoveryEntity item = getItem(position);
            holder.iconView.setImageBitmap(ImageUtils.bindLocalImage(item.getIconRes()));
            Drawable defaultDrawable = holder.iconView.getDrawable();
            ImageManager.loadForView(mContext,holder.iconView,item.getIconUrl(),defaultDrawable,defaultDrawable);
            holder.iconView.setColorFilter(item.getPrimaryColor());
            holder.nameView.setText(item.getName());
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscoveryCollection.onDiscoveryItemAction(mContext, item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return entities.size();
        }

    }

    static class DiscoveryItemHolder extends RecyclerView.ViewHolder {

        public final View card;
        public final ImageView iconView;
        public final TextView nameView;

        public DiscoveryItemHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            iconView = (ImageView) itemView.findViewById(R.id.icon);
            nameView = (TextView) itemView.findViewById(R.id.name);
        }
    }

    private void requestDiscoveryDataChanged() {
        int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("LASTTIME", lastTime).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "handle", "GetDiscoveryData", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetDiscoveryData", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    onResponseAllDiscovery(responseProtocol.getResponse());
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    public void onResponseAllDiscovery(List<LinkedTreeMap<String, String>> discoveryData) {
        int count = discoveryData == null ? 0 : discoveryData.size();
        if (count <= 0) {
            return;
        }
        ArrayList<DiscoveryEntity> needDb = new ArrayList<>();
        for (LinkedTreeMap<String, String> item : discoveryData) {
            DiscoveryEntity entity = DiscoveryEntity.mapToEntity(item);
            needDb.add(entity);
        }
        bindData();
    }

    private void bindData() {
        List<DiscoveryEntity> discoveryEntities = DBInterface.instance().loadAllDiscovery();
        discoveryAdapter.putDefaultEntities(discoveryEntities);
        discoveryAdapter.notifyDataSetChanged();
    }
}

