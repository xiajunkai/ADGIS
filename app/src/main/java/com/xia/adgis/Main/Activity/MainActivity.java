package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.swipeback.SwipeBackActivityImpl;
import com.xia.adgis.Admin.Activity.AdminActivity;
import com.xia.adgis.Login.LoginActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Main.Bean.ADCompany;
import com.xia.adgis.Main.Bean.ADmaintain;
import com.xia.adgis.Main.Bean.ADphysical;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.R;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.xia.adgis.Register.Bean.User;
import com.xia.imagewatch.RolloutBDInfo;
import com.xia.imagewatch.RolloutInfo;
import com.xia.imagewatch.RolloutPreviewActivity;
import com.xia.toprightmenu.MenuItem;
import com.xia.toprightmenu.TopRightMenu;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;

@SuppressWarnings("unchecked")
public class MainActivity extends SwipeBackActivityImpl implements AMap.OnMarkerClickListener,PopupWindow.OnDismissListener{
    private static final int UI_ANIMATION_DELAY = 5;
    private static final int SEARCH = 1;
    private static final int SETTING = 2;
    private static final int USER_CENTRE = 3;
    private static final int ALL_ADS = 4;
    private static final int ADS_DETAIL = 20;
    private static final int VIEW_ALL_ADS = 30;
    //需点击隐藏的UI控件
    @BindView(R.id.fullscreen_content_controls)
    View mControlsView;
    //自定义toolbar
    @BindView(R.id.toolbar)
    View mToolBar;
    //自定义下部导航栏
    @BindView(R.id.my_back)
    LinearLayout back;
    @BindView(R.id.back_icon)
    ImageView backIcon;
    @BindView(R.id.back_text)
    TextView backText;
    @BindView(R.id.my_info)
    LinearLayout info;
    @BindView(R.id.info_icon)
    ImageView infoIcon;
    @BindView(R.id.info_text)
    TextView infoText;
    @BindView(R.id.my_advance)
    LinearLayout advance;
    @BindView(R.id.advance_icon)
    ImageView advanceIcon;
    @BindView(R.id.advance_text)
    TextView advanceText;

    //主界面头像
    @BindView(R.id.imageView)
    CircleImageView mCircleImageView;
    //我的位置
    @BindView(R.id.preferences)
    ImageView mPreferences;
    //搜索
    @BindView(R.id.search)
    ImageView mSearch;

    //地图
    @BindView(R.id.map)
    MapView mMapView;
    private AMap mAMap;
    private UiSettings mUiSettings;
    private Marker locationMarker;
    //自定义定位相关
    public AMapLocationClient mAMapLocationClient;
    public AMapLocationClientOption mAMapLocationClientOption;
    //private MyLocationStyle myLocationStyle;
    //当前广告牌在数组中位置
    private int tempMarkerId = 0;

    private ArrayList<LatLng> tempLatLng = new ArrayList<>();
    private ArrayList<Marker> tempMarker = new ArrayList<>();
    //左侧滑相关
    @BindView(R.id.right)
    FrameLayout right;
    @BindView(R.id.nav_view)
    NavigationView left;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private boolean isDrawer = false;
    //左侧侧滑内部控件
    TextView username;
    TextView userAdmin;
    CircleImageView icon;
    //右侧滑栏
    @BindView(R.id.right_nav_view)
    NavigationView right_nav_view;
    //右侧滑内部控件
    Toolbar right_toolbar;
    //弹出的popwindow
    private PopupWindow popupWindow;
    private int navigationHeight;   //弹窗弹出的位置
    private String title;   //弹出对应的标题
    //弹窗内部UI
    ImageView locationImage;
    TextView locationName;
    ImageView popDetail;
    //弹窗的UI
    View view;
    private TopRightMenu popMenu;
    //图片浏览
    private ArrayList<RolloutInfo> data = new ArrayList<>();
    protected RolloutBDInfo bdInfo;
    protected RolloutInfo imageInfo;
    String tempImage;
    //云端登陆
    User user;
    //从云端获取的AD数组
    List<AD> bmobData = new ArrayList<>();
    //是否显示UI变量
    private boolean mVisible;
    //进度显示
    ProgressDialog progressDialog;
    //设置首选项
    SharedPreferences settingPreferences;
    private long mExitTime;
    //主界面查看广告牌的dialog
    AlertDialog dialog = null;
    /**
     * 以下全部都是隐藏界面的逻辑
     */
    private final Handler mHideHandler = new Handler();

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

            mControlsView.setVisibility(View.VISIBLE);
            mToolBar.setVisibility(View.VISIBLE);
        }
    };

    //点击隐藏UI
    private void toggle() {
        if (mVisible) {
            hide();
            //mUiSettings.setMyLocationButtonEnabled(true);
        } else {
            show();
            //mUiSettings.setMyLocationButtonEnabled(false);
        }
    }
    //隐藏逻辑
    private void hide() {
        mControlsView.setVisibility(View.GONE);
        mToolBar.setVisibility(View.GONE);
        mVisible = false;
        mHideHandler.removeCallbacks(mShowPart2Runnable);
    }
    //显示逻辑
    private void show() {
        mVisible = true;
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //用户
        user = BmobUser.getCurrentUser(User.class);
        mVisible = true;
        //沉浸式任务栏
        StatusBarUtil.immersive(this, true);
        //地图设置
        initMapSetting();
        mMapView.onCreate(savedInstanceState);
        //载入设置首选项
        settingPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        LoadSettingPreferences();
        //初始化底部导航栏
        initBottomNavigationBar();
        //右上角弹出菜单
        initTopRightMenu();
        //初始化左侧滑栏
        initLeftDrawerLayout();
        //初始化右侧滑栏
        initRightDrawerLayout();
        //加载数据(初始版本，从缓存中读取)
        loadingMapData();
        //底部导航栏图片资源
        infoIcon.setImageResource(R.drawable.ic_visibility_off_unpress);
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        //弹窗弹出位置
        //int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        //navigationHeight = getResources().getDimensionPixelSize(resourceId);
    }

    //载入设置
    private void LoadSettingPreferences(){
        String Pre_MAP_TYPE = settingPreferences.getString("preference_chose_map","1");
        boolean isRound = settingPreferences.getBoolean("preference_traffic",false);
        int MAP_TYPE = Integer.valueOf(Pre_MAP_TYPE);
        mAMap.setMapType(MAP_TYPE);
        mUiSettings.setRotateGesturesEnabled(isRound);
    }
    /**
     *  地图部分
     */
    //初始化并设置地图
    private void initMapSetting(){
        //初始化MapView
        if(mAMap == null){
            mAMap = mMapView.getMap();
            mAMap.showIndoorMap(true);
            mUiSettings = mAMap.getUiSettings();
            //此处为系统自定义的定位
            /*myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_1));
            mAMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));*/
            initLocation();
            //mAMap.setTrafficEnabled(true);
            mAMap.setOnMarkerClickListener(this);
        }
        // 设置用户交互以手动显示或隐藏系统UI。
        mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(settingPreferences.getBoolean("preference_hide_ui",true)) {
                    toggle();
                }else {
                    deleteInfoWindows(tempMarker);
                }
            }
        });
        mAMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //隐藏infoWindows
                //deleteInfoWindows(tempMarker);
            }
        });
        mAMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                //隐藏infoWindows
                deleteInfoWindows(tempMarker);
            }
        });
        //地图基本的UI设置
        //标志设置在左下方
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
        //显示比例尺
        mUiSettings.setScaleControlsEnabled(true);
        //定位按钮显示
        //mUiSettings.setMyLocationButtonEnabled(true);
        //mAMap.setMyLocationEnabled(true);
        //缩放按钮不显示
        mUiSettings.setZoomControlsEnabled(false);
    }

    //初始化定位相关
    private void initLocation() {
        //初始化定位
        mAMapLocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位回调监听
        mAMapLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        //取出经纬度
                        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        //添加Marker显示定位位置
                        if (locationMarker == null) {
                            //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
                            locationMarker = mAMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ad_location))));
                            //jumpPoint(locationMarker);
                        } else {
                            //已经添加过了，修改位置即可
                            locationMarker.remove();
                            //locationMarker.setPosition(latLng);
                            locationMarker = mAMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ad_location))));
                        }

                        //然后可以移动到定位点,使用animateCamera就有动画效果
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                latLng, 15, 45, 0)),500,null);
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Toast.makeText(MainActivity.this,"location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //初始化定位参数
        mAMapLocationClientOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mAMapLocationClientOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mAMapLocationClientOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mAMapLocationClientOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mAMapLocationClientOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mAMapLocationClientOption.setInterval(100);
        //给定位客户端对象设置定位参数
        mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
    }
    //创建时加载地图数据(有缓存判断)
    private void loadingMapData(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("加载中");
        progressDialog.show();
        //使用Bmob获取用来标记的信息
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        //先判断是否有缓存
        boolean isCache = adBmobQuery.hasCachedResult(AD.class);
        if(isCache){
            adBmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);	// 先从缓存取数据，如果没有的话，再从网络取。
        }else{
            adBmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);	// 如果没有缓存的话，则先从网络中取
        }
        adBmobQuery.findObjectsObservable(AD.class)
                .subscribe(new Subscriber<List<AD>>() {
                    @Override
                    public void onCompleted() {
                        //加载顶部导航栏的头像
                        Glide.with(MainActivity.this)
                                .load(user.getUserIcon())
                                .into(new GlideDrawableImageViewTarget(mCircleImageView){
                                    @Override
                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                        super.onResourceReady(resource, animation);
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(MainActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(List<AD> ads) {
                        bmobData = ads;
                        addMarksToMap(ads);
                        //是否记忆位置
                        boolean isMemory = settingPreferences.getBoolean("preference_history",true);
                        isMemoryLocation(isMemory);
                    }
                });

    }
    //刷新地图数据
    private void RefreshMapData(){
        //progressDialog.show();
        //使用Bmob获取用来标记的信息
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        adBmobQuery.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if(e == null) {
                    bmobData = list;
                    addMarksToMap(list);
                }else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
    //对地图添加mark
    private void addMarksToMap(List<AD> ads){
        mAMap.clear();
        tempLatLng.clear();
        tempMarker.clear();
        LatLng latLng;
        MarkerOptions markerOptions;
        for(AD ad : ads){
            latLng = new LatLng(ad.getLatitude(),ad.getLongitude());
            tempLatLng.add(latLng);
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(ad.getName());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ad_location)));
            Marker marker = mAMap.addMarker(markerOptions);
            tempMarker.add(marker);
        }
    }

    //清除广告消息
    private void deleteAllMarkers(List<Marker> markers){
        for(Marker marker : markers){
            marker.remove();
        }
    }

    //清除infowindows
    private void deleteInfoWindows(List<Marker> markers){
        for(Marker marker : markers){
            marker.hideInfoWindow();
        }
    }

    //Marker点击回调
    @Override
    public boolean onMarkerClick(Marker marker) {
        //jumpPoint(marker);
        if(!marker.equals(locationMarker)){
            tempMarkerId = SearchTempMarkerIdFromLatLng(marker.getPosition());
            //jumpPoint(marker);
            loadingCorrespondingMessage(marker);
        }else {
            jumpPoint(marker);
        }
        return false;
    }

    //点击事件所进行的加载
    private void loadingCorrespondingMessage(Marker marker){
        openPopupWindow();
        title = marker.getTitle();
        tempImage = getImage(title);
        locationName.setText(title);
        //此处为预览图片所进行的处理
        data.clear();
        bdInfo = new RolloutBDInfo();
        imageInfo = new RolloutInfo();
        //图片的宽高可以自己去设定,也可以计算图片宽高
        imageInfo.width = 1400;
        imageInfo.height = 1120;
        imageInfo.url = tempImage;
        data.add(imageInfo);
        //图片加载
        progressDialog.setCancelable(false);
        progressDialog.setMessage("加载中");
        Glide.with(this).
                load(tempImage).
                into(new GlideDrawableImageViewTarget(locationImage){
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        progressDialog.show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        progressDialog.dismiss();
                    }
                });
    }

    //得到图片
    public String getImage(String title){
        String s = null;
        for (AD ad:bmobData){
            if(ad.getName().equals(title)){
                s = ad.getImageID();
                break;
            }
        }
        return s;
    }

    //pop弹出函数
    private void openPopupWindow() {
        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        //设置PopupWindow的View
        view = LayoutInflater.from(this).inflate(R.layout.ad_detail_popwindow, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置背景,这个没什么效果，不添加会报错
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置点击弹窗外隐藏自身
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //设置动画
        popupWindow.setAnimationStyle(R.style.PopupWindow);
        //设置位置
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, navigationHeight);
        //设置消失监听
        popupWindow.setOnDismissListener(this);
        //设置PopupWindow的View点击事件
        setOnPopupViewClick(view);
        //设置背景色(半透明)
        setBackgroundAlpha(0.8f);
    }

    private void setOnPopupViewClick(View view) {

        locationImage = (ImageView) view.findViewById(R.id.locationImage);
        locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ADsDetailActivity.class);
                intent.putExtra("data",title);
                ActivityCompat.startActivityForResult(MainActivity.this,
                        intent,ADS_DETAIL,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this,
                                new Pair<>(view, "detail_image"))
                                .toBundle());
            }
        });
        locationImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //获取相对位置，左边和顶部
                int location[] = new int[2];
                locationImage.getLocationOnScreen(location);
                bdInfo.x = location[0];
                bdInfo.y = location[1];
                //视图布局的宽高
                bdInfo.width = locationImage.getWidth();
                bdInfo.height = locationImage.getHeight();
                //跳转和传数据都必须要
                Intent intent = new Intent(MainActivity.this, RolloutPreviewActivity.class);
                intent.putExtra("data", (Serializable) data);
                intent.putExtra("bdinfo",bdInfo);
                intent.putExtra("type", 0);//单图传0
                intent.putExtra("index",0);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            }
        });
        popDetail = (ImageView) view.findViewById(R.id.popDetail);
        popDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popMenu = new TopRightMenu(MainActivity.this);
                List<MenuItem> menuItemList = new ArrayList<>();
                menuItemList.add(new MenuItem(R.drawable.ads_physical,"物理信息"));
                menuItemList.add(new MenuItem(R.drawable.ads_company,"公司信息"));
                menuItemList.add(new MenuItem(R.drawable.ads_maintain,"维护信息"));
                popMenu.addMenuList(menuItemList)
                        .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                            @Override
                            public void onMenuItemClick(int position) {
                                switch (position){
                                    case 0:
                                        progressDialog.setCancelable(true);
                                        progressDialog.show();
                                        BmobQuery<ADphysical> physicalBmobQuery = new BmobQuery<>();
                                        physicalBmobQuery.addWhereEqualTo("name", title);
                                        physicalBmobQuery.findObjects(new FindListener<ADphysical>() {
                                            @Override
                                            public void done(List<ADphysical> list, BmobException e) {
                                                progressDialog.dismiss();
                                                dialog = new AlertDialog.Builder(MainActivity.this).create();
                                                dialog.show();
                                                if (dialog.getWindow() != null) {
                                                    dialog.getWindow().setContentView(R.layout.fragment_ads_physical);
                                                }
                                                TextView length = (TextView) dialog.findViewById(R.id.ads_length);
                                                TextView width = (TextView) dialog.findViewById(R.id.ads_width);
                                                TextView height = (TextView) dialog.findViewById(R.id.ads_height);
                                                TextView material = (TextView) dialog.findViewById(R.id.ads_material);
                                                if(length != null && width != null && height != null && material != null) {
                                                    length.setText(list.get(list.size() - 1).getLength() + " cm");
                                                    width.setText(list.get(list.size() - 1).getWidth() + " cm");
                                                    height.setText(list.get(list.size() - 1).getHeight() + " cm");
                                                    material.setText(list.get(list.size() - 1).getMaterial());
                                                }
                                            }
                                        });
                                        break;
                                    case 1:
                                        progressDialog.setCancelable(true);
                                        progressDialog.show();
                                        BmobQuery<ADCompany> companyBmobQuery = new BmobQuery<>();
                                        companyBmobQuery.addWhereEqualTo("name", title);
                                        companyBmobQuery.findObjects(new FindListener<ADCompany>() {
                                            @Override
                                            public void done(List<ADCompany> list, BmobException e) {
                                                progressDialog.dismiss();
                                                dialog = new AlertDialog.Builder(MainActivity.this).create();
                                                dialog.show();
                                                if (dialog.getWindow() != null) {
                                                    dialog.getWindow().setContentView(R.layout.fragment_ads_company);
                                                }
                                                TextView designer = (TextView) dialog.findViewById(R.id.ads_designer);
                                                TextView holder = (TextView) dialog.findViewById(R.id.ads_holder);
                                                if (designer != null && holder != null){
                                                    designer.setText(list.get(list.size() - 1).getDesigner());
                                                    holder.setText(list.get(list.size() - 1).getHoder());
                                                }
                                            }
                                        });
                                        break;
                                    case 2:
                                        progressDialog.setCancelable(true);
                                        progressDialog.show();
                                        BmobQuery<ADmaintain> maintainBmobQuery = new BmobQuery<>();
                                        maintainBmobQuery.addWhereEqualTo("name", title);
                                        maintainBmobQuery.findObjects(new FindListener<ADmaintain>() {
                                            @Override
                                            public void done(List<ADmaintain> list, BmobException e) {
                                                progressDialog.dismiss();
                                                dialog = new AlertDialog.Builder(MainActivity.this).create();
                                                dialog.show();
                                                if (dialog.getWindow() != null) {
                                                    dialog.getWindow().setContentView(R.layout.fragment_ads_maintain);
                                                }
                                                TextView company = (TextView) dialog.findViewById(R.id.ads_maintain_company);
                                                TextView content = (TextView) dialog.findViewById(R.id.ads_maintain_context);
                                                TextView time = (TextView) dialog.findViewById(R.id.ads_maintain_time);
                                                if (company != null && content != null && time != null){
                                                    company.setText(list.get(list.size() - 1).getCompany());
                                                    content.setText(list.get(list.size() - 1).getContext());
                                                    time.setText(list.get(list.size() - 1).getTime());
                                                }
                                            }
                                        });
                                        break;
                                    default:
                                }
                            }
                        })
                        .showAsDropDown(popDetail,-250,-25)
                        .dimBackground(false);
            }
        });
        locationName = (TextView) view.findViewById(R.id.locationName);

    }

    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    //marker点击时跳动一下
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final LatLng templatLng = marker.getPosition();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mAMap.getProjection();
        Point startPoint = proj.toScreenLocation(templatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * templatLng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * templatLng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    //存储退出时的mark序号
    private void isSaveTempMarkId(int MarkerId, boolean isMenory){
        SharedPreferences.Editor editor = getSharedPreferences("map_data", MODE_PRIVATE).edit();
        if(isMenory) {
            editor.putInt("tempMarkerId", MarkerId);
        }else{
            editor.clear();
        }
        editor.apply();
    }

    //是否记忆了退出位置
    private void isMemoryLocation(boolean isMemory){
        if(isMemory) {
            mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    tempLatLng.get(getSharedPreferences("map_data", MODE_PRIVATE).
                            getInt("tempMarkerId", 0)), 17, 0, 0)), 500, null);
        }else{
            mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    tempLatLng.get(0), 17, 0, 0)), 500, null);
        }
    }
    /**
     * 底部导航栏
     */
    //初始化底部导航栏
    private void initBottomNavigationBar(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //导航栏UI处理
                backIcon.setImageResource(R.drawable.back_press);
                backText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.press));
                if(infoText.getText().toString().equals("隐藏所有")) {
                    infoIcon.setImageDrawable(null);
                    infoIcon.setImageResource(R.drawable.ic_visibility_off_unpress);
                }else{
                    infoIcon.setImageDrawable(null);
                    infoIcon.setImageResource(R.drawable.ic_visibility_unpress);
                }
                infoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                advanceIcon.setImageResource(R.drawable.advance_unpress);
                advanceText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                //处理逻辑
                if(tempMarker.size() != 0) {
                    if (tempMarkerId != 0) {
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                tempLatLng.get(tempMarkerId - 1), 17, 0, 0)),500,null);
                        //jumpPoint(tempMarker.get(tempMarkerId - 1));
                        tempMarkerId --;
                    } else {
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                tempLatLng.get(tempLatLng.size() - 1), 17, 0, 0)),500,null);
                        //jumpPoint(tempMarker.get(tempMarker.size() - 1));
                        tempMarkerId = tempLatLng.size() - 1;
                    }
                }else{
                    Toast.makeText(getBaseContext(),"找不到位置",Toast.LENGTH_SHORT).show();
                }
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //导航栏UI处理
                backIcon.setImageResource(R.drawable.back_unpress);
                backText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                infoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.press));
                advanceIcon.setImageResource(R.drawable.advance_unpress);
                advanceText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                //处理逻辑(显示与隐藏)
                if(infoText.getText().toString().equals("隐藏所有")){
                    if(tempMarker.size() != 0) {
                        deleteAllMarkers(tempMarker);
                        tempMarker.clear();
                        infoIcon.setImageDrawable(null);
                        infoIcon.setImageResource(R.drawable.ic_visibility_press);
                        infoText.setText("显示所有");
                    }else{
                        Toast.makeText(getBaseContext(),"广告位置已经隐藏",Toast.LENGTH_SHORT).show();
                    }
                }else if(infoText.getText().toString().equals("显示所有")){
                    if(tempMarker.size() == 0) {
                        addMarksToMap(bmobData);
                        /*LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(bmobData.get(0).getLatitude(), bmobData.get(0).getLongitude()))
                                .include(new LatLng(bmobData.get(1).getLatitude(), bmobData.get(1).getLongitude()))
                                .include(new LatLng(bmobData.get(2).getLatitude(), bmobData.get(2).getLongitude()))
                                .include(new LatLng(bmobData.get(3).getLatitude(), bmobData.get(3).getLongitude())).build();*/
                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(SearchMinLatitude(tempLatLng), SearchMimLongitude(tempLatLng)))
                                .include(new LatLng(SearchMaxLatitude(tempLatLng), SearchMimLongitude(tempLatLng)))
                                .include(new LatLng(SearchMinLatitude(tempLatLng), SearchMaxLongitude(tempLatLng)))
                                .include(new LatLng(SearchMaxLatitude(tempLatLng), SearchMaxLongitude(tempLatLng)))
                                .build();
                        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                        infoIcon.setImageDrawable(null);
                        infoIcon.setImageResource(R.drawable.ic_visibility_off_press);
                        infoText.setText("隐藏所有");
                    }else {
                        Toast.makeText(getBaseContext(),"广告位置已经显示",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        advance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //导航栏UI处理
                backIcon.setImageResource(R.drawable.back_unpress);
                backText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                if(infoText.getText().toString().equals("隐藏所有")) {
                    infoIcon.setImageDrawable(null);
                    infoIcon.setImageResource(R.drawable.ic_visibility_off_unpress);
                }else{
                    infoIcon.setImageDrawable(null);
                    infoIcon.setImageResource(R.drawable.ic_visibility_unpress);
                }
                infoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                advanceIcon.setImageResource(R.drawable.advance_press);
                advanceText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.press));
                //处理逻辑
                if(tempMarker.size() != 0) {
                    if (tempMarkerId != tempLatLng.size() - 1) {
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                tempLatLng.get(tempMarkerId + 1), 17, 0, 0)),500,null);
                        //jumpPoint(tempMarker.get(tempMarkerId + 1));
                        tempMarkerId ++;
                    } else {
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                tempLatLng.get(0), 17, 0, 0)),500,null);
                        //jumpPoint(tempMarker.get(0));
                        tempMarkerId = 0;
                    }
                }else{
                    Toast.makeText(getBaseContext(),"找不到位置",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 右上角菜单
     */
    //右上角菜单
    private void initTopRightMenu(){
        mPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.END);
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivityForResult(intent,SEARCH);
            }
        });
    }

    /**
     * 左侧滑栏
     */
    //左侧滑栏
    private void initLeftDrawerLayout(){

        left.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                switch (item.getItemId()){
                    case R.id.All_ADs:
                        if(user.isAdmin()) {
                            startActivityForResult(new Intent(MainActivity.this, AllADsActivity.class), ALL_ADS);
                            overridePendingTransition(R.anim.in,R.anim.out);
                        }else {
                            Toast.makeText(MainActivity.this, "您不是管理员，无法查看该界面！", Toast.LENGTH_SHORT).show();
                            drawer.closeDrawer(Gravity.START);
                        }
                        break;
                    case R.id.view_All_ADs:
                        Intent in = new Intent(MainActivity.this, AllADsActivity.class);
                        in.putExtra("user_name", "all");
                        startActivityForResult(in,VIEW_ALL_ADS);
                        overridePendingTransition(R.anim.in,R.anim.out);
                        break;
                    case R.id.setting:
                        Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                        startActivityForResult(intent,SETTING);
                        overridePendingTransition(R.anim.in,R.anim.out);
                        break;
                    case R.id.aboutUs_menu:
                        startActivity(new Intent(MainActivity.this, AboutMeActivity.class));
                        overridePendingTransition(R.anim.in,R.anim.out);
                        break;
                    case R.id.exit:
                        finish();
                        overridePendingTransition(R.anim.in_1,R.anim.out_1);
                        break;
                    default:
                }
                return true;
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                isDrawer = true;
                //获取屏幕的宽高
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                //设置右面的布局位置  根据左面菜单的right作为右面布局的left   左面的right+屏幕的宽度（或者right的宽度这里是相等的）为右面布局的right
                right.layout(left.getRight(), 0, left.getRight() + display.getWidth(), display.getHeight());
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawer = false;
                LoadSettingPreferences();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        View headView = left.getHeaderView(0);
        username = (TextView) headView.findViewById(R.id.username);
        username.setText(user.getUsername());
        //是否是管理员账户
        userAdmin = (TextView) headView.findViewById(R.id.mail);
        if (user.isAdmin()){
            userAdmin.setText("商家账户");
        }else {
            userAdmin.setText("普通账户");
        }
        //加载用户头像
        icon = (CircleImageView) headView.findViewById(R.id.icon_image);
        Glide.with(this).load(user.getUserIcon()).into(icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,UserCentreActivity.class);
                ActivityCompat.startActivityForResult(MainActivity.this, intent, USER_CENTRE,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this,
                                new Pair<>(view, "icon"))
                                .toBundle());
            }
        });
    }

    /**
     * 右侧滑栏
     */
    //右侧滑栏
    private void initRightDrawerLayout(){
        right_nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                switch (item.getItemId()){
                    case R.id.my_location:
                        if (mAMapLocationClient != null) {
                            mAMapLocationClient.startLocation();
                        }
                        break;
                    default:
                }
                drawer.closeDrawer(Gravity.END);
                return false;
            }
        });
        //toolbar处理
        View header = right_nav_view.getHeaderView(0);
        right_toolbar = (Toolbar) header.findViewById(R.id.main_setting_toolbar);
        right_toolbar.setTitle("偏好设置");
        StatusBarUtil.setPaddingSmart(this,right_toolbar);
        right_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.END);
            }
        });
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,Gravity.END);
    }


   @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        isSaveTempMarkId(tempMarkerId ,settingPreferences.getBoolean("preference_history",false));
        //退出界面的时候停止定位
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
        }
    }

    //当侧滑栏没有隐藏时，先隐藏侧滑栏，故重写onBackPress方法
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(drawer.isDrawerOpen(Gravity.END)){
            drawer.closeDrawer(Gravity.END);
        } else if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        } else if ((System.currentTimeMillis() - mExitTime) > 2000){
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SEARCH :
                if(resultCode == RESULT_OK){
                    LatLng latLng = data.getParcelableExtra("search_return");
                    if(tempMarker.size() != 0) {
                        tempMarkerId = SearchTempMarkerIdFromLatLng(latLng);
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                latLng, 17, 0, 0)), 500, null);
                        Marker marker = SearchCorrespondingMarker(latLng);
                        loadingCorrespondingMessage(marker);
                    }else{
                        Toast.makeText(this, "找不到位置！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SETTING :
                if(resultCode == RESULT_OK){
                    String type = data.getStringExtra("setting");
                    switch (type) {
                        case "logout":
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            overridePendingTransition(R.anim.in, R.anim.out);
                            finish();
                            break;
                        case "preference":
                            drawer.closeDrawer(Gravity.START);
                            drawer.openDrawer(Gravity.END);
                            break;
                        default:
                            drawer.closeDrawer(Gravity.START);
                            break;
                    }
                    //设置界面载入设置
                    LoadSettingPreferences();
                }
                break;
            case USER_CENTRE :
                if(resultCode == RESULT_OK) {
                    if (data.getStringExtra("centre").equals("success")) {
                        user = BmobUser.getCurrentUser(User.class);
                        Glide.with(MainActivity.this)
                                .load(user.getUserIcon())
                                .into(new GlideDrawableImageViewTarget(mCircleImageView) {
                                    @Override
                                    public void onLoadStarted(Drawable placeholder) {
                                        super.onLoadStarted(placeholder);
                                        progressDialog.show();
                                    }

                                    @Override
                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                        super.onResourceReady(resource, animation);
                                        Glide.with(MainActivity.this)
                                                .load(user.getUserIcon())
                                                .into(new GlideDrawableImageViewTarget(icon) {
                                                    @Override
                                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                                        super.onResourceReady(resource, animation);
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                    }
                                });
                        //是否是管理员
                        if (user.isAdmin()) {
                            userAdmin.setText("商家账户");
                        } else {
                            userAdmin.setText("普通账户");
                        }
                    }
                }
                break;
            case ALL_ADS:
                if (resultCode == RESULT_OK){
                    String all = data.getStringExtra("all");
                    if(all.equals("success")){
                        RefreshMapData();
                    }
                }
                break;
            case VIEW_ALL_ADS:
                if (resultCode == RESULT_OK){
                    String all = data.getStringExtra("all");
                    if(all.equals("success")){
                        RefreshMapData();
                    }
                }
                break;
            case ADS_DETAIL:
                if(resultCode == RESULT_OK){
                    String detail = data.getStringExtra("detail_info");
                    if (detail.equals("success")){
                        RefreshMapData();
                    }
                }
                break;
        }
    }

    //寻找经纬度对应序号
    private int SearchTempMarkerIdFromLatLng(LatLng latLng){
        int temp = 0;
        for(int i = 0; i < tempLatLng.size(); i ++){
            if(tempLatLng.get(i).equals(latLng)){
                temp = i;
                break;
            }
        }
        return temp;
    }
    //搜索界面回调所传递的Marker
    private Marker SearchCorrespondingMarker(LatLng latLng){
        Marker temp = null;
        for(int i = 0; i < tempMarker.size(); i++){
            if(tempMarker.get(i).getPosition().equals(latLng)){
                temp = tempMarker.get(i);
                break;
            }
        }
        return temp;
    }
    //寻找最大纬度
    private double SearchMaxLatitude(List<LatLng> latitude){
        double max = latitude.get(0).latitude;
        for(LatLng latlng : latitude){
            if(latlng.latitude > max){
                max = latlng.latitude;
            }
        }
        return max;
    }
    //寻找最小纬度
    private double SearchMinLatitude(List<LatLng> latitude){
        double min = latitude.get(0).latitude;
        for (LatLng latlng : latitude){
            if(latlng.latitude < min){
                min = latlng.latitude;
            }
        }
        return min;
    }
    //寻找最大经度
    private double SearchMaxLongitude(List<LatLng> latitude){
        double max = latitude.get(0).longitude;
        for(LatLng latlng : latitude){
            if(latlng.longitude > max){
                max = latlng.longitude;
            }
        }
        return max;
    }
    //寻找最小经度
    private double SearchMimLongitude(List<LatLng> latitude){
        double min = latitude.get(0).longitude;
        for (LatLng latlng : latitude){
            if(latlng.longitude < min){
                min = latlng.longitude;
            }
        }
        return min;
    }
    //这是最底层activity,不需要背景透明
    @Override
    public boolean isTransparent() {
        return false;
    }
}
