package com.romens.yjk.health.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.MapsInitializer;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.core.LocationHelper;
import com.romens.yjk.health.model.LocationEntity;
import com.romens.yjk.health.ui.activity.BaseLocationActivity;
import com.romens.yjk.health.ui.adapter.BaseLocationAdapter;
import com.romens.yjk.health.ui.adapter.LocationActivityAdapter;
import com.romens.yjk.health.ui.adapter.LocationActivitySearchAdapter;
import com.romens.yjk.health.ui.components.MapPlaceholderDrawable;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends BaseLocationActivity {

    public static final String ARGUMENT_KEY_QUERY_KEYWORD = "query_keyword";
    public static final String ARGUMENT_KEY_TARGET_LOCATION_LAT = "target_location_lat";
    public static final String ARGUMENT_KEY_TARGET_LOCATION_LON = "target_location_lon";
    public static final String ARGUMENT_KEY_TARGET_LOCATION_ADDRESS = "target_location_address";
    public static final String ARGUMENT_KEY_FROM_USER = "frome_user";

    private FrameLayout fragmentView;
    private AMap aMap;
    private TextView distanceTextView;
    private BackupImageView avatarImageView;
    private TextView nameTextView;
    private MapView mapView;
    private FrameLayout mapViewClip;
    private LocationActivityAdapter adapter;
    private ListView listView;
    private ListView searchListView;
    private LocationActivitySearchAdapter searchAdapter;
    private LinearLayout emptyTextLayout;
    private ImageView markerImageView;
    private ImageView markerXImageView;
    private ImageView locationButton;

    private AnimatorSet animatorSet;

    private boolean searching;
    private boolean searchWas;

    private boolean wasResults;

    private Location myLocation;
    private Location userLocation;
    private int markerTop;

    private boolean userLocationMoved = false;
    private boolean firstWas = false;

    private int overScrollHeight = AndroidUtilities.displaySize.x - AndroidUtilities.getCurrentActionBarHeight() - AndroidUtilities.dp(66);
    private int halfHeight;

    private final static int share = 1;
    private final static int map_list_menu_map = 2;
    private final static int map_list_menu_satellite = 3;

    private static final String MAP_SEARCH_DEFAULT_KEY = "";


    private String currQueryKeyword = MAP_SEARCH_DEFAULT_KEY;
    private boolean isReadOnly = false;
    private double targetLocationLat = 0;
    private double targetLocationLon = 0;
    private String targetLocationAddress;


    private ActionBarMenuItem otherMenuItem;

    private BaseLocationAdapter.SearchType searchType = BaseLocationAdapter.SearchType.SHOP;

    @Override
    public void onDestroy() {
        ConnectManager.getInstance().destroyInitiator(LocationActivity.class);
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (adapter != null) {
            adapter.destroy();
        }
        if (searchAdapter != null) {
            searchAdapter.destroy();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getIntent().getExtras();
        boolean fromUser = false;
        if (arguments != null) {
            currQueryKeyword = arguments.getString(ARGUMENT_KEY_QUERY_KEYWORD, MAP_SEARCH_DEFAULT_KEY);
            targetLocationLat = arguments.getDouble(ARGUMENT_KEY_TARGET_LOCATION_LAT, 0);
            targetLocationLon = arguments.getDouble(ARGUMENT_KEY_TARGET_LOCATION_LON, 0);
            targetLocationAddress = arguments.getString(ARGUMENT_KEY_TARGET_LOCATION_ADDRESS, "");
            fromUser = arguments.getBoolean(ARGUMENT_KEY_FROM_USER, false);
        }
        searchType = fromUser ? BaseLocationAdapter.SearchType.USER : BaseLocationAdapter.SearchType.SHOP;
        isReadOnly = !TextUtils.isEmpty(targetLocationAddress);
        final Context context = this;

        final ActionBar actionBar = new ActionBar(this);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    setResult(RESULT_CANCELED);
                    finish();
                } else if (id == map_list_menu_map) {
                    if (aMap != null) {
                        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                    }
                } else if (id == map_list_menu_satellite) {
                    if (aMap != null) {
                        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                    }
                } else if (id == share) {
//                    try {
//                        double lat = messageObject.messageOwner.media.geo.lat;
//                        double lon = messageObject.messageOwner.media.geo._long;
//                        getParentActivity().startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
//                    } catch (Exception e) {
//                        FileLog.e("tmessages", e);
//                    }
                }
            }
        });
//
        ActionBarMenu menu = actionBar.createMenu();
        if (isReadOnly) {
            actionBar.setTitle("药店位置");
            menu.addItem(share, R.drawable.share);
        } else {
            if (searchType == BaseLocationAdapter.SearchType.SHOP) {
                actionBar.setTitle("附近药店");
            } else {
                actionBar.setTitle("送达药品至...");
            }

            ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                @Override
                public void onSearchExpand() {
                    searching = true;
                    listView.setVisibility(View.GONE);
                    mapViewClip.setVisibility(View.GONE);
                    searchListView.setVisibility(View.VISIBLE);
                    searchListView.setEmptyView(emptyTextLayout);
                    if (otherMenuItem != null) {
                        otherMenuItem.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onSearchCollapse() {
                    searching = false;
                    searchWas = false;
                    searchListView.setEmptyView(null);
                    listView.setVisibility(View.VISIBLE);
                    mapViewClip.setVisibility(View.VISIBLE);
                    searchListView.setVisibility(View.GONE);
                    emptyTextLayout.setVisibility(View.GONE);
                    searchAdapter.searchDelayed(searchType, null, null);
                    if (otherMenuItem != null) {
                        otherMenuItem.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onTextChanged(EditText editText) {
                    if (searchAdapter == null) {
                        return;
                    }
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
                        searchWas = true;
                    }
                    searchAdapter.searchDelayed(searchType, text, userLocation);
                }
            });
            item.getSearchField().setHint(searchType == BaseLocationAdapter.SearchType.USER ? "输入地点名称" : "输入药店名称");
        }

        otherMenuItem = menu.addItem(0, R.drawable.ic_ab_other);
        otherMenuItem.addSubItem(map_list_menu_map, "地图模式", 0);
        otherMenuItem.addSubItem(map_list_menu_satellite, "卫星模式", 0);
        fragmentView = new FrameLayout(context) {
            private boolean first = true;

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);

                if (changed) {
                    fixLayoutInternal(first);
                    first = false;
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        locationButton = new ImageView(context);
        locationButton.setBackgroundResource(R.drawable.floating_user_states);
        locationButton.setImageResource(R.drawable.myloc_on);
        locationButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(locationButton, "translationZ", AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(locationButton, "translationZ", AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
            locationButton.setStateListAnimator(animator);
            locationButton.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                    }
                }
            });
        }
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(context);
        if (isReadOnly) {
            content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            setContentView(content, actionBar);
            content.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            mapView = new MapView(context);
            frameLayout.setBackgroundDrawable(new MapPlaceholderDrawable());
            mapView.onCreate(null);
            try {
                MapsInitializer.initialize(context);
                aMap = mapView.getMap();
            } catch (Exception e) {
                FileLog.e("LocationActivity", e);
            }

            FrameLayout bottomView = new FrameLayout(context);
            bottomView.setBackgroundResource(R.drawable.location_panel);
            frameLayout.addView(bottomView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 60, Gravity.LEFT | Gravity.BOTTOM));
            bottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userLocation != null) {
                        LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                        if (aMap != null) {
                            CameraUpdate position = CameraUpdateFactory.newLatLngZoom(latLng, getLocationFitZoom());
                            aMap.animateCamera(position);
                        }
                    }
                }
            });

            avatarImageView = new BackupImageView(context);
            avatarImageView.setBackgroundResource(R.drawable.round_grey);
            avatarImageView.setSize(AndroidUtilities.dp(40), AndroidUtilities.dp(40));
            avatarImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(0xff999999, PorterDuff.Mode.MULTIPLY));

            bottomView.addView(avatarImageView, LayoutHelper.createFrame(40, 40, Gravity.TOP | (Gravity.LEFT), 12, 12, 0, 0));

            nameTextView = new TextView(context);
            nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            nameTextView.setTextColor(0xff212121);
            nameTextView.setMaxLines(1);
            nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            nameTextView.setSingleLine(true);
            nameTextView.setGravity(Gravity.LEFT);
            bottomView.addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | (Gravity.LEFT), 72, 10, 12, 0));

            distanceTextView = new TextView(context);
            distanceTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            distanceTextView.setTextColor(0xff2f8cc9);
            distanceTextView.setMaxLines(1);
            distanceTextView.setEllipsize(TextUtils.TruncateAt.END);
            distanceTextView.setSingleLine(true);
            distanceTextView.setGravity(Gravity.LEFT);
            bottomView.addView(distanceTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | (Gravity.LEFT), 72, 33, 12, 0));

            userLocation = new Location("network");
            userLocation.setLatitude(targetLocationLat);
            userLocation.setLongitude(targetLocationLon);
            if (aMap != null) {
                LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                try {
                    aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                } catch (Exception e) {
                    FileLog.e("LocationActivity", e);
                }
                CameraUpdate position = CameraUpdateFactory.newLatLngZoom(latLng, getLocationFitZoom());
                aMap.moveCamera(position);
            }

            ImageView routeButton = new ImageView(context);
            routeButton.setBackgroundResource(R.drawable.floating_states);
            routeButton.setImageResource(R.drawable.navigate);
            routeButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(routeButton, "translationZ", AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
                animator.addState(new int[]{}, ObjectAnimator.ofFloat(routeButton, "translationZ", AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
                routeButton.setStateListAnimator(animator);
                routeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                        }
                    }
                });
            }
            frameLayout.addView(routeButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (Gravity.RIGHT) | Gravity.BOTTOM, 0, 0, 14, 28));
            routeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myLocation != null) {
//                        try {
//                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.dateFormat(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", myLocation.getLatitude(), myLocation.getLongitude(), messageObject.messageOwner.media.geo.lat, messageObject.messageOwner.media.geo._long)));
//                            getParentActivity().startActivity(intent);
//                        } catch (Exception e) {
//                            FileLog.e("LocationActivity", e);
//                        }
                    }
                }
            });

            frameLayout.addView(locationButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (Gravity.RIGHT) | Gravity.BOTTOM, 0, 0, 14, 100));
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myLocation != null && aMap != null) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), getLocationFitZoom()));
                    }
                }
            });
        } else {
            searchWas = false;
            searching = false;
            mapViewClip = new FrameLayout(context);
            mapViewClip.setBackground(new MapPlaceholderDrawable());
            if (adapter != null) {
                adapter.destroy();
            }
            if (searchAdapter != null) {
                searchAdapter.destroy();
            }

            listView = new ListView(context);
            listView.setAdapter(adapter = new LocationActivityAdapter(context, searchType));
            listView.setVerticalScrollBarEnabled(false);
            listView.setDividerHeight(0);
            listView.setDivider(null);
            frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (totalItemCount == 0) {
                        return;
                    }
                    updateClipView(firstVisibleItem);
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 1) {
                        getMyActionBar().openSearchField("");
                    } else {
                        LocationEntity object = adapter.getItem(position);
                        processLocationSelected(object);
                    }
                }
            });
            adapter.setDelegate(new BaseLocationAdapter.BaseLocationAdapterDelegate() {
                @Override
                public void didLoadedSearchResult(ArrayList<LocationEntity> places) {
                    if (!wasResults && !places.isEmpty()) {
                        wasResults = true;
                        tryCreateLocationMarket(places);
                    }
                }
            });
            adapter.setOverScrollHeight(overScrollHeight);

            frameLayout.addView(mapViewClip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));

            mapView = new MapView(context) {
                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (Build.VERSION.SDK_INT >= 11) {
                        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                            if (animatorSet != null) {
                                animatorSet.cancel();
                            }
                            animatorSet = new AnimatorSet();
                            animatorSet.setDuration(200);
                            animatorSet.playTogether(
                                    ObjectAnimator.ofFloat(markerImageView, "translationY", markerTop + -AndroidUtilities.dp(10)),
                                    ObjectAnimator.ofFloat(markerXImageView, "alpha", 1.0f));
                            animatorSet.start();
                        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                            if (animatorSet != null) {
                                animatorSet.cancel();
                            }
                            animatorSet = new AnimatorSet();
                            animatorSet.setDuration(200);
                            animatorSet.playTogether(
                                    ObjectAnimator.ofFloat(markerImageView, "translationY", markerTop),
                                    ObjectAnimator.ofFloat(markerXImageView, "alpha", 0.0f));
                            animatorSet.start();
                        }
                    }
                    if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                        if (!userLocationMoved) {
                            if (Build.VERSION.SDK_INT >= 11) {
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.setDuration(200);
                                animatorSet.play(ObjectAnimator.ofFloat(locationButton, "alpha", 1.0f));
                                animatorSet.start();
                            } else {
                                locationButton.setVisibility(View.VISIBLE);
                            }
                            userLocationMoved = true;
                        }
                        if (aMap != null && userLocation != null) {
                            userLocation.setLatitude(aMap.getCameraPosition().target.latitude);
                            userLocation.setLongitude(aMap.getCameraPosition().target.longitude);
                        }
                        adapter.setCustomLocation(userLocation);
                    }
                    return super.onInterceptTouchEvent(ev);
                }
            };
            mapView.onCreate(null);
            try {
                MapsInitializer.initialize(context);
                aMap = mapView.getMap();
            } catch (Exception e) {
                FileLog.e("LocationActivity", e);
            }

            View shadow = new View(context);
            shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
            mapViewClip.addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(3), Gravity.LEFT | Gravity.BOTTOM));

            markerImageView = new ImageView(context);
            markerImageView.setImageResource(R.drawable.map_pin);
            mapViewClip.addView(markerImageView, LayoutHelper.createFrame(24, 42, Gravity.TOP | Gravity.CENTER_HORIZONTAL));

            if (Build.VERSION.SDK_INT >= 11) {
                markerXImageView = new ImageView(context);
                markerXImageView.setAlpha(0.0f);
                markerXImageView.setImageResource(R.mipmap.place_x);
                mapViewClip.addView(markerXImageView, LayoutHelper.createFrame(14, 14, Gravity.TOP | Gravity.CENTER_HORIZONTAL));
            }

            mapViewClip.addView(locationButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56 : 60, (Gravity.RIGHT) | Gravity.BOTTOM, 0, 0, 14, 14));
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myLocation != null && aMap != null) {
                        if (Build.VERSION.SDK_INT >= 11) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.setDuration(200);
                            animatorSet.play(ObjectAnimator.ofFloat(locationButton, "alpha", 0.0f));
                            animatorSet.start();
                        } else {
                            locationButton.setVisibility(View.INVISIBLE);
                        }
                        adapter.setCustomLocation(null);
                        userLocationMoved = false;
                        //aMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), getLocationFitZoom()));
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= 11) {
                locationButton.setAlpha(0.0f);
            } else {
                locationButton.setVisibility(View.INVISIBLE);
            }

            emptyTextLayout = new LinearLayout(context);
            emptyTextLayout.setVisibility(View.GONE);
            emptyTextLayout.setOrientation(LinearLayout.VERTICAL);
            frameLayout.addView(emptyTextLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
            emptyTextLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            TextView emptyTextView = new TextView(context);
            emptyTextView.setTextColor(0xff808080);
            emptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            emptyTextView.setGravity(Gravity.CENTER);
            emptyTextView.setText("无数据");
            emptyTextLayout.addView(emptyTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 0.5f));

            FrameLayout frameLayoutEmpty = new FrameLayout(context);
            emptyTextLayout.addView(frameLayoutEmpty, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 0.5f));

            searchListView = new ListView(context);
            searchListView.setVisibility(View.GONE);
            searchListView.setDividerHeight(0);
            searchListView.setDivider(null);
            searchListView.setAdapter(searchAdapter = new LocationActivitySearchAdapter(context));
            frameLayout.addView(searchListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
            searchListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == SCROLL_STATE_TOUCH_SCROLL && searching && searchWas) {
                        AndroidUtilities.hideKeyboard(getCurrentFocus());
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LocationEntity object = searchAdapter.getItem(position);
                    processLocationSelected(object);
                }
            });

            if (aMap != null) {
                userLocation = new Location("network");
                userLocation.setLatitude(120.413879);
                userLocation.setLongitude(36.07745);
            }
            frameLayout.addView(actionBar);
            content.addView(fragmentView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            setContentView(content, actionBar);
        }

        if (aMap != null) {
            aMap.setMyLocationEnabled(true);
            aMap.getUiSettings().setMyLocationButtonEnabled(false);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setCompassEnabled(false);
            aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    positionMarker(location);
                }
            });
            positionMarker(myLocation = getLastLocation());
        }
        startLocation();
    }


    private void tryCreateLocationMarket(ArrayList<LocationEntity> places) {
        if (searchType == BaseLocationAdapter.SearchType.SHOP) {
            for (LocationEntity location :
                    places) {
                try {
                    aMap.addMarker(new MarkerOptions()
                            .position(location.createLocation())
                            .title(location.name)
                            .snippet(location.address));
                } catch (Exception e) {
                    FileLog.e("LocationActivity", e);
                }
            }
        }
    }

    /**
     * 处理位置列表行选中状态
     *
     * @param entity
     */
    private void processLocationSelected(LocationEntity entity) {
        if (searchType == BaseLocationAdapter.SearchType.SHOP) {
            Intent intent = new Intent(LocationActivity.this, DrugStoryDetailActivity.class);
            intent.putExtra("locationEntity", entity);
            startActivity(intent);
        } else {
            LocationHelper.updateLastLocation(LocationActivity.this, entity);
            finish();
        }
    }

    private float getLocationFitZoom() {
        return aMap.getMaxZoomLevel() - 3;
    }

    @Override
    protected void onLocationSuccess(AMapLocation aMapLocation) {
        myLocation = new Location("my");
        myLocation.setLatitude(aMapLocation.getLatitude());
        myLocation.setLongitude(aMapLocation.getLongitude());
        myLocation.setAltitude(aMapLocation.getAltitude());
        myLocation.setAccuracy(aMapLocation.getAccuracy());
        LocationHelper.writeLastLocation(aMapLocation.getCityCode(), aMapLocation.getLatitude(), aMapLocation.getLongitude());
        positionMarker(myLocation);
    }

    @Override
    protected void onLocationFail(AMapLocation aMapLocation) {
        myLocation = null;
        positionMarker(myLocation);
    }

    private void updateClipView(int firstVisibleItem) {
        int height = 0;
        int top = 0;
        View child = listView.getChildAt(0);
        if (child != null) {
            if (firstVisibleItem == 0) {
                top = child.getTop();
                height = overScrollHeight + (top < 0 ? top : 0);
                halfHeight = (top < 0 ? top : 0) / 2;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapViewClip.getLayoutParams();
            if (layoutParams != null) {
                if (height <= 0) {
                    if (mapView.getVisibility() == View.VISIBLE) {
                        mapView.setVisibility(View.INVISIBLE);
                        mapViewClip.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (mapView.getVisibility() == View.INVISIBLE) {
                        mapView.setVisibility(View.VISIBLE);
                        mapViewClip.setVisibility(View.VISIBLE);
                    }
                }
                if (Build.VERSION.SDK_INT >= 11) {
                    mapViewClip.setTranslationY(Math.min(0, top));
                    mapView.setTranslationY(Math.max(0, -top / 2));
                    markerImageView.setTranslationY(markerTop = -top - AndroidUtilities.dp(42) + height / 2);
                    markerXImageView.setTranslationY(-top - AndroidUtilities.dp(7) + height / 2);

                    if (mapView != null) {
                        layoutParams = (FrameLayout.LayoutParams) mapView.getLayoutParams();
                        if (layoutParams != null && layoutParams.height != overScrollHeight + AndroidUtilities.dp(10)) {
                            layoutParams.height = overScrollHeight + AndroidUtilities.dp(10);
                            mapView.setPadding(0, 0, 0, AndroidUtilities.dp(10));
                            mapView.setLayoutParams(layoutParams);
                        }
                    }
                } else {
                    markerTop = 0;
                    layoutParams.height = height;
                    mapViewClip.setLayoutParams(layoutParams);

                    layoutParams = (FrameLayout.LayoutParams) markerImageView.getLayoutParams();
                    layoutParams.topMargin = height / 2 - AndroidUtilities.dp(42);
                    markerImageView.setLayoutParams(layoutParams);

                    if (mapView != null) {
                        layoutParams = (FrameLayout.LayoutParams) mapView.getLayoutParams();
                        if (layoutParams != null) {
                            layoutParams.topMargin = halfHeight;
                            layoutParams.height = overScrollHeight + AndroidUtilities.dp(10);
                            mapView.setPadding(0, 0, 0, AndroidUtilities.dp(10));
                            mapView.setLayoutParams(layoutParams);
                        }
                    }
                }
            }
        }
    }

    private void fixLayoutInternal(final boolean resume) {
        if (listView != null) {
            int height = (getMyActionBar().getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.getCurrentActionBarHeight();
            int viewHeight = mapView.getMeasuredHeight();
            if (viewHeight <= AndroidUtilities.dp(66)) {
                return;
            }

            overScrollHeight = resume ? overScrollHeight : viewHeight;
            //overScrollHeight = resume ? overScrollHeight : viewHeight - AndroidUtilities.dp(66) - height;

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
            layoutParams.topMargin = height;
            listView.setLayoutParams(layoutParams);
            layoutParams = (FrameLayout.LayoutParams) mapViewClip.getLayoutParams();
            layoutParams.topMargin = height;
            layoutParams.height = overScrollHeight;
            mapViewClip.setLayoutParams(layoutParams);
            layoutParams = (FrameLayout.LayoutParams) searchListView.getLayoutParams();
            layoutParams.topMargin = height;
            searchListView.setLayoutParams(layoutParams);

            adapter.setOverScrollHeight(overScrollHeight);
            layoutParams = (FrameLayout.LayoutParams) mapView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = overScrollHeight + AndroidUtilities.dp(10);
                if (mapView != null) {
                    mapView.setPadding(0, 0, 0, AndroidUtilities.dp(10));
                }
                mapView.setLayoutParams(layoutParams);
            }
            adapter.notifyDataSetChanged();

            if (resume) {
                listView.setSelectionFromTop(0, -(int) (AndroidUtilities.dp(56) * 2.5f + AndroidUtilities.dp(36 + 66)));
                updateClipView(listView.getFirstVisiblePosition());
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setSelectionFromTop(0, -(int) (AndroidUtilities.dp(56) * 2.5f + AndroidUtilities.dp(36 + 66)));
                        updateClipView(listView.getFirstVisiblePosition());
                    }
                });
            } else {
                updateClipView(listView.getFirstVisiblePosition());
            }
        }
    }

    private Location getLastLocation() {
        LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i = providers.size() - 1; i >= 0; i--) {
            //l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) {
                break;
            }
        }
        return l;
    }

    private void updateTargetLocationInfo() {
        if ((!TextUtils.isEmpty(targetLocationAddress)) && avatarImageView != null) {
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setSmallStyle(true);
            avatarDrawable.setInfo(0, "药");
            avatarDrawable.setColor(0xff999999);
            avatarImageView.setImageUrl("", null, avatarDrawable);
        }
    }

    private void positionMarker(Location location) {
        if (location == null) {
            return;
        }
        myLocation = new Location(location);
        if (isReadOnly) {
            float distance = location.distanceTo(userLocation);
            if (distance < 1000) {
                distanceTextView.setText(String.format("距离 %d 米", (int) (distance)));
            } else {
                distanceTextView.setText(String.format("距离 %.2f 千米", distance / 1000.0f));
            }
        } else if (aMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (adapter != null) {
                if (searchType == BaseLocationAdapter.SearchType.SHOP) {
                    adapter.searchMapPlacesWithServerQuery(MAP_SEARCH_DEFAULT_KEY, myLocation);
                } else {
                    adapter.searchMapPlacesWithQuery(MAP_SEARCH_DEFAULT_KEY, myLocation);
                }
                adapter.setGpsLocation(myLocation);
            }
            if (!userLocationMoved) {
                userLocation = new Location(location);
                if (firstWas) {
                    CameraUpdate position = CameraUpdateFactory.newLatLngZoom(latLng, getLocationFitZoom());
                    aMap.animateCamera(position);
                } else {
                    firstWas = true;
                    CameraUpdate position = CameraUpdateFactory.newLatLngZoom(latLng, getLocationFitZoom());
                    aMap.moveCamera(position);
                }
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            try {
                mapView.onPause();
            } catch (Exception e) {
                FileLog.e("LocationActivity", e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        if (mapView != null) {
            mapView.onResume();
        }
        try {
            if (mapView.getParent() instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) mapView.getParent();
                viewGroup.removeView(mapView);
            }
        } catch (Exception e) {
            FileLog.e("LocationActivity", e);
        }
        if (mapViewClip != null) {
            mapViewClip.addView(mapView, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, overScrollHeight + AndroidUtilities.dp(10), Gravity.TOP | Gravity.LEFT));
            updateClipView(listView.getFirstVisiblePosition());
        } else {
            fragmentView.addView(mapView, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        }
        updateTargetLocationInfo();
        //fixLayoutInternal(true);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    private void updateSearchInterface() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        ActionBar actionBar = getMyActionBar();
        if (actionBar != null && actionBar.isSearchFieldVisible()) {
            actionBar.closeSearchField();
        } else {
            cancelLocation();
        }
    }

    private void cancelLocation() {
//        if (isReturn) {
//            setResult(RESULT_CANCELED);
//        }
        finish();
    }
}
