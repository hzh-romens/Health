package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.model.LocationEntity;
import com.romens.yjk.health.ui.LocationActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BaseLocationAdapter extends BaseFragmentAdapter {

    public interface BaseLocationAdapterDelegate {
        void didLoadedSearchResult(ArrayList<LocationEntity> places);
    }

    private static final String POI_TYPE = "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";


    protected boolean searching;
    protected ArrayList<LocationEntity> places = new ArrayList<>();
    private Location lastSearchLocation;
    private BaseLocationAdapterDelegate delegate;
    private Timer searchTimer;

    protected Context mContext;
    protected boolean isDestroy = false;

    public enum SearchType {
        SHOP, USER
    }

    public BaseLocationAdapter(Context context) {
        this.mContext = context;
    }

    public void destroy() {
        isDestroy = true;
    }

    public void setDelegate(BaseLocationAdapterDelegate delegate) {
        this.delegate = delegate;
    }

    public void searchDelayed(final SearchType searchType, final String query, final Location coordinate) {
        if (query == null || query.length() == 0) {
            places.clear();
            notifyDataSetChanged();
        } else {
            try {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e("BaseLocationAdapter", e);
            }
            searchTimer = new Timer();
            searchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        searchTimer.cancel();
                        searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e("BaseLocationAdapter", e);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            lastSearchLocation = null;
                            if (searchType == SearchType.SHOP) {
                                searchMapPlacesWithServerQuery(query, coordinate);
                            } else {
                                searchMapPlacesWithQuery(query, coordinate);
                            }
                        }
                    });
                }
            }, 200, 500);
        }
    }

    public void searchMapPlacesWithQuery(final String query, final Location coordinate) {
        if (lastSearchLocation != null && coordinate.distanceTo(lastSearchLocation) < 100) {
            return;
        }
        lastSearchLocation = coordinate;
        if (searching) {
            searching = false;
        }
        try {
            searching = true;
            String poiTyp = TextUtils.isEmpty(query) ? POI_TYPE : "";
            PoiSearch.Query mapQuery = new PoiSearch.Query(query, poiTyp, "");
            mapQuery.setPageSize(30);// 设置每页最多返回多少条poiitem
            mapQuery.setPageNum(0);//设置查第一页
            PoiSearch poiSearch = new PoiSearch(mContext, mapQuery);
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(coordinate.getLatitude(),
                    coordinate.getLongitude()), 1000));//划定搜索的bound
            poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                @Override
                public void onPoiSearched(PoiResult poiResult, int i) {
                    if (isDestroy) {
                        return;
                    }
                    places.clear();
                    if (poiResult != null) {
                        ArrayList<PoiItem> poiItems = poiResult.getPois();
                        if (poiItems != null) {
                            for (PoiItem item : poiItems) {
                                places.add(new LocationEntity(item));
                            }
                        }
                        Collections.sort(places, new Comparator<LocationEntity>() {
                            @Override
                            public int compare(LocationEntity lhs, LocationEntity rhs) {
                                if (lhs.distance > rhs.distance) {
                                    return 1;
                                } else if (lhs.distance < rhs.distance) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
                        searching = false;
                        notifyDataSetChanged();
                        if (delegate != null) {
                            delegate.didLoadedSearchResult(places);
                        }
                    }
                }

                @Override
                public void onPoiItemSearched(PoiItem poiItem, int i) {

                }
            });//设置回调数据的监听器
            poiSearch.searchPOIAsyn();//开始搜索
        } catch (Exception e) {
            FileLog.e("BaseLocationAdapter", e);
            searching = false;
            if (delegate != null) {
                delegate.didLoadedSearchResult(places);
            }
        }
        notifyDataSetChanged();
    }

    // TODO: 附近药店获取药店列表信息
    public void searchMapPlacesWithServerQuery(final String query, final Location coordinate) {
        if (lastSearchLocation != null && coordinate.distanceTo(lastSearchLocation) < 100) {
            return;
        }
        lastSearchLocation = coordinate;
        if (searching) {
            searching = false;
        }
        searching = true;
        Map<String, Object> args = new HashMap<>();
        args.put("KEY", query);
        args.put("LONG", coordinate.getLongitude());
        args.put("LAT", coordinate.getLatitude());
        args.put("PAGE", 0);
        args.put("COUNT", 50);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetNearbyShops", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(LocationActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (isDestroy) {
                            return;
                        }
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            onResponseShops(response);
                        } else {
                            Toast.makeText(mContext, "查询发生异常,请稍候再试!", Toast.LENGTH_SHORT).show();
                        }
                        searching = false;
                        notifyDataSetChanged();
                        if (delegate != null) {
                            delegate.didLoadedSearchResult(places);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(mContext, connect);
//        Message message = new Message.MessageBuilder()
//                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
//                .build();
//        FacadeClient.request(mContext, message, new FacadeClient.FacadeCallback() {
//            @Override
//            public void onTokenTimeout(Message msg) {
//                Log.e("GetDiscoveryData", "ERROR");
//            }
//
//            @Override
//            public void onResult(Message msg, Message errorMsg) {
//
//            }
//        });
        notifyDataSetChanged();
    }

    private void onResponseShops(JsonNode data) {
        places.clear();
        if (data != null) {
            int size = data.size();
            for (int i = 0; i < size; i++) {
                places.add(LocationEntity.jsonToEntity(data.get(i)));
            }
        }
        Collections.sort(places, new Comparator<LocationEntity>() {
            @Override
            public int compare(LocationEntity lhs, LocationEntity rhs) {
                if (lhs.distance > rhs.distance) {
                    return 1;
                } else if (lhs.distance < rhs.distance) {
                    return -1;
                }
                return 0;
            }
        });
    }
}
