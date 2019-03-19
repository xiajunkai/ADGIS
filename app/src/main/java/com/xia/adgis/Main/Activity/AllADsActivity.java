package com.xia.adgis.Main.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xia.adgis.Main.Adapter.ADsAdapter;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AllADsActivity extends AppCompatActivity {

    @BindView(R.id.all_ads_refresh)
    RefreshLayout adsRefresh;
    @BindView(R.id.all_ads_detail)
    RecyclerView adsDetail;
    @BindView(R.id.all_ads_toolbar)
    Toolbar adsToolbar;
    @BindView(R.id.add_ads)
    FloatingActionButton addADs;
    private List<AD> adList = new ArrayList<>();
    private ADsAdapter adapter;
    //回调请求码
    public static final int EDIT_ADS = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_ads);
        ButterKnife.bind(this);
        adsToolbar.setTitle("管理广告");
        setSupportActionBar(adsToolbar);
        adsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
            }
        });
        adsRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshADs();
            }
        });
        //初始化数据
        initADs();
        //添加节点
        addADs();
    }

    private void initADs() {
        //使用Bmob获取用来标记的信息
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        //先判断是否有缓存
        boolean isCache = adBmobQuery.hasCachedResult(AD.class);
        if(isCache){
            adBmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);	// 先从缓存取数据，如果没有的话，再从网络取。
        }else{
            adBmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);	// 如果没有缓存的话，则先从网络中取
        }
        adBmobQuery.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if(e == null){
                    adList = list;
                    GridLayoutManager layoutManager = new GridLayoutManager(AllADsActivity.this, 2);
                    adsDetail.setLayoutManager(layoutManager);
                    adapter = new ADsAdapter(adList);
                    adsDetail.setAdapter(adapter);
                }else {
                    Toast.makeText(AllADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void refreshADs(){
        //使用Bmob获取用来标记的信息
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        //先判断是否有缓存
        adBmobQuery.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if(e == null){
                    adList.clear();
                    adList.addAll(list);
                    adapter.notifyDataSetChanged();
                    adsRefresh.finishRefresh();
                }else {
                    Toast.makeText(AllADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addADs(){
        addADs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllADsActivity.this, AddAdsActivity.class));
                overridePendingTransition(R.anim.in,R.anim.out);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDIT_ADS){
            if (resultCode == RESULT_OK){
                //String s = data.getStringExtra("detail_info");
                //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
