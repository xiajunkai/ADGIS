package com.xia.adgis.Main.Adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xia.adgis.Main.Activity.ADsDetailActivity;
import com.xia.adgis.Main.Activity.AllADsActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.R;

import java.util.List;
@SuppressWarnings("unchecked")
public class ADsAdapter extends RecyclerView.Adapter<ADsAdapter.ViewHolder>{

    private Context mContext;

    private List<AD> mADList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView adImage;
        TextView adName;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            adImage = (ImageView) view.findViewById(R.id.all_ads_image);
            adName = (TextView) view.findViewById(R.id.all_ads_name);
        }
    }

    public ADsAdapter(List<AD> adList) {
        mADList = adList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_all_ads, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ADsDetailActivity.class);
                int position = holder.getAdapterPosition();
                intent.putExtra("data", mADList.get(position).getName());

                ActivityCompat.startActivityForResult((AppCompatActivity)mContext,intent, AllADsActivity.EDIT_ADS,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (AppCompatActivity)mContext,
                                new Pair<>(v, "detail_image"))
                                .toBundle());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AD ad = mADList.get(position);
        holder.adName.setText(ad.getName());
        Glide.with(mContext).load(ad.getImageID()).thumbnail(0.1f)
                .into(holder.adImage);
    }

    @Override
    public int getItemCount() {
        return mADList.size();
    }

}
