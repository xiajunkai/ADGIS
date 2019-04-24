package com.xia.adgis.Main.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xia.adgis.Main.Adapter.AllADsAdapter;
import com.xia.adgis.Main.Adapter.ViewAllADsAdapter;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
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
    private AllADsAdapter allADsAdapter;
    private ViewAllADsAdapter viewAllADsAdapter;
    //回调请求码
    public static final int ADD_ADS = 30;
    public static final int EDIT_ADS = 20;
    private String result = "fail";
    //当前用户
    User user = null;
    //传递的索引
    private String userName = null;
    private String passUserName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_ads);
        ButterKnife.bind(this);
        //选择账户
        initUser();
        adsRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshADs(passUserName);
            }
        });
        //添加节点
        addADs();
    }

    private void initUser(){
        user = new User();
        Intent intent = getIntent();
        userName = intent.getStringExtra("user_name");
        if (TextUtils.isEmpty(userName)){
            user = BmobUser.getCurrentUser(User.class);
            passUserName = user.getUsername();
            initToolbar(passUserName);
            //初始化数据
            initADs(user.getUsername());
        } else if(userName.equals("all")){
            initToolbar(userName);
            initAllADs();
            addADs.setVisibility(View.GONE);
        } else {
            passUserName = userName;
            initToolbar(userName);
            //初始化数据
            initADs(userName);
            addADs.setVisibility(View.GONE);
        }
    }

    private void initToolbar(String name){
        if (name.equals("all")){
            adsToolbar.setTitle("所有广告");
        }else {
            adsToolbar.setTitle(name + "发布的广告");
        }
        setSupportActionBar(adsToolbar);
        adsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initAllADs(){
        //使用Bmob获取用来标记的信息
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        adBmobQuery.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if(e == null){
                    adList = list;
                    GridLayoutManager layoutManager = new GridLayoutManager(AllADsActivity.this, 1);
                    adsDetail.setLayoutManager(layoutManager);
                    viewAllADsAdapter = new ViewAllADsAdapter(adList);
                    adsDetail.setAdapter(viewAllADsAdapter);
                }else {
                    Toast.makeText(AllADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initADs(String name) {
        //使用Bmob获取用来标记的信息
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        adBmobQuery.addWhereEqualTo("editor",name);
        adBmobQuery.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if(e == null){
                    adList = list;
                    GridLayoutManager layoutManager = new GridLayoutManager(AllADsActivity.this, 2);
                    adsDetail.setLayoutManager(layoutManager);
                    allADsAdapter = new AllADsAdapter(adList);
                    adsDetail.setAdapter(allADsAdapter);
                }else {
                    Toast.makeText(AllADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void refreshADs(String name){
        //查看全部
        if (name.equals("all")){
            //使用Bmob获取用来标记的信息
            BmobQuery<AD> adBmobQuery = new BmobQuery<>();
            adBmobQuery.findObjects(new FindListener<AD>() {
                @Override
                public void done(List<AD> list, BmobException e) {
                    if(e == null){
                        adList.clear();
                        adList.addAll(list);
                        viewAllADsAdapter.notifyDataSetChanged();
                        adsRefresh.finishRefresh();
                    }else {
                        Toast.makeText(AllADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            //查看个人
            //使用Bmob获取用来标记的信息
            BmobQuery<AD> adBmobQuery = new BmobQuery<>();
            adBmobQuery.addWhereEqualTo("editor", name);
            adBmobQuery.findObjects(new FindListener<AD>() {
                @Override
                public void done(List<AD> list, BmobException e) {
                    if (e == null) {
                        adList.clear();
                        adList.addAll(list);
                        allADsAdapter.notifyDataSetChanged();
                        adsRefresh.finishRefresh();
                    } else {
                        Toast.makeText(AllADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addADs(){
        addADs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AllADsActivity.this, AddAdsActivity.class),ADD_ADS);
                overridePendingTransition(R.anim.in,R.anim.out);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if (requestCode == EDIT_ADS){
                result= data.getStringExtra("detail_info");
                if(result.equals("success")){
                    adsRefresh.autoRefresh();
                }
            }else if(requestCode == ADD_ADS){
                result= data.getStringExtra("add_ad");
                if(result.equals("success")){
                    adsRefresh.autoRefresh();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("all",result);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1,R.anim.out_1);
    }

    public RefreshLayout getAdsRefresh() {
        return adsRefresh;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isAdapterClickable(){
        return TextUtils.isEmpty(userName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userName = null;
    }
}
