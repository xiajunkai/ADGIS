package com.xia.adgis.Admin.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xia.adgis.Admin.Adapter.ManageADsAdapter;
import com.xia.adgis.Admin.DividerItemDecoration;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ManageADsActivity extends AppCompatActivity implements View.OnClickListener, ManageADsAdapter.OnItemClickListener {

    private static final int MODE_CHECK = 0;
    private static final int MODE_EDIT = 1;

    @BindView(R.id.manage_toolbar)
    Toolbar toolbar;
    @BindView(R.id.manage_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.select_num)
    TextView mSelectNum;
    @BindView(R.id.manage_delete)
    Button mDelete;
    @BindView(R.id.select_all)
    TextView mSelectAll;
    @BindView(R.id.manage_bottom_dialog)
    LinearLayout mBottomDialog;
    @BindView(R.id.manage_editor)
    TextView mEditor;
    @BindView(R.id.manage_title)
    TextView mTitle;

    //
    private ManageADsAdapter adapter = null;
    private List<AD> mList = new ArrayList<>();
    private int mEditMode = MODE_CHECK;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mTitle.setText("管理广告");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
            }
        });
        initData();
        initListener();
    }

    private void initData(){
        //获取服务器数据
        BmobQuery<AD> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if(e == null){
                    mList = list;
                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ManageADsActivity.this);
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                    DividerItemDecoration itemDecorationHeader = new DividerItemDecoration(ManageADsActivity.this, DividerItemDecoration.VERTICAL_LIST);
                    itemDecorationHeader.setDividerDrawable(ContextCompat.getDrawable(ManageADsActivity.this, R.drawable.divider_main_bg_height_1));
                    mRecyclerView.addItemDecoration(itemDecorationHeader);
                    adapter = new ManageADsAdapter(mList);
                    mRecyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(ManageADsActivity.this);
                }else {
                    Toast.makeText(ManageADsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 根据选择的数量是否为0来判断按钮的是否可点击.
     */
    private void setBtnBackground(int size) {
        if (size != 0) {
            mDelete.setBackgroundResource(R.drawable.button_shape);
            mDelete.setEnabled(true);
            mDelete.setTextColor(Color.WHITE);
        } else {
            mDelete.setBackgroundResource(R.drawable.button_noclickable_shape);
            mDelete.setEnabled(false);
            mDelete.setTextColor(ContextCompat.getColor(this, R.color.color_b7b8bd));
        }
    }

    private void initListener() {
        mDelete.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);
        mEditor.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.manage_delete:
                delete();
                break;
            case R.id.select_all:
                selectAll();
                break;
            case R.id.manage_editor:
                updataEditMode();
                break;
            default:
                break;
        }
    }

    //全选与反选
    private void selectAll(){
        if (adapter == null){
            return;
        }
        if(!isSelectAll) {
            for (int i = 0, j = adapter.getList().size(); i < j; i++){
                adapter.getList().get(i).setSelect(true);
            }
            index = adapter.getList().size();
            mDelete.setEnabled(true);
            mSelectAll.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = adapter.getList().size(); i < j; i++) {
                adapter.getList().get(i).setSelect(false);
            }
            index = 0;
            mDelete.setEnabled(false);
            mSelectAll.setText("全选");
            isSelectAll = false;
        }
        adapter.notifyDataSetChanged();
        setBtnBackground(index);
        mSelectNum.setText(String.valueOf(index));
    }

    //删除逻辑
    private void delete(){
        if(index == 0){
            mDelete.setEnabled(false);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        if(index == 1){
            builder.setMessage("删除后不可恢复，是否删除该条目？");
        }else {
            builder.setMessage("删除后不可恢复，是否删除这" + index + "个条目？");
        }
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = adapter.getList().size(), j = 0 ; i > j; i--) {
                    AD ad = adapter.getList().get(i - 1);
                    if (ad.isSelect()) {
                        adapter.getList().remove(ad);
                        //删除逻辑
                        index--;
                    }
                }
                index = 0;
                mSelectNum.setText(String.valueOf(0));
                setBtnBackground(index);
                if (adapter.getList().size() == 0){
                    mBottomDialog.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    private void updataEditMode() {
        mEditMode = mEditMode == MODE_CHECK ? MODE_EDIT : MODE_CHECK;
        if (mEditMode == MODE_EDIT) {
            mEditor.setText("取消");
            mBottomDialog.setVisibility(View.VISIBLE);
            editorStatus = true;
        } else {
            mEditor.setText("编辑");
            mBottomDialog.setVisibility(View.GONE);
            editorStatus = false;
            clearAll();
        }
        adapter.setEditMode(mEditMode);
    }

    private void clearAll() {
        mSelectNum.setText(String.valueOf(0));
        isSelectAll = false;
        mSelectAll.setText("全选");
        setBtnBackground(0);
    }

    @Override
    public void onItemClickListener(int pos, List<AD> myADList) {
        if (editorStatus) {
            AD ad = myADList.get(pos);
            boolean isSelect = ad.isSelect();
            if (!isSelect) {
                index++;
                ad.setSelect(true);
                if (index == myADList.size()) {
                    isSelectAll = true;
                    mSelectAll.setText("取消全选");
                }
            } else {
                ad.setSelect(false);
                index--;
                isSelectAll = false;
                mSelectAll.setText("全选");
            }
            setBtnBackground(index);
            mSelectNum.setText(String.valueOf(index));
            adapter.notifyDataSetChanged();
        }
    }
}
