package com.xia.adgis.Main.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.example.swipeback.ISwipeBackActivity;
import com.example.swipeback.SwipeBackActivityImpl;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xia.adgis.App;
import com.xia.adgis.Utils.MySearchView.Adapter.AutoCompleteAdapter;
import com.xia.adgis.Utils.MySearchView.Adapter.SearchAdapter;
import com.xia.adgis.Utils.MySearchView.Adapter.UserNameHistoryAdapter;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Utils.MySearchView.Bean.SearchItem;
import com.xia.adgis.Main.DataBase.HistorySqliteHelpter;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.Utils.MySearchView.SearchView;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import rx.Subscriber;

@SuppressWarnings("unchecked")
public class SearchActivity extends SwipeBackActivityImpl implements SearchView.SearchViewListener {

    /**
     * 滑动切换效果
     */
    SmartRefreshLayout smartRefreshLayout;

    /**
     * 搜索结果列表view
     */
    private ListView lvResults;

    /**
     * 搜索view
     */
    private SearchView searchView;

    /**
     * 热搜框列表adapter
     */
    private UserNameHistoryAdapter hintAdapter;

    /**
     * 自动补全列表adapter
     */
    private AutoCompleteAdapter autoCompleteAdapter;

    /**
     * 搜索结果列表adapter
     */
    private SearchAdapter resultAdapter;

    private List<SearchItem> dbData;

    /**
     * 搜索过程中自动补全数据
     */
    private List<String> autoCompleteData;

    /**
     * 搜索结果的数据
     */
    private List<SearchItem> resultData;

    /**
     * 默认提示框显示项的个数
     */
    private static int DEFAULT_HINT_SIZE = 4;

    /**
     * 提示框显示项的个数
     */
    private static int hintSize = DEFAULT_HINT_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        StatusBarUtil.immersive(this, true);
        initData();
        initViews();
        SearchView.isShow = false;
        StatusBarUtil.setPaddingSmart(this,searchView);
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        lvResults = (ListView) findViewById(R.id.main_lv_search_results);
        searchView = (SearchView) findViewById(R.id.main_search_layout);
        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        smartRefreshLayout.setEnableAutoLoadMore(false);
        smartRefreshLayout.setEnableRefresh(false);
        //设置监听
        searchView.setSearchViewListener(this);
        //设置adapter
        searchView.setTipsHintAdapter(hintAdapter);
        searchView.setAutoCompleteAdapter(autoCompleteAdapter);
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LatLng latLng = new LatLng(resultData.get(position).latitude,resultData.get(position).longitude);
                Intent intent = new Intent();
                intent.putExtra("search_return",latLng);
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //从数据库获取数据
        getDbData();
        //初始化搜索历史数据
        getSearchHistoryData();
        //初始化自动补全数据
        getAutoCompleteData(null);
        //初始化搜索结果数据
        getResultData(null);
    }

    /**
     * 获取db 数据(若先存在缓存，则从缓存中抓取)
     */
    private void getDbData() {
        //
        //使用Bmob获取用来标记的信息
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        adBmobQuery.findObjectsObservable(AD.class)
                .subscribe(new Subscriber<List<AD>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(SearchActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<AD> ads) {
                        dbData = new ArrayList<>(ads.size());
                        for (int i = 0; i < ads.size(); i++) {
                            SearchItem temp = new SearchItem();
                            temp.title = ads.get(i).getName();
                            temp.url = ads.get(i).getImageID();
                            temp.content = ads.get(i).getBrief();
                            temp.width = 1400;
                            temp.height = 1400;
                            temp.latitude = ads.get(i).getLatitude();
                            temp.longitude = ads.get(i).getLongitude();
                            dbData.add(temp);
                        }
                    }
                });
    }

    /**
     * 获取搜索历史
     * 获取热搜版data 和adapter
     */
    private void getSearchHistoryData() {
        /*
      热搜版数据
     */
        List<String> hintData = new ArrayList<>(hintSize);
        HistorySqliteHelpter helpter = new HistorySqliteHelpter(SearchActivity.this);
        SQLiteDatabase database = helpter.getWritableDatabase();
        try {
            String sql = "select * from history";
            Cursor cursor = database.rawQuery(sql,null);
            while (cursor.moveToNext()){
                hintData.add(cursor.getString(cursor.getColumnIndex("name")));
            }
            cursor.close();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage() , Toast.LENGTH_SHORT).show();
        }
        hintAdapter = new UserNameHistoryAdapter(this, R.layout.search_history_item, hintData);
    }

    /**
     * 获取自动补全data 和adapter
     */
    private void getAutoCompleteData(String text) {
        if (autoCompleteData == null) {
            //初始化
            autoCompleteData = new ArrayList<>(hintSize);
        } else {
            // 根据text 获取auto data
            autoCompleteData.clear();
            for (int i = 0, count = 0; i < dbData.size()
                    && count < hintSize; i++) {
                if (dbData.get(i).title.contains(text.trim())) {
                    autoCompleteData.add(dbData.get(i).title);
                    count++;
                }
            }
        }
        if (autoCompleteAdapter == null) {
            autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.search_common_item, autoCompleteData);
        } else {
            autoCompleteAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取搜索结果data和adapter
     */
    private void getResultData(String text) {
        if (resultData == null) {
            // 初始化
            resultData = new ArrayList<>();
        } else {
            resultData.clear();
            for (int i = 0; i < dbData.size(); i++) {
                if (dbData.get(i).title.contains(text.trim())) {
                    resultData.add(dbData.get(i));
                }
            }
        }
        if (resultAdapter == null) {
            resultAdapter = new SearchAdapter(this, resultData, R.layout.search_item);
        } else {
            resultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 当搜索框 文本改变时 触发的回调 ,更新自动补全数据
     */
    @Override
    public void onRefreshAutoComplete(String text) {
        //更新数据
        getAutoCompleteData(text);
    }

    /**
     * 点击搜索键时edit text触发的回调
     *
     */
    @Override
    public void onSearch(String text) {
        //更新result数据
        getResultData(text);
        lvResults.setVisibility(View.VISIBLE);
        //第一次获取结果 还未配置适配器
        if (lvResults.getAdapter() == null) {
            //获取搜索数据 设置适配器
            lvResults.setAdapter(resultAdapter);
        } else {
            //更新搜索数据
            resultAdapter.notifyDataSetChanged();
        }
        Toast.makeText(this, searchView.etInput.getText(), Toast.LENGTH_SHORT).show();
        //加入历史记录
        HistorySqliteHelpter helpter = new HistorySqliteHelpter(SearchActivity.this);
        SQLiteDatabase database = helpter.getWritableDatabase();
        try {
            String sql = "insert into history (name) values(?)";
            database.execSQL(sql,new String[]{searchView.etInput.getText().toString()});
        }catch (Exception e){
            //Toast.makeText(MainActivity.this,"加入失败",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public ISwipeBackActivity getPreActivity() {
        return (ISwipeBackActivity) App.getInstance().getStack().getBackActivity();
    }

    public ListView getLvResults() {
        return lvResults;
    }

    public SearchView getSearchView() {
        return searchView;
    }
}
