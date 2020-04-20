package com.taodongdong.ecommerce;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.*;

import com.taodongdong.ecommerce.api.ApiCallback;
import com.taodongdong.ecommerce.api.Page;
import com.taodongdong.ecommerce.api.ProductInfo;
import com.taodongdong.ecommerce.prouctlistview.ProductItem;
import com.taodongdong.ecommerce.prouctlistview.ProductListAdapter;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AbstractActivity implements View.OnClickListener {
    private ViewPager mViewpager;

    //声明四个ImageButton
    private ImageButton shopImg;
    private ImageButton myshopImg;
    private ImageButton usrImg;
    private SearchView searchView;
    private ListView productList;
    private ListView myshopList;
    private ProductListAdapter plAdapter;
    private ProductListAdapter myshopAdapter;

    //声明ViewPager的适配器
    private PagerAdapter mAdpater;
    //Tab的List
    private List<View> mTabs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉TitleBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_pager);
        initViews();//初始化控件
        initDatas();//初始化数据
        initEvents();//初始化事件


        //test/////////////////////////////////////
        Resources res = this.getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
        ProductItem pi = new ProductItem("test",bmp);
        List<ProductItem> list = new ArrayList<ProductItem>();
        for(int i = 0; i < 100;i++){
            list.add(new ProductItem("test",bmp));
        }

        //test//////////////////////////////////////



    }

    private void initEvents() {
        //设置Tab的点击事件
        shopImg.setOnClickListener(this);
        myshopImg.setOnClickListener(this);
        usrImg.setOnClickListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                HomeActivity.this.api().searchProducts(query, 1, 10, new ApiCallback<Page<ProductInfo>>() {
                    @Override
                    public void onSuccess(Page<ProductInfo> data) throws JSONException {
                        HomeActivity.this.plAdapter.clear();
                        ProductItem.Factory.convertFromProductInfo(Arrays.asList(data.data), HomeActivity.this.plAdapter);
                        plAdapter.notifyDataSetChanged();
                        //TODO 处理搜索成功
                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        Toast.makeText(HomeActivity.this,"搜索失败：" + message,Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        //添加ViewPager的切换Tab的监听事件
        mViewpager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentItem = mViewpager.getCurrentItem();

                resetImgs();
                switch (currentItem) {
                    //TODO 添加图标
                    case 0:
                        shopImg.setImageResource(R.mipmap.ic_launcher);
                        break;
                    case 1:
                        myshopImg.setImageResource(R.mipmap.ic_launcher);
                        break;
                    case 2:
                        usrImg.setImageResource(R.mipmap.ic_launcher);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initDatas() {
        //初始化ViewPager的适配器
        mAdpater = new PagerAdapter() {
            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mTabs.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mTabs.get(position));
            }
        };
            Log.e("init", "initData: null adpter " + mAdpater.getCount());
        //设置ViewPager的适配器
        mViewpager.setAdapter(mAdpater);

        plAdapter = new ProductListAdapter(this);
        productList.setAdapter(plAdapter);

        myshopAdapter = new ProductListAdapter(this);
        myshopList.setAdapter(myshopAdapter);
    }

    //初始化控件
    private void initViews() {
        mViewpager = (ViewPager) findViewById(R.id.id_viewpager);
        shopImg = (ImageButton) findViewById(R.id.id_tab_shop);
        myshopImg = (ImageButton) findViewById(R.id.id_tab_myshop);
        usrImg = (ImageButton) findViewById(R.id.id_tab_usr);

        //获取到四个Tab
        LayoutInflater inflater = LayoutInflater.from(this);
        View tab1 = inflater.inflate(R.layout.shop, null);
        View tab2 = inflater.inflate(R.layout.myshop, null);
        View tab3 = inflater.inflate(R.layout.usr, null);
        searchView = (SearchView)tab1.findViewById(R.id.search_products);
        productList = (ListView)tab1.findViewById(R.id.id_shop_list);
        myshopList = (ListView)tab2.findViewById(R.id.id_myshop_list);
        if(productList == null){
            Log.e("init", "initViews: null listview");
        }

        //将四个Tab添加到集合中
        mTabs.add(tab1);
        mTabs.add(tab2);
        mTabs.add(tab3);

    }

    @Override
    public void onClick(View v) {
        resetImgs();
        switch (v.getId()) {
            //TODO 添加图标
            case R.id.id_tab_shop:
                Toast.makeText(this," tab 1",Toast.LENGTH_SHORT).show();
                //设置viewPager的当前Tab
                mViewpager.setCurrentItem(0);
                shopImg.setImageResource(R.mipmap.ic_launcher);

                //TODO 更新商品列表
                break;
            case R.id.id_tab_myshop:
                Toast.makeText(this," tab 2",Toast.LENGTH_SHORT).show();
                mViewpager.setCurrentItem(1);
                myshopImg.setImageResource(R.mipmap.ic_launcher);
                //TODO 更新自己的商品列表
                break;
            case R.id.id_tab_usr:
                Toast.makeText(this," tab 3",Toast.LENGTH_SHORT).show();
                mViewpager.setCurrentItem(2);
                usrImg.setImageResource(R.mipmap.ic_launcher);
                break;
        }
    }

    //将四个ImageButton设置成灰色
    private void resetImgs () {
        //TODO 添加图标
        shopImg.setImageResource(R.mipmap.ic_launcher);
        myshopImg.setImageResource(R.mipmap.ic_launcher);
        usrImg.setImageResource(R.mipmap.ic_launcher);

    }
}
