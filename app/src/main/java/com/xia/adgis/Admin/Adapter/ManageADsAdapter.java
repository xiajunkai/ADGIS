package com.xia.adgis.Admin.Adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xia.adgis.Main.Activity.ADsDetailActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageADsAdapter extends RecyclerView.Adapter<ManageADsAdapter.ViewHolder> {

    private static final int MODE_CHECK = 0;
    private int mEditMode = MODE_CHECK;

    private List<AD> mADList;
    private OnItemClickListener mOnItemClickListener;

    private Context mContext;

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
                //mContext.startActivity(intent);
                ActivityCompat.startActivity(mContext, intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (AppCompatActivity) mContext,
                                new Pair<>(v, "detail_image"))
                                .toBundle());
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
}
