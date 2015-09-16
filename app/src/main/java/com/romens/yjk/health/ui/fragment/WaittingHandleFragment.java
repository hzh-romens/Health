package com.romens.yjk.health.ui.fragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.support.widget.LinearLayoutManager;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DiscoveryDao;
import com.romens.yjk.health.db.entity.DiscoveryEntity;
import com.romens.yjk.health.model.OrderEntity;
import com.romens.yjk.health.ui.adapter.CompleteAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
import com.romens.yjk.health.ui.components.OnRecyclerViewScrollListener;
import com.romens.yjk.health.ui.components.OnRecyclerViewScrollLocationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AUSU on 2015/9/9.
 */
/**
 * Created by AUSU on 2015/9/9.
 * 订单待评价页面
 */
public class WaittingHandleFragment extends BaseFragment{
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CompleteAdapter completeAdapter;
    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        swipeRefreshLayout= (SwipeRefreshLayout)root.findViewById(R.id.swiperefreshlayout);
        recyclerView= (RecyclerView)root.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //下拉刷新加载
            @Override
            public void onRefresh() {
                requestCompleteDataChanged();
            }
        });

        final ABaseLinearLayoutManager layoutManager=new ABaseLinearLayoutManager(getActivity(), android.support.v7.widget.LinearLayoutManager.VERTICAL,false);


        layoutManager.setOnRecyclerViewScrollLocationListener(recyclerView, new OnRecyclerViewScrollLocationListener() {
            @Override
            public void onTopWhenScrollIdle(RecyclerView recyclerView) {

            }

            @Override
            public void onBottomWhenScrollIdle(RecyclerView recyclerView) {

                //跑一个线程，获取下一页数据

            }
        });

        layoutManager.getRecyclerViewScrollManager().addScrollListener(recyclerView, new OnRecyclerViewScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        //获取数据


        //加分割
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                //   int curPosition = layoutManager.findFirstVisibleItemPosition();
                // View curItemView = layoutManager.findViewByPosition(curPosition);
                // LayoutInflater.from(getActivity()).inflate(R.layo.);
                Paint paint = new Paint();
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
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = AndroidUtilities.dp(1);
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        // requestCompleteDataChanged();
        List<OrderEntity> datas=new ArrayList<OrderEntity>();
        OrderEntity orderEntity=new OrderEntity();
        orderEntity.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
        orderEntity.setComment("评价");
        orderEntity.setName("四季感冒药");
        datas.add(orderEntity);
        OrderEntity orderEntity1=new OrderEntity();
        orderEntity1.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
        orderEntity1.setComment("评价");
        orderEntity1.setName("四季感冒药");
        datas.add(orderEntity1);
        OrderEntity orderEntity2=new OrderEntity();
        orderEntity2.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
        orderEntity2.setComment("评价");
        orderEntity2.setName("四季感冒药");
        datas.add(orderEntity2);
        OrderEntity orderEntity3=new OrderEntity();
        orderEntity3.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
        orderEntity3.setComment("评价");
        orderEntity3.setName("四季感冒药");
        datas.add(orderEntity3);
        completeAdapter=new CompleteAdapter(getActivity());
        completeAdapter.BindValue(datas);
        recyclerView.setAdapter(completeAdapter);
        return root;
    }
    //加载数据
    private void requestCompleteDataChanged() {
        int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("LASTTIME", lastTime).put("STATE", "16").put("TYPE","").put("PAGE","").put("COUNT","").build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "handle", "GetUserOrderListInfo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        final Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {

            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetUserOrderListInfo", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                swipeRefreshLayout.setRefreshing(false);
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    onResponseOrderData(responseProtocol.getResponse());
                } else {
                    Log.e("GetUserOrderListInfo", "ERROR");
                }
            }
        });
    }

    public void onResponseOrderData(List<LinkedTreeMap<String, String>> OrderData) {
        int count = OrderData == null ? 0 : OrderData.size();
        if (count <= 0) {
            return;
        }
        // for (int )
        // Iterator  iterator = hashMap.keySet().iterator();
        //while (iterator.hasNext()) {
        //2、拼接字符串
        //   str += hashMap.get(iterator.next()).toString()+",";
        //}
        ArrayList<OrderEntity> needDb = new ArrayList<>();
        for (LinkedTreeMap<String, String> item : OrderData) {
            OrderEntity entity = OrderEntity.mapToEntity(item);
            needDb.add(entity);
        }
        //if (needDb.size() > 0) {
        //  DiscoveryDao userDao =DBInterface.instance().openWritableDb().getDiscoveryDao();
        //userDao.insertOrReplaceInTx(needDb);
        //}
        List<OrderEntity> datas=new ArrayList<OrderEntity>();
        OrderEntity orderEntity=new OrderEntity();
        orderEntity.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
        orderEntity.setComment("评价");
        orderEntity.setName("四季感冒药");
        datas.add(orderEntity);
        OrderEntity orderEntity1=new OrderEntity();
        orderEntity1.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
        orderEntity1.setComment("评价");
        orderEntity1.setName("四季感冒药");
        datas.add(orderEntity1);
        OrderEntity orderEntity2=new OrderEntity();
        orderEntity2.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
        orderEntity2.setComment("评价");
        orderEntity2.setName("四季感冒药");
        datas.add(orderEntity2);
        OrderEntity orderEntity3=new OrderEntity();
        orderEntity3.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
        orderEntity3.setComment("评价");
        orderEntity3.setName("四季感冒药");
        datas.add(orderEntity3);
        completeAdapter=new CompleteAdapter(getActivity());
        completeAdapter.BindValue(datas);
        recyclerView.setAdapter(completeAdapter);

    }


    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }
}