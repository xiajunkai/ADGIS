package com.xia.adgis.Main.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xia.adgis.Main.Activity.ADsDetailActivity;
import com.xia.adgis.Main.Bean.ADphysical;
import com.xia.adgis.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ADsPhysicalFragment extends Fragment {

    @BindView(R.id.ads_length)
    TextView length;
    @BindView(R.id.ads_width)
    TextView width;
    @BindView(R.id.ads_height)
    TextView height;
    @BindView(R.id.ads_material)
    TextView material;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_ads_physical, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ADsDetailActivity aDsDetailActivity = (ADsDetailActivity) getActivity();
        String title = aDsDetailActivity.getAdsName();
        BmobQuery<ADphysical> aDphysicalBmobQuery = new BmobQuery<>();
        aDphysicalBmobQuery.addWhereEqualTo("name", title);
        aDphysicalBmobQuery.findObjects(new FindListener<ADphysical>() {
            @Override
            public void done(List<ADphysical> list, BmobException e) {
                if(e == null) {
                    length.setText(list.get(list.size() - 1).getLength());
                    width.setText(list.get(list.size() - 1).getWidth());
                    height.setText(list.get(list.size() - 1).getHeight());
                    material.setText(list.get(list.size() - 1).getMaterial());
                }else {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
