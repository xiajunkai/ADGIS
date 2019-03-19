package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
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
import com.kevin.crop.UCrop;
import com.xia.adgis.CropActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Main.Bean.ADCompany;
import com.xia.adgis.Main.Bean.ADmaintain;
import com.xia.adgis.Main.Bean.ADphysical;
import com.xia.adgis.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AddAdsActivity extends AppCompatActivity implements View.OnClickListener, PopupWindow.OnDismissListener {

    @BindView(R.id.add_ads_toolbar)
    Toolbar addToolbar;
    @BindView(R.id.add_ads_edit_photo)
    RelativeLayout addEditPhoto;
    @BindView(R.id.add_ads_image)
    ImageView addImage;
    @BindView(R.id.add_ads_name)
    EditText addName;
    @BindView(R.id.add_ads_brief)
    TextView addBrief;
    @BindView(R.id.add_ads_location)
    LinearLayout addLocation;
    //经度
    @BindView(R.id.add_ads_longitude)
    TextView addLongitude;
    //纬度
    @BindView(R.id.add_ads_latitude)
    TextView addLatitude;
    //加载对话框
    ProgressDialog loading;
    /**
    图像部分
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
    View view;
    //图片路径
    String imagePath = "unEdit";
    /**
     * 输入简介部分
     */
    //输入简介中的输入框
    EditText briefEdit;
    /**
     *选择地址部分
     */
    private static final int LOCATION_REQUEST = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ads);
        ButterKnife.bind(this);
        //注册点击监听
        addEditPhoto.setOnClickListener(this);
        addBrief.setOnClickListener(this);
        addLocation.setOnClickListener(this);
        //得到文件存储路径
        String filename = "cropImage.jpeg";
        //目标路径
        mDestinationUri = Uri.fromFile(new File(getCacheDir(),filename));
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mDestinationUri));
        //拍照临时照片
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        //初始化工具栏
        initToolbar();
        //初始化加载对话框
        initProgressDialog();
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_ads_edit_photo:
                SelectIcon();
                break;
            case R.id.add_ads_brief:
                InputBrief();
                break;
            case R.id.add_ads_location:
                ConfirmLocation();
                break;
        }
    }

    //初始化工具栏
    private void initToolbar(){
        addToolbar.setTitle("添加广告牌");
        setSupportActionBar(addToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化加载对话框
    private void initProgressDialog(){
        loading = new ProgressDialog(this);
        loading.setMessage("加载中...");
        loading.setCancelable(true);
    }

    //头像选择
    private void SelectIcon(){

        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        view = LayoutInflater.from(this).inflate(R.layout.edit_icon_popwindow,null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:   // 调用相机拍照
                    //考虑兼容性
                    Uri imageUri; //照片文件临时存储路径
                    File temp = new File(mTempPhotoPath);
                    if(Build.VERSION.SDK_INT < 24){
                        imageUri = Uri.fromFile(temp);
                    }else {
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
                case LOCATION_REQUEST:      //传递位置信息
                    LatLng latLng = data.getParcelableExtra("location_info");
                    addLatitude.setText(String.valueOf(latLng.latitude));
                    addLongitude.setText(String.valueOf(latLng.longitude));
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
                addImage.setImageBitmap(bitmap);
                String filePath = resultUri.getEncodedPath();
                //获得了图片路径
                imagePath = Uri.decode (filePath);
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

    //输入广告牌简介
    private void InputBrief(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.input_brief, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("简介");
        builder.setView(mView);
        briefEdit = (EditText) mView.findViewById(R.id.input_brief_edit);
        //先判断主界面中TextView是否存在内容
        if(! addBrief.getText().equals("")){
            briefEdit.setText(addBrief.getText());
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addBrief.setText(briefEdit.getText());
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //输入广告牌位置
    private void ConfirmLocation(){
        startActivityForResult(new Intent(AddAdsActivity.this, ConfirmLocationActivity.class),LOCATION_REQUEST);
        overridePendingTransition(R.anim.in,R.anim.out);
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
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
                break;
            case R.id.ads_save:
                SaveADs();
                break;
        }
        return true;
    }

    private void SaveADs(){
        String name = addName.getText().toString();
        String brief = addBrief.getText().toString();
        double latitude;
        double longitude;
        if (imagePath.equals("unEdit")){
            Toast.makeText(this, "请选择图像！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "广告牌名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(brief)){
            Toast.makeText(this, "广告牌简介不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(addLatitude.getText().toString()) || TextUtils.isEmpty(addLongitude.getText().toString())){
            Toast.makeText(this, "经纬度不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else {
            latitude = Double.valueOf(addLatitude.getText().toString());
            longitude = Double.valueOf(addLongitude.getText().toString());
        }
        loading.setMessage("添加中...");
        loading.show();
        //添加信息
        SaveADsInfo(name, brief, latitude,longitude);
    }

    private void SaveADsInfo(final String name, final String brief, final double latitude, final double longitude){
        final BmobFile adsImage = new BmobFile(new File(imagePath));
        adsImage.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    //添加AD
                    AD ad = new AD();
                    ad.setName(name);
                    ad.setBrief(brief);
                    ad.setImageID(adsImage.getFileUrl());
                    ad.setLatitude(latitude);
                    ad.setLongitude(longitude);
                    ad.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                //添加物理信息
                                ADphysical adPhysical = new ADphysical();
                                adPhysical.setName(name);
                                adPhysical.setLength("0");
                                adPhysical.setHeight("0");
                                adPhysical.setWidth("0");
                                adPhysical.setMaterial("无");
                                adPhysical.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        //添加公司信息
                                        if (e == null) {
                                            ADCompany adCompany = new ADCompany();
                                            adCompany.setName(name);
                                            adCompany.setHoder("无");
                                            adCompany.setDesigner("无");
                                            adCompany.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e == null) {
                                                        //添加维护信息
                                                        ADmaintain adMaintain = new ADmaintain();
                                                        adMaintain.setName(name);
                                                        adMaintain.setCompany("无");
                                                        adMaintain.setContext("无");
                                                        adMaintain.setTime("2019-01-01");
                                                        adMaintain.save(new SaveListener<String>() {
                                                            @Override
                                                            public void done(String s, BmobException e) {
                                                                loading.dismiss();
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(AddAdsActivity.this, "上传维护信息失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        loading.dismiss();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(AddAdsActivity.this, "上传公司信息失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            loading.dismiss();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(AddAdsActivity.this, "上传物理信息失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
                }else {
                    Toast.makeText(AddAdsActivity.this, "上传图片失败", Toast.LENGTH_SHORT).show();
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
}
