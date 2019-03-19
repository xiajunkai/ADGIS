package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.kevin.crop.UCrop;
import com.xia.adgis.CropActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Main.Bean.ADCompany;
import com.xia.adgis.Main.Bean.ADmaintain;
import com.xia.adgis.Main.Bean.ADphysical;
import com.xia.adgis.Main.Tool.EditCompanyDialog;
import com.xia.adgis.Main.Tool.EditMaintainDialog;
import com.xia.adgis.Main.Tool.EditPhysicalDialog;
import com.xia.adgis.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class EditADsActivity extends AppCompatActivity implements View.OnClickListener,PopupWindow.OnDismissListener {

    @BindView(R.id.edit_ads_toolbar)
    Toolbar toolbar;
    //图片
    @BindView(R.id.edit_ads_edit_photo)
    RelativeLayout editPhoto;
    @BindView(R.id.edit_ads_image)
    ImageView showImage;
    @BindView(R.id.edit_ads_brief)
    TextView editBrief;
    //广告牌基本信息
    @BindView(R.id.edit_ads_location)
    LinearLayout editLocation;
    @BindView(R.id.edit_ads_longitude)
    TextView editLongitude;
    @BindView(R.id.edit_ads_latitude)
    TextView editLatitude;
    //广告牌物理信息
    @BindView(R.id.edit_physical)
    LinearLayout editPhysical;
    @BindView(R.id.edit_ads_length)
    TextView editLength;
    @BindView(R.id.edit_ads_width)
    TextView editWidth;
    @BindView(R.id.edit_ads_height)
    TextView editHeight;
    @BindView(R.id.edit_ads_material)
    TextView editMaterial;
    //广告牌公司信息
    @BindView(R.id.edit_company)
    LinearLayout editCompany;
    @BindView(R.id.edit_ads_designer)
    TextView editDesigner;
    @BindView(R.id.edit_ads_holder)
    TextView editHolder;
    //广告牌维护信息
    @BindView(R.id.edit_maintain)
    LinearLayout editMaintain;
    @BindView(R.id.edit_ads_maintain_company)
    TextView editMaintainCompany;
    @BindView(R.id.edit_ads_maintain_content)
    TextView editContent;
    @BindView(R.id.edit_ads_maintain_time)
    TextView editTime;
    //加载框
    ProgressDialog loading;
    //当前广告牌相关
    AD ad;
    private String adsName;
    private String objectID_AD;
    private String objectID_PHY;
    private String objectID_COM;
    private String objectID_MAIN;
    /**
     *图像相关
     */
    //相册选图标记
    private static final int GALLERY_REQUEST = 0;
    //相机拍照标记
    private static final int CAMERA_REQUEST = 1;
    // 拍照临时图片
    private String mTempPhotoPath;
    // 剪切后图像文件
    private Uri mDestinationUri;
    //选择头像弹出窗口
    private PopupWindow popupWindow;
    //弹窗弹出的位置
    private int navigationHeight;
    View mView;
    //图片路径
    String imagePath;
    //是否编辑了图像，默认为不编辑
    boolean isIcon = false;
    //前一次头像文件
    String lastImage;
    BmobFile lastTimeImage;
    //这一次
    BmobFile thisTimeImage;
    /**
     * 输入简介部分
     */
    //输入简介中的输入框
    EditText briefEdit;
    /**
     *选择地址部分
     */
    private static final int LOCATION_REQUEST = 7;
    /**
     * 选择维护时间
     */
    TimePickerView mTimePickerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ads);
        ButterKnife.bind(this);
        //初始化工具栏
        initToolbar();
        //初始化加载框
        initProgressDialog();
        //进入后加载信息
        loadingMessage();
        //图片变量初始化
        initImageVar();
        //时间选择
        initTimePicker();
        //注册点击监听
        registerClick();
    }
    //初始化工具栏
    private void initToolbar(){
        toolbar.setTitle("编辑广告牌信息");
        setSupportActionBar(toolbar);
    }
    //初始化加载对话框
    private void initProgressDialog(){
        loading = new ProgressDialog(this);
        loading.setMessage("加载中...");
        loading.setCancelable(true);
    }

    /**
     * 进入后加载信息
     */
    private void loadingMessage(){
        //获取广告名
        adsName = getIntent().getStringExtra("ads_name");
        BmobQuery<AD> result = new BmobQuery<>();
        result.addWhereEqualTo("name",adsName);
        //开始显示加载
        loading.show();
        result.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if (e == null) {
                    ad = list.get(list.size() - 1);
                    //加载综合信息
                    editBrief.setText(ad.getBrief());
                    editLongitude.setText(String.valueOf(ad.getLongitude()));
                    editLatitude.setText(String.valueOf(ad.getLatitude()));
                    //作用于更换图像，储存原来图像路径，方便删除
                    lastImage = ad.getImageID();
                    //
                    objectID_AD = ad.getObjectId();
                    loadingImage();
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //加载头像
    private void loadingImage(){
        Glide.with(this)
                .load(ad.getImageID())
                .into(new GlideDrawableImageViewTarget(showImage){
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        loading.dismiss();
                        Toast.makeText(EditADsActivity.this, "图像加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        //loading.dismiss();
                        //加载物理信息
                        loadingPhysical();
                    }
                });
    }
    //加载物理信息
    private void loadingPhysical(){
        BmobQuery<ADphysical> result = new BmobQuery<>();
        result.addWhereEqualTo("name",adsName);
        result.findObjects(new FindListener<ADphysical>() {
            @Override
            public void done(List<ADphysical> list, BmobException e) {
                if(e == null){
                    objectID_PHY = list.get(list.size() - 1).getObjectId();
                    editLength.setText(list.get(list.size() - 1).getLength());
                    editWidth.setText(list.get(list.size() - 1).getWidth());
                    editHeight.setText(list.get(list.size() - 1).getHeight());
                    editMaterial.setText(list.get(list.size() - 1).getMaterial());
                    //loading.dismiss();
                    //加载公司信息
                    loadingCompany();
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //加载公司信息
    private void loadingCompany(){
        BmobQuery<ADCompany> result = new BmobQuery<>();
        result.addWhereEqualTo("name", adsName);
        result.findObjects(new FindListener<ADCompany>() {
            @Override
            public void done(List<ADCompany> list, BmobException e) {
                if(e == null){
                    objectID_COM = list.get(list.size() - 1).getObjectId();
                    editDesigner.setText(list.get(list.size() - 1).getDesigner());
                    editHolder.setText(list.get(list.size() - 1).getHoder());
                    //加载维护信息
                    loadingMaintain();
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //加载维护信息
    private void loadingMaintain(){
        BmobQuery<ADmaintain> result = new BmobQuery<>();
        result.addWhereEqualTo("name",adsName);
        result.findObjects(new FindListener<ADmaintain>() {
            @Override
            public void done(List<ADmaintain> list, BmobException e) {
                if(e == null){
                    objectID_MAIN = list.get(list.size() - 1).getObjectId();
                    editMaintainCompany.setText(list.get(list.size() - 1).getCompany());
                    editContent.setText(list.get(list.size() - 1).getContext());
                    editTime.setText(list.get(list.size() - 1).getTime());
                    loading.dismiss();
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 点击事件
     */
    //注册点击事件
    private void registerClick(){
        //综合信息
        editPhoto.setOnClickListener(this);
        editBrief.setOnClickListener(this);
        editLocation.setOnClickListener(this);
        //物理信息
        editPhysical.setOnClickListener(this);
        //公司信息
        editCompany.setOnClickListener(this);
        //维护信息
        editMaintain.setOnClickListener(this);
        editTime.setOnClickListener(this);
    }

    //所有点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_ads_edit_photo:
                selectImage();
                break;
            case R.id.edit_ads_brief:
                inputBrief();
                break;
            case R.id.edit_ads_location:
                confirmLocation();
                break;
            case R.id.edit_physical:
                EditPhysicalDialog physical = EditPhysicalDialog.newInstance(editLength.getText().toString()
                        ,editWidth.getText().toString(),editHeight.getText().toString(),editMaterial.getText().toString());
                physical.show(getSupportFragmentManager());
                break;
            case R.id.edit_company:
                EditCompanyDialog company = EditCompanyDialog.newInstance(editDesigner.getText().toString(),
                        editHolder.getText().toString());
                company.show(getSupportFragmentManager());
                break;
            case R.id.edit_maintain:
                EditMaintainDialog maintain = EditMaintainDialog.newInstance(editMaintainCompany.getText().toString(),
                        editContent.getText().toString());
                maintain.show(getSupportFragmentManager());
                break;
            case R.id.edit_ads_maintain_time:
                mTimePickerView.show();
                break;
            default:
                break;
        }
    }

    /**
     *选择图片
     */
    private void initImageVar(){
        //得到文件存储路径
        String filename = "cropImage.jpeg";
        //目标路径
        mDestinationUri = Uri.fromFile(new File(getCacheDir(),filename));
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mDestinationUri));
        //拍照临时照片
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
    }

    private void selectImage(){
        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        mView = LayoutInflater.from(this).inflate(R.layout.edit_icon_popwindow,null);
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
        TextView takePhoto = (TextView) view.findViewById(R.id.take_photo);
        TextView pickPicture = (TextView) view.findViewById(R.id.pick_picture);
        TextView cancel = (TextView) view.findViewById(R.id.picture_selector_cancel);
        //拍照
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePhoto();
            }
        });
        //选择图片
        pickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
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
    //点击popWindow背景透明度变化
    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }
    //拍照
    private void TakePhoto() {
        popupWindow.dismiss();
        Intent takeIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        //考虑兼容性
        Uri imageUri; //照片文件临时存储路径
        File temp = new File(mTempPhotoPath);
        if(Build.VERSION.SDK_INT < 24){
            imageUri = Uri.fromFile(temp);
        }else {
            imageUri = FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", temp);
        }
        //下面这句指定调用相机拍照后的照片存储的路径
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takeIntent, CAMERA_REQUEST);
    }
    //从系统相册选择
    private void pickFromGallery() {
        popupWindow.dismiss();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, GALLERY_REQUEST);
    }
    //剪切图片返回与编辑三大属性返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:   // 调用相机拍照
                    //考虑兼容性
                    Uri imageUri; //照片文件临时存储路径
                    File temp = new File(mTempPhotoPath);
                    if (Build.VERSION.SDK_INT < 24) {
                        imageUri = Uri.fromFile(temp);
                    } else {
                        imageUri = FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", temp);
                    }
                    startCropActivity(imageUri);
                    break;
                case GALLERY_REQUEST:  // 直接从相册获取
                    startCropActivity(data.getData());
                    break;
                case UCrop.REQUEST_CROP:    // 裁剪图片结果
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:    // 裁剪图片错误
                    handleCropError(data);
                    break;
                case LOCATION_REQUEST:
                    //传递位置信息
                    LatLng latLng = data.getParcelableExtra("location_info");
                    editLatitude.setText(String.valueOf(latLng.latitude));
                    editLongitude.setText(String.valueOf(latLng.longitude));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //裁剪图片方法实现
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1024, 1024)
                .withTargetActivity(CropActivity.class)
                .start(this);
    }
    //处理剪切成功的返回值
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri) {
            Bitmap bitmap = null;
            try {
                //只能使用这种方法得出
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                showImage.setImageBitmap(bitmap);
                String filePath = resultUri.getEncodedPath();
                //获得了图片路径
                imagePath = Uri.decode (filePath);
                //此时图像已经被编辑
                isIcon = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }
    //处理剪切失败返回值
    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }
    //删除拍照临时文件
    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

    /**
     * 编辑简介
     */
    private void inputBrief(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.input_brief, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("简介");
        builder.setView(mView);
        briefEdit = (EditText) mView.findViewById(R.id.input_brief_edit);
        //先判断主界面中TextView是否存在内容
        if(! editBrief.getText().equals("")){
            briefEdit.setText(editBrief.getText());
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editBrief.setText(briefEdit.getText());
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 选择经纬度
     */
    private void confirmLocation(){
        startActivityForResult(new Intent(EditADsActivity.this, ConfirmLocationActivity.class),LOCATION_REQUEST);
        overridePendingTransition(R.anim.in,R.anim.out);
    }

    /**
     *  时间选择
     */
    private void initTimePicker(){
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(2026,0,0);
        //时间选择器
        mTimePickerView = new TimePickerView.Builder(EditADsActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                editTime.setText(getTime(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, true, false, false})
                //设置显示标签
                .setLabel("年","月","日","时",null,null)
                .setTitleText("选择维护时间")
                .setDividerColor(Color.BLACK)
                //滚轮字体大小
                .setContentSize(20)
                //两横线之间间隔倍数1.2 ~ 2
                .setLineSpacingMultiplier(2.0f)
                .isCyclic(false)
                .setDate(selectedDate)
                .setRangDate(startDate,endDate)
                //设置为true则只有中间部分显示标签
                .isCenterLabel(true)
                .build();
    }

    //获取时间
    private String getTime(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
        return format.format(date);
    }
    //保存选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ads_add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.ads_save:
                saveADsModify();
                break;
        }
        return true;
    }

    private void saveADsModify(){
        //综合信息
        String brief = editBrief.getText().toString();
        double latitude = Double.valueOf(editLatitude.getText().toString());
        double longitude = Double.valueOf(editLongitude.getText().toString());
        //物理信息
        String length = editLength.getText().toString();
        String width = editWidth.getText().toString();
        String height = editHeight.getText().toString();
        String material = editMaterial.getText().toString();
        //公司信息
        String designer = editDesigner.getText().toString();
        String holder = editHolder.getText().toString();
        //维护信息
        String company = editMaintainCompany.getText().toString();
        String content = editContent.getText().toString();
        String time = editTime.getText().toString();
        if (TextUtils.isEmpty(brief)){
            Toast.makeText(this, "简介不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(length)){
            Toast.makeText(this, "长度不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(width)){
            Toast.makeText(this, "宽度不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(height)){
            Toast.makeText(this, "高度不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(material)){
            Toast.makeText(this, "材料不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(designer)){
            Toast.makeText(this, "设计者不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(holder)){
            Toast.makeText(this, "持有者不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(company)){
            Toast.makeText(this, "维护公司不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)){
            Toast.makeText(this, "维护内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(time)){
            Toast.makeText(this, "维护时间不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        loading.setMessage("修改信息中");
        loading.show();
        //修改信息
        if (isIcon) {
            saveADsAndIcon(brief,latitude,longitude,length,width,height,material,designer,holder,company,content,time);
        } else {
            upgradeADs(brief,latitude,longitude,length,width,height,material,designer,holder,company,content,time);
        }
    }

    //这是修改了图像的用户保存，需要获取当前图像网络路径，删除后再上传新图片，节省服务器存储空间
    private void saveADsAndIcon(final String brief, final double latitude, final double longitude,
                                final String length, final String width, final String height, final String material,
                                final String designer, final String holder,
                                final String company, final String content, final String time){
        //删除前一次图片文件
        lastTimeImage = new BmobFile();
        lastTimeImage.setUrl(lastImage);
        lastTimeImage.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    thisTimeImage = new BmobFile(new File(imagePath));
                    thisTimeImage.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                //更新信息
                                upgradeADsAndImage(brief,latitude,longitude,length,width,height,material,designer,holder,company,content,time);
                            }else {
                                Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }
    //更新广告牌信息
    private void upgradeADs(String brief, double latitude, double longitude,
                            final String length, final String width, final String height, final String material,
                            final String designer, final String holder,
                            final String company, final String content, final String time){
        AD ad = new AD();
        ad.setBrief(brief);
        ad.setLatitude(latitude);
        ad.setLongitude(longitude);
        ad.update(objectID_AD, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    upgradePhysical(length,width,height,material,designer,holder,company,content,time);
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }
    //更新广告牌信息(包含图像)
    private void upgradeADsAndImage(String brief, double latitude, double longitude,
                            final String length, final String width, final String height, final String material,
                            final String designer, final String holder,
                            final String company, final String content, final String time){
        AD ad = new AD();
        ad.setBrief(brief);
        ad.setLatitude(latitude);
        ad.setLongitude(longitude);
        ad.setImageID(thisTimeImage.getFileUrl());
        ad.update(objectID_AD, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    upgradePhysical(length,width,height,material,designer,holder,company,content,time);
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }
    //更新物理信息
    private void upgradePhysical(String length, String width, String height, String material,
                                 final String designer, final String holder,
                                 final String company, final String content, final String time){
        ADphysical phy = new ADphysical();
        phy.setLength(length);
        phy.setWidth(width);
        phy.setHeight(height);
        phy.setMaterial(material);
        phy.update(objectID_PHY, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    upgradeCompany(designer,holder,company,content,time);
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }

    //更新公司信息
    private void upgradeCompany(String designer, String holder,
                                final String company, final String content, final String time){
        ADCompany com = new ADCompany();
        com.setDesigner(designer);
        com.setHoder(holder);
        com.update(objectID_COM, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    upgradeMaintain(company,content,time);
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }

    //更新维护信息
    private void upgradeMaintain(String company, String content, String time){
        ADmaintain main = new ADmaintain();
        main.setCompany(company);
        main.setContext(content);
        main.setTime(time);
        main.update(objectID_MAIN, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Toast.makeText(EditADsActivity.this, "信息更新成功！", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else {
                    Toast.makeText(EditADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1,R.anim.out_1);
    }

    //碎片返回数据

    public TextView getEditLength() {
        return editLength;
    }

    public TextView getEditWidth() {
        return editWidth;
    }

    public TextView getEditHeight() {
        return editHeight;
    }

    public TextView getEditMaterial() {
        return editMaterial;
    }

    public TextView getEditDesigner() {
        return editDesigner;
    }

    public TextView getEditHolder() {
        return editHolder;
    }

    public TextView getEditMaintainCompany() {
        return editMaintainCompany;
    }

    public TextView getEditContent() {
        return editContent;
    }
}
