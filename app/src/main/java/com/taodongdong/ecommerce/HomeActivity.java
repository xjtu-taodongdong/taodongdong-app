package com.taodongdong.ecommerce;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;


import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.*;

import com.taodongdong.ecommerce.api.ApiCallback;
import com.taodongdong.ecommerce.api.Page;
import com.taodongdong.ecommerce.api.ProductInfo;
import com.taodongdong.ecommerce.api.StoreInfo;
import com.taodongdong.ecommerce.api.Errors;
import com.taodongdong.ecommerce.api.UserInfo;
import com.taodongdong.ecommerce.prouctlistview.ProductItem;
import com.taodongdong.ecommerce.prouctlistview.ProductListAdapter;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AbstractActivity implements View.OnClickListener {
    private ViewPager mViewpager;

    //声明ImageButton
    private ImageButton shopImg;
    private ImageButton myshopImg;
    private ImageButton usrImg;

    private SearchView searchView;
    private ListView productList;
    private ListView myshopList;
    private ProductListAdapter plAdapter;
    private ProductListAdapter myshopAdapter;

    private Button put_on_sale;

    private View userInfoPage;

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



    }

    private void initEvents() {
        //设置Tab的点击事件
        shopImg.setOnClickListener(this);
        myshopImg.setOnClickListener(this);
        usrImg.setOnClickListener(this);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                api().showToast("query" + query);

                HomeActivity.this.api().searchProducts(query, 1, 10, new ApiCallback<Page<ProductInfo>>() {
                    @Override
                    public void onSuccess(Page<ProductInfo> data) throws JSONException {
                        HomeActivity.this.plAdapter.clear();
                        ProductItem.Factory.convertFromProductInfo(Arrays.asList(data.data), HomeActivity.this.plAdapter);
                        plAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        api().showToast("搜索失败");
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int ID = HomeActivity.this.plAdapter.getItem(position).getID();
                Bundle b = new Bundle();
                b.putInt("id",ID);
                Intent intent = new Intent();
                intent.putExtras(b);
                intent.setClass(HomeActivity.this,ProductDetails.class);
                startActivity(intent);

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
                        HomeActivity.this.refreshUserInfo();
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
        put_on_sale = (Button) findViewById(R.id.put_on_sale);

        //获取到三个Tab
        LayoutInflater inflater = LayoutInflater.from(this);
        View tab1 = inflater.inflate(R.layout.shop, null);
        View tab2 = inflater.inflate(R.layout.myshop, null);
        View tab3 = inflater.inflate(R.layout.usr, null);
        searchView = (SearchView)tab1.findViewById(R.id.search_products);
        productList = (ListView)tab1.findViewById(R.id.id_shop_list);
        myshopList = (ListView)tab2.findViewById(R.id.id_myshop_list);
        userInfoPage = tab3;
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
                api().showToast(" tab 1");
                //设置viewPager的当前Tab
                mViewpager.setCurrentItem(0);
                shopImg.setImageResource(R.mipmap.ic_launcher);
                break;
            case R.id.id_tab_myshop:
                api().showToast(" tab 2");

                api().getMyStoreInfo(new ApiCallback<StoreInfo>() {
                    @Override
                    public void onSuccess(StoreInfo data) throws JSONException {
                        final StoreInfo s = data;
                        mViewpager.setCurrentItem(1);
                        //还需api来实现
                        put_on_sale.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击后可以用对话框的方式来但是也是要写一个view传给对话框
                                HomeActivity.this.myDialog();//上架商品
                                api().getAllProducts(s.id, 1, 10, new ApiCallback<Page<ProductInfo>>() {
                                    @Override
                                    public void onSuccess(Page<ProductInfo> data) throws JSONException {
                                        HomeActivity.this.myshopAdapter.clear();
                                        ProductItem.Factory.convertFromProductInfo(Arrays.asList(data.data), HomeActivity.this.myshopAdapter);
                                        myshopAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(int code, String message, Object data) throws JSONException {

                                    }
                                });//重新获取商品所有的信息

                            }
                        });
                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        switch (code){
                            case Errors.NOT_LOGIN:
                                api().showToast("NOT_LOGIN");
                                break;
                            case Errors.NOT_MERCHANT:
                                api().showToast("NOT_MERCHANT");
                                break;
                            case Errors.NO_STORE:
                                api().showToast("NO_STORE");
                                break;
                        }
                    }
                });
                myshopImg.setImageResource(R.mipmap.ic_launcher);
                //TODO 更新自己的商品列表
                break;
            case R.id.id_tab_usr:
                api().showToast(" tab 3");
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

    //上架商品和修改商品时的对话弹窗
    protected void myDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.fill_product_detail, null);
        dialog.setView(dialogView);
        dialog.show();
        api().showToast("dialog show");

        final EditText product_name = dialogView.findViewById(R.id.fill_product_name);
        final EditText product_price = dialogView.findViewById(R.id.product_unit_price);
        final EditText product_amount = dialogView.findViewById(R.id.product_amount);
        final EditText product_description = dialogView.findViewById(R.id.product_description);

        Button confirm = dialogView.findViewById(R.id.confirm_product_info);
        Button btn_cancel = dialogView.findViewById(R.id.cancel_product_info);
        Button upload_img = dialogView.findViewById(R.id.upload_img);

        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传商品的图片，要调用系统的接口
            }
        });



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,description,price,amount;
                name = product_name.getText().toString();
                price = product_price.getText().toString();
                amount = product_amount.getText().toString();
                description = product_description.getText().toString();
                if (name.equals("") || price.equals("") || amount.equals("") || description.equals("")) {
                    api().showToast("商品名，价格，数量，描述均不能为空");
                }else {
                    api().createProduct(name, Integer.parseInt(price), Integer.parseInt(amount), description, new ApiCallback<ProductInfo>() {
                        @Override
                        public void onSuccess(ProductInfo data) throws JSONException {
                            api().showToast("上架商品成功");
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(int code, String message, Object data) throws JSONException {
                            api().showToast("上架商品失败");
                        }
                    });
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void refreshUserInfo(){
        View tab = userInfoPage;
        final TextView user = (TextView)tab.findViewById(R.id.user);
        final TextView balance = (TextView)tab.findViewById(R.id.balance);
        api().getUserInfo(new ApiCallback<UserInfo>(){

            @Override
            public void onSuccess(UserInfo data) throws JSONException {
                user.setText("用户名:" + data.username);
                balance.setText("余额:" + String.valueOf(data.balance));
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {

            }
        });

    }
}
