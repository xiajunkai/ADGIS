package com.xia.adgis.Main.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class AboutMeActivity extends AppCompatActivity {

    @BindView(R.id.test_delete)
    Button a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<Messages> bmobQuery = new BmobQuery<>();
                bmobQuery.addWhereEqualTo("adName","昆明理工大学");
                bmobQuery.findObjects(new FindListener<Messages>() {
                    @Override
                    public void done(List<Messages> list, BmobException e) {
                        deleteAll(list);
                        //List<BmobObject> objects = transform(list);
                        //new BmobBatch().deleteBatch(objects);
                        //Toast.makeText(AboutMeActivity.this, ""+list.get(list.size()-1).getUserName(), Toast.LENGTH_SHORT).show();
                    }
                });
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

                }
            });
        }
    }
    /*private List<BmobObject> transform(List<Messages> lists){
        List<BmobObject> objects = new ArrayList<>();
        for (int i = 0; i < lists.size() - 1; i ++){
            BmobObject object = new BmobObject();
            object.setObjectId(lists.get(i).getObjectId());
            objects.add(object);
        }
        return objects;
    }*/
}
