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
import com.xia.adgis.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ADsCompanyFragment extends Fragment {

    @BindView(R.id.ads_designer)
    TextView designer;
    @BindView(R.id.ads_holder)
    TextView holder;
    ADsDetailActivity aDsDetailActivity;
    String title;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_ads_company, container, false);
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
        BmobQuery<ADCompany> adCompanyBmobQuery = new BmobQuery<>();
        adCompanyBmobQuery.addWhereEqualTo("name",title);
        adCompanyBmobQuery.findObjects(new FindListener<ADCompany>() {
            @Override
            public void done(List<ADCompany> list, BmobException e) {
                if(e == null){
                    designer.setText(list.get(list.size()-1).getDesigner());
                    holder.setText(list.get(list.size()-1).getHoder());
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
