package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Main.Fragment.ADsCompanyFragment;
import com.xia.adgis.Main.Fragment.ADsMaintainFragment;
import com.xia.adgis.Main.Fragment.ADsMessageFragment;
import com.xia.adgis.Main.Fragment.ADsPhysicalFragment;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.Utils.DragScrollDetailsLayout;
import com.xia.adgis.R;
import com.xia.imagewatch.GlideProgress.ProgressInterceptor;
import com.xia.imagewatch.GlideProgress.ProgressListener;
import com.xia.imagewatch.RolloutBDInfo;
import com.xia.imagewatch.RolloutInfo;
import com.xia.imagewatch.RolloutPreviewActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ADsDetailActivity extends AppCompatActivity {

    @BindView(R.id.detailDrag)
    DragScrollDetailsLayout mDragScrollDetailsLayout;
    @BindView(R.id.imageDetail)
    ImageView imageDetail;
    @BindView(R.id.detailViewPager)
    ViewPager viewPager;
    @BindView(R.id.flag_tips)
    TextView mTextView;
    @BindView(R.id.adsDetail)
    LinearLayout adsDetail;
    @BindView(R.id.detailTabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.detailToolBar)
    Toolbar toolbar;
    @BindView(R.id.detailButtonBarLayout)
    View buttonBar;
    @BindView(R.id.detailTitle)
    TextView title;
    @BindView(R.id.detail_name)
    TextView detailName;
    @BindView(R.id.detail_brief)
    TextView detailBiref;
    @BindView(R.id.last_edit_time)
    TextView lastTime;
    private int mScrollY = 0;
    //图片浏览相关
    private ArrayList<RolloutInfo> data = new ArrayList<>();
    protected RolloutBDInfo bdInfo;
    protected RolloutInfo imageInfo;
    private String imageID;
    //碎片
    ADsPhysicalFragment aDsPhysicalFragment;
    ADsCompanyFragment aDsCompanyFragment;
    ADsMaintainFragment aDsMaintainFragment;
    ADsMessageFragment aDsMessageFragment;
    //定义要装fragment的列表
    private ArrayList<Fragment> list_fragment=new ArrayList<Fragment>();
    //tablayout的标题
    private ArrayList<String> list_title=new ArrayList<>();
    //viewpager与tablayout共用适配器
    TabAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_detail);
        ButterKnife.bind(this);
        title.setText("广告牌详细信息");
        //toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //沉浸模式
        StatusBarUtil.immersive(this, true);
        StatusBarUtil.setPaddingSmart(this,toolbar);
        StatusBarUtil.setPaddingSmart(this,adsDetail);
        //开始不透明
        buttonBar.setAlpha(0);
        toolbar.setBackgroundColor(0);
        //碎片初始化
        aDsPhysicalFragment = new ADsPhysicalFragment();
        aDsMessageFragment = new ADsMessageFragment();
        aDsCompanyFragment = new ADsCompanyFragment();
        aDsMaintainFragment = new ADsMaintainFragment();
        //添加碎片
        list_fragment.add(aDsPhysicalFragment);
        list_fragment.add(aDsCompanyFragment);
        list_fragment.add(aDsMaintainFragment);
        list_fragment.add(aDsMessageFragment);
        //添加标题
        list_title.add("文字内容");
        list_title.add("物理信息");
        list_title.add("公司信息");
        list_title.add("维护信息");
        //tablelayout与viewpager逻辑
        adapter = new TabAdapter(getSupportFragmentManager());
        //TabLayout与ViewPager关联
        //ViewPager滑动关联TabLayout
        mTabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);
        mDragScrollDetailsLayout.setOnSlideDetailsListener(new DragScrollDetailsLayout.OnSlideFinishListener() {
            @Override
            public void onStatueChanged(DragScrollDetailsLayout.CurrentTargetIndex status) {
                if(status == DragScrollDetailsLayout.CurrentTargetIndex.UPSTAIRS){
                    mTextView.setText("上拉展现更多");
                }else{
                    mTextView.setText("回到顶部");
                }
            }
        });

        mDragScrollDetailsLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private int h = DensityUtil.dp2px(300);
            private int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)&0x00ffffff;
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBar.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                }
                lastScrollY = scrollY;
            }
        });
        //加载
        imageID = getIntent().getStringExtra("data");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("加载中");
        ProgressInterceptor.addListener(imageID, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });
        Glide.with(this).
                load(imageID)
                .into(new GlideDrawableImageViewTarget(imageDetail){
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        progressDialog.show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        BmobQuery<AD> temple = new BmobQuery<>();
                        temple.addWhereEqualTo("imageID",imageID);
                        temple.findObjects(new FindListener<AD>() {
                            @Override
                            public void done(List<AD> list, BmobException e) {
                                if (e == null){
                                    detailName.setText(list.get(list.size() - 1).getName());
                                    detailBiref.setText(list.get(list.size() - 1).getBrief());
                                    String last = getString(R.string.front) + list.get(list.size() - 1).getUpdatedAt();
                                    lastTime.setText(last);
                                    progressDialog.dismiss();
                                    ProgressInterceptor.removeListener(imageID);
                                }else{
                                    Toast.makeText(ADsDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
        //初始化图像
        initImage();
        imageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int location[] = new int[2];
                imageDetail.getLocationOnScreen(location);
                bdInfo.x = location[0];
                bdInfo.y = location[1];
                //视图布局的宽高
                bdInfo.width = imageDetail.getWidth();
                bdInfo.height = imageDetail.getHeight();
                Intent intent = new Intent(ADsDetailActivity.this, RolloutPreviewActivity.class);
                intent.putExtra("data", (Serializable) data);
                intent.putExtra("bdinfo",bdInfo);
                intent.putExtra("type", 0);//单图传0
                intent.putExtra("index",0);
                startActivity(intent);
            }
        });
    }

    private void initImage(){
        data = new ArrayList<>();
        bdInfo = new RolloutBDInfo();
        imageInfo = new RolloutInfo();
        imageInfo.width = 1440;
        imageInfo.height = 1600;
        imageInfo.url = imageID;
        data.add(imageInfo);
    }


    private class TabAdapter extends FragmentPagerAdapter {
        private TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }

        @Override
        public int getCount() {
            return list_fragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list_title.get(position);
        }
    }
}
