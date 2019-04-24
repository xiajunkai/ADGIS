package com.xia.adgis.Main.Activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.swipeback.SwipeBackActivityImpl;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xia.adgis.DragPhotoActivity;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

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

public class UserCentreActivity extends SwipeBackActivityImpl implements PopupWindow.OnDismissListener {

    //回调请求
    private static final int EDIT_USER = 11;
    private String result = "fail";
    //
    private int mOffset = 0;
    private int mScrollY = 0;
    User user;
    private String passUserName;
    //刷新框架
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.toolbar_centre)
    Toolbar toolbar;
    //背景图片
    @BindView(R.id.parallax)
    View parallax;
    //toolbar上的标题布局
    @BindView(R.id.buttonBarLayout)
    View buttonBar;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    //头像
    @BindView(R.id.avatar)
    CircleImageView avatar;
    //用户相关
    @BindView(R.id.centre_nickname)
    TextView userNickname;
    @BindView(R.id.centre_moto)
    TextView userMoto;
    @BindView(R.id.centre_username)
    TextView userName;
    @BindView(R.id.centre_mail)
    TextView userMail;
    @BindView(R.id.centre_phone)
    TextView userPhone;
    @BindView(R.id.centre_sex)
    TextView userSex;
    @BindView(R.id.centre_birthday)
    TextView userBrithday;
    @BindView(R.id.centre_area)
    TextView userArea;
    @BindView(R.id.title)
    TextView Title;
    @BindView(R.id.more_info)
    LinearLayout more;
    //图片查看相关
    ArrayList<String> path = new ArrayList<>();
    //选择头像弹出窗口
    private PopupWindow popupWindow;
    //弹窗弹出的位置
    private int navigationHeight;
    View mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_centre);
        ButterKnife.bind(this);
        //账户选择
        initUser();
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, true);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshLayout) {
                if (TextUtils.isEmpty(passUserName)) {
                    user = BmobUser.getCurrentUser(User.class);
                    //头像加载
                    userNickname.setText(user.getNickName());
                    userMoto.setText(user.getMotto());
                    userName.setText(user.getUsername());
                    userMail.setText(user.getEmail());
                    userPhone.setText(user.getMobilePhoneNumber());
                    userSex.setText(user.getSex());
                    userBrithday.setText(user.getBirthday());
                    userArea.setText(user.getAddress());
                    Title.setText(user.getNickName());
                    Glide.with(UserCentreActivity.this).
                            load(user.getUserIcon()).
                            into(new GlideDrawableImageViewTarget(avatar) {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                    super.onResourceReady(resource, animation);
                                    refreshLayout.finishRefresh();
                                }
                            });
                }
            }
        });
        //上下滑动效果
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                parallax.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }

            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                parallax.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private int h = DensityUtil.dp2px(200);
            private int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBar.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                    parallax.setTranslationY(mOffset - mScrollY);
                }
                lastScrollY = scrollY;
            }
        });
        buttonBar.setAlpha(0);
        toolbar.setBackgroundColor(0);
        //查看图片
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path.clear();
                Intent intent = new Intent(UserCentreActivity.this, DragPhotoActivity.class);
                int location[] = new int[2];
                avatar.getLocationOnScreen(location);
                path.add(user.getUserIcon());
                intent.putExtra("index", 0);
                intent.putExtra("path", (Serializable) path);
                intent.putExtra("left", location[0]);
                intent.putExtra("top", location[1]);
                intent.putExtra("height", avatar.getHeight());
                intent.putExtra("width", avatar.getWidth());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //点击更多
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreInfo();
            }
        });
    }
    private void moreInfo(){
        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        mView = LayoutInflater.from(this).inflate(R.layout.show_more,null);
        popupWindow = new PopupWindow(mView, RelativeLayout.LayoutParams.MATCH_PARENT,
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
        setOnPopupViewClick(mView);
        //设置背景色(半透明)
        setBackgroundAlpha(0.5f);
    }

    //弹窗内点击事件
    private void setOnPopupViewClick(View view) {
        TextView ad = (TextView) view.findViewById(R.id.my_ad);
        TextView message = (TextView) view.findViewById(R.id.my_message);
        TextView cancel = (TextView) view.findViewById(R.id.my_cancel);
        //广告
        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCentreActivity.this, AllADsActivity.class);
                intent.putExtra("user_name",user.getUsername());
                startActivity(intent);
                overridePendingTransition(R.anim.in,R.anim.out);
                popupWindow.dismiss();
            }
        });
        //选择图片
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCentreActivity.this, MessageActivity.class);
                intent.putExtra("user_name",user.getUsername());
                startActivity(intent);
                overridePendingTransition(R.anim.in,R.anim.out);
                popupWindow.dismiss();
            }
        });
        //取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
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

    private void initUser(){
        user = new User();
        //两种模式，一种是查看自身，一种是查看别人
        Intent intent = getIntent();
        passUserName = intent.getStringExtra("user_name");
        if(TextUtils.isEmpty(passUserName)){
            user = BmobUser.getCurrentUser(User.class);
            initUserData();
            refreshLayout.autoRefresh();
        }else {
            BmobQuery<User> userQuery = new BmobQuery<>();
            userQuery.addWhereEqualTo("username",passUserName);
            userQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    user = list.get(list.size() - 1);
                    initUserData();
                    refreshLayout.setEnableRefresh(false);
                }
            });
        }
    }

    private void initUserData(){
        //头像加载
        Glide.with(this).
                load(user.getUserIcon()).
                into(avatar);
        userNickname.setText(user.getNickName());
        userMoto.setText(user.getMotto());
        userName.setText(user.getUsername());
        userMail.setText(user.getEmail());
        userPhone.setText(user.getMobilePhoneNumber());
        userSex.setText(user.getSex());
        userBrithday.setText(user.getBirthday());
        userArea.setText(user.getAddress());
        Title.setText(user.getNickName());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_centre,menu);
        return TextUtils.isEmpty(passUserName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_edit:
                Intent intent = new Intent(UserCentreActivity.this,EditUserActivity.class);
                startActivityForResult(intent,EDIT_USER);
                break;
        }
        return true;
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == EDIT_USER) {
                result = data.getStringExtra("edit_user");
                if (result.equals("success")){
                    refreshLayout.autoRefresh();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("centre",result);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setSwipeBackEnable(false);
    }


}
