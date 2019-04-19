package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xia.adgis.Main.Adapter.DragDetailFragmentPagerAdapter;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.Main.Fragment.ADsCompanyFragment;
import com.xia.adgis.Main.Fragment.ADsMaintainFragment;
import com.xia.adgis.Main.Fragment.ADsMessageFragment;
import com.xia.adgis.Main.Fragment.ADsPhysicalFragment;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.Register.Bean.User;
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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class ADsDetailActivity extends AppCompatActivity {

    //请求码
    private final static int EDIT = 50;
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
    private  String imageID;
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
    //当前查看广告牌名称
    private String adsName;
    //当前广告牌发布者
    private String adsEditor;
    //当前留言用户
    User user;
    //留言是否成功
    boolean isSuccess = false;
    //编辑是否正常
    String isEdit = "fail";
    //
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_detail);
        ButterKnife.bind(this);
        isEdit = "fail";
        //获取当前广告牌名称
        adsName = getIntent().getStringExtra("data");
        title.setText(adsName);
        //toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //沉浸模式
        StatusBarUtil.immersive(this, true);
        StatusBarUtil.setPaddingSmart(this,toolbar);
        StatusBarUtil.setPaddingSmart(this,adsDetail);
        //开始不透明
        buttonBar.setAlpha(0);
        toolbar.setBackgroundColor(0);
        //碎片初始化
        aDsCompanyFragment = new ADsCompanyFragment();
        aDsMessageFragment = new ADsMessageFragment();
        aDsPhysicalFragment = new ADsPhysicalFragment();
        aDsMaintainFragment = new ADsMaintainFragment();
        //添加碎片
        list_fragment.add(aDsCompanyFragment);
        list_fragment.add(aDsMessageFragment);
        list_fragment.add(aDsPhysicalFragment);
        list_fragment.add(aDsMaintainFragment);
        //添加标题
        list_title.add("公司信息");
        list_title.add("留言");
        list_title.add("物理内容");
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
        toolbar.setBackgroundColor(0);
        //加载
        loadingData();
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

    //加载数据
    private void loadingData(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("加载中");
        BmobQuery<AD> temple = new BmobQuery<>();
        temple.addWhereEqualTo("name",adsName);
        temple.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if (e == null){
                    detailName.setText(adsName);
                    detailBiref.setText(list.get(list.size() - 1).getBrief());
                    String last = getString(R.string.front) + list.get(list.size() - 1).getUpdatedAt();
                    lastTime.setText(last);
                    adsEditor = list.get(list.size() - 1).getEditor();
                    imageID = list.get(list.size() - 1).getImageID();
                    ProgressInterceptor.addListener(imageID, new ProgressListener() {
                        @Override
                        public void onProgress(int progress) {
                            progressDialog.setProgress(progress);
                        }
                    });
                    Glide.with(ADsDetailActivity.this).
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
                                    progressDialog.dismiss();
                                    ProgressInterceptor.removeListener(imageID);
                                }
                            });
                    initImage();
                }else {
                    Toast.makeText(ADsDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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

    //设配器相关
    private class TabAdapter extends DragDetailFragmentPagerAdapter {
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

        //处理滑动冲突
        private View mCurrentView;
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if(object instanceof View) {
                mCurrentView = (View) object;
            }else if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                mCurrentView = fragment.getView();
            }
        }

        @Override
        public View getPrimaryItem() {
            return mCurrentView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_message,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.ads_detail_edit:
                user = BmobUser.getCurrentUser(User.class);
                if(user.isAdmin()){
                    if(user.getUsername().equals(adsEditor)){
                        Intent intent = new Intent(new Intent(ADsDetailActivity.this, EditADsActivity.class));
                        intent.putExtra("ads_name",adsName);
                        startActivityForResult(intent,EDIT);
                        overridePendingTransition(R.anim.in, R.anim.out);
                    } else {
                        Toast.makeText(this, "当前广告牌不是您发布的，无法编辑当前广告牌！", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "您不是商家账户，无法编辑当前广告牌！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ads_detail_message:
                LeaveMessage();
                break;
        }
        return true;
    }
    //留言
    private void LeaveMessage(){
        //获取当前留言用户
        user = BmobUser.getCurrentUser(User.class);
        //dialog留言框
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.leave_message, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("留言");
        builder.setView(view);
        final EditText messageEdit = (EditText) view.findViewById(R.id.leave_message_edit);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = messageEdit.getText().toString();
                //当前留言部分
                Messages messages = new Messages();
                messages.setContent(s);
                messages.setUserName(user.getUsername());
                messages.setAdName(adsName);
                messages.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            isSuccess = true;
                            //通知留言碎片进行更改
                            aDsMessageFragment.refresh(new ADsMessageFragment.CallBack() {
                                @Override
                                public boolean isLeaveMessageSuccess() {
                                    return isSuccess;
                                }
                            });
                            //Toast.makeText(ADsDetailActivity.this, "留言成功！", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ADsDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDIT){
            if (resultCode == RESULT_OK){
                isEdit = data.getStringExtra("edit_ad");
                if(isEdit.equals("success")){
                    loadingData();
                }
            }
        }
    }

    public String getAdsName() {
        return adsName;
    }

    public String getIsEdit() {
        return isEdit;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("detail_info",isEdit);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
