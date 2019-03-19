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
import com.xia.adgis.Main.Bean.ADCompany;
import com.xia.adgis.Main.Bean.ADmaintain;
import com.xia.adgis.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ADsMaintainFragment extends Fragment {

    @BindView(R.id.ads_maintain_company)
    TextView company;
    @BindView(R.id.ads_maintain_context)
    TextView context;
    @BindView(R.id.ads_maintain_time)
    TextView time;
    ADsDetailActivity aDsDetailActivity;
    String title;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_ads_maintain, container, false);
        ButterKnife.bind(this,mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        aDsDetailActivity = (ADsDetailActivity) getActivity();
        title = aDsDetailActivity.getAdsName();
        refresh();
    }

    private void refresh(){
        BmobQuery<ADmaintain> aDmaintainBmobQuery = new BmobQuery<>();
        aDmaintainBmobQuery.addWhereEqualTo("name",title);
        aDmaintainBmobQuery.findObjects(new FindListener<ADmaintain>() {
            @Override
            public void done(List<ADmaintain> list, BmobException e) {
                if(e == null){
                    company.setText(list.get(list.size()-1).getCompany());
                    context.setText(list.get(list.size()-1).getContext());
                    time.setText(list.get(list.size()-1).getTime());
                }else {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        String s = aDsDetailActivity.getIsEdit();
        if(s.equals("success")){
            refresh();
        }
    }
}
