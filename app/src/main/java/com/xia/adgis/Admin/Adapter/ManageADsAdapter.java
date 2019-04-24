package com.xia.adgis.Admin.Adapter;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xia.adgis.Main.Activity.ADsDetailActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Main.Bean.ADCompany;
import com.xia.adgis.Main.Bean.ADmaintain;
import com.xia.adgis.Main.Bean.ADphysical;
import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManageADsAdapter extends RecyclerView.Adapter<ManageADsAdapter.ViewHolder> {

    private static final int MODE_CHECK = 0;
    private int mEditMode = MODE_CHECK;

    private List<AD> mADList;
    private OnItemClickListener mOnItemClickListener;

    private Context mContext;

    private ProgressDialog loading = null;

    //删除对应变量
    //objectID
    private String objectID_AD;
    //删除的图像
    private BmobFile deleteImg;
    //删除留言标志
    private boolean flag;

    public ManageADsAdapter(List<AD> lists){
        mADList = lists;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.manage_img)
        ImageView mADImg;
        @BindView(R.id.manage_title)
        TextView mADTitle;
        @BindView(R.id.manage_source)
        TextView mADbrief;
        @BindView(R.id.root_view)
        RelativeLayout mRootView;
        @BindView(R.id.check_box)
        ImageView mCheckBox;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_manage, parent, false);
        return new ManageADsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AD ad = mADList.get(holder.getAdapterPosition());
        holder.mADTitle.setText(ad.getName());
        holder.mADbrief.setText(ad.getBrief());
        Glide.with(mContext).load(ad.getImageID()).thumbnail(0.5f)
                .into(holder.mADImg);
        if (mEditMode == MODE_CHECK) {
            holder.mCheckBox.setVisibility(View.GONE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            if (ad.isSelect()) {
                holder.mCheckBox.setImageResource(R.drawable.ic_checked);
            } else {
                holder.mCheckBox.setImageResource(R.drawable.ic_uncheck);
            }
        }
        //点击逻辑
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), mADList);
            }
        });
        holder.mADImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ADsDetailActivity.class);
                intent.putExtra("data",ad.getName());
                ActivityCompat.startActivity(mContext, intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (AppCompatActivity) mContext,
                                new Pair<>(v, "detail_image"))
                                .toBundle());
            }
        });
        holder.mRootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loading = new ProgressDialog(mContext);
                loading.setMessage("删除中...");
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(mContext);
                deleteDialog.setTitle("删除节点");
                deleteDialog.setMessage("确定删除“" + ad.getName() + "”吗？\n警告，删除后不可恢复！！！");
                deleteDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loading.show();
                        deleteADs(ad.getName(),ad);
                    }
                });
                deleteDialog.setNegativeButton("取消", null);
                deleteDialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mADList.size();
    }

    public List<AD> getList() {
        if (mADList == null) {
            mADList = new ArrayList<>();
        }
        return mADList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClickListener(int pos,List<AD> myADList);
    }
    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

    private void deleteADs(final String adName, final AD temp){
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        adBmobQuery.addWhereEqualTo("name",adName);
        adBmobQuery.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if (e == null) {
                    //获取删除的ID
                    objectID_AD = list.get(list.size() - 1).getObjectId();
                    //删除广告牌图片
                    deleteImg = new BmobFile();
                    deleteImg.setUrl(list.get(list.size() - 1).getImageID());
                    deleteImg.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                //删除广告牌对象
                                AD ad = new AD();
                                ad.setObjectId(objectID_AD);
                                ad.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
                                            mADList.remove(temp);
                                            notifyDataSetChanged();
                                            deletePhysical(adName);
                                        }else {
                                            Toast.makeText(mContext, "删除广告牌失败！", Toast.LENGTH_SHORT).show();
                                            loading.dismiss();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(mContext, "删除图像失败！", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
                }else {
                    Toast.makeText(mContext,"当前广告牌不存在！", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }

    //删除物理信息
    private void deletePhysical(final String adName){
        BmobQuery<ADphysical> physical = new BmobQuery<>();
        physical.addWhereEqualTo("name", adName);
        physical.findObjects(new FindListener<ADphysical>() {
            @Override
            public void done(List<ADphysical> list, BmobException e) {
                if (e == null){
                    ADphysical ad = new ADphysical();
                    ad.setObjectId(list.get(list.size() - 1).getObjectId());
                    ad.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                deleteCompany(adName);
                            }else {
                                Toast.makeText(mContext, "删除物理信息失败！", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
                }else {
                    Toast.makeText(mContext, "查找物理信息失败！", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }

    //删除公司信息
    private void deleteCompany(final String adName){
        BmobQuery<ADCompany> adcompany = new BmobQuery<>();
        adcompany.addWhereEqualTo("name", adName);
        adcompany.findObjects(new FindListener<ADCompany>() {
            @Override
            public void done(List<ADCompany> list, BmobException e) {
                if (e == null) {
                    ADCompany ad = new ADCompany();
                    ad.setObjectId(list.get(list.size() - 1).getObjectId());
                    ad.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                deleteMaintain(adName);
                            }else {
                                Toast.makeText(mContext, "删除公司失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(mContext, "查找公司失败", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }

    //删除维护信息
    private void deleteMaintain(final String adName){
        BmobQuery<ADmaintain> admaintain = new BmobQuery<>();
        admaintain.addWhereEqualTo("name", adName);
        admaintain.findObjects(new FindListener<ADmaintain>() {
            @Override
            public void done(List<ADmaintain> list, BmobException e) {
                if (e == null){
                    ADmaintain ad = new ADmaintain();
                    ad.setObjectId(list.get(list.size() - 1).getObjectId());
                    ad.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                deleteMessage(adName);
                            }else {
                                Toast.makeText(mContext, "删除维护信息失败！", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
                }else {
                    Toast.makeText(mContext, "查找维护信息失败！", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }

    //删除留言
    private void deleteMessage(String adName){
        BmobQuery<Messages> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("adName",adName);
        bmobQuery.findObjects(new FindListener<Messages>() {
            @Override
            public void done(List<Messages> list, BmobException e) {
                if (e == null){
                    if (list.size() == 0){
                        loading.dismiss();
                    }else {
                        deleteAll(list);
                    }
                }else {
                    Toast.makeText(mContext, "查找留言信息失败！", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }

    private void deleteAll(final List<Messages> lists){
        for (int i = 0; i < lists.size(); i ++){
            Messages message = new Messages();
            message.setObjectId(lists.get(i).getObjectId());
            message.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null){
                        flag = false;
                    }
                }
            });
            //其中一项删除失败，立即退出
            if (!flag){
                loading.dismiss();
                break;
            }
            //删除完成后，全部退出
            if (i == (lists.size() - 1)){
                loading.dismiss();
            }
        }
    }
}
