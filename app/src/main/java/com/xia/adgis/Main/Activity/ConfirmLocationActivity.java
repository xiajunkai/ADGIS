package com.xia.adgis.Main.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.R;
import com.xia.toprightmenu.MenuItem;
import com.xia.toprightmenu.TopRightMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmLocationActivity extends AppCompatActivity {

    //地图
    @BindView(R.id.confirm_map)
    MapView mMapView;
    private AMap mAMap;
    private UiSettings mUiSettings;
    private Marker locationMarker;
    //自定义定位相关
    public AMapLocationClient mAMapLocationClient;
    public AMapLocationClientOption mAMapLocationClientOption;
    //帮助
    @BindView(R.id.confirm_help)
    ImageView help;
    private TopRightMenu popMenu;
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmlocation);
        ButterKnife.bind(this);
        //沉浸式任务栏
        StatusBarUtil.immersive(this, true);
        //地图设置
        initMapSetting();
        mMapView.onCreate(savedInstanceState);
        //查看打开模式
        initMode();
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popMenu = new TopRightMenu(ConfirmLocationActivity.this);
                popMenu.addMenuItem(new MenuItem(R.drawable.ads_message,
                        "在本界面中长按地图可选择地点的经纬度"))
                        .showAsDropDown(help,-830,-25)
                        .dimBackground(true);
            }
        });
    }

    //地图初始化相关
    private void initMapSetting() {
        //初始化MapView
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.showIndoorMap(true);
            mUiSettings = mAMap.getUiSettings();
            //自定义定位相关
            initLocation();
        }
        mAMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmLocationActivity.this);
                builder.setTitle("经纬度选择");
                builder.setMessage("你确定选择当前地点么？" +
                        "\n" + "经度：" + latLng.longitude +
                        "\n" + "纬度：" + latLng.latitude);
                longitude = latLng.longitude;
                latitude = latLng.latitude;
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Confirm(new LatLng(latitude,longitude));
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
        //地图基本的UI设置
        //标志设置在左下方
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
        //显示比例尺
        mUiSettings.setScaleControlsEnabled(true);
        //缩放按钮不显示
        mUiSettings.setZoomControlsEnabled(false);
    }

    private void initMode(){
        Intent intent = getIntent();
        boolean isShowLocation = intent.getBooleanExtra("mode", false);
        if (isShowLocation){
            //编辑模式
            LatLng latLng = intent.getParcelableExtra("location");
            if (locationMarker == null) {
                //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
                locationMarker = mAMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ad_location))));
            } else {
                //已经添加过了，修改位置即可
                locationMarker.remove();
                locationMarker = mAMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ad_location))));
            }
            mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    latLng, 15, 0, 0)), 500, null);
        }else {
            //添加模式
            //进去就定位
            if (mAMapLocationClient != null) {
                mAMapLocationClient.startLocation();
            }
        }
    }
    private void Confirm(LatLng lng){
        Intent intent = new Intent();
        intent.putExtra("location_info", lng);
        setResult(RESULT_OK, intent);
        onBackPressed();
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
                                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ad_location))));
                        } else {
                            //已经添加过了，修改位置即可
                            locationMarker.remove();
                            locationMarker = mAMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ad_location))));
                        }

                        //然后可以移动到定位点,使用animateCamera就有动画效果
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                latLng, 15, 0, 0)), 500, null);
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Toast.makeText(ConfirmLocationActivity.this, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo(), Toast.LENGTH_SHORT).show();
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
        //退出界面的时候停止定位
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1,R.anim.out_1);
    }
}
