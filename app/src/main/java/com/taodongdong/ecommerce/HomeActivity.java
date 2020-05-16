package com.taodongdong.ecommerce;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.Manifest;
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
import android.os.Environment;
//import android.support.v4.app.ActivityCompat;


import android.app.Activity;

import android.database.Cursor;
import android.net.Uri;

import android.provider.MediaStore;

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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

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
    private TextView img_file_path;
    private Button reg_saler;
    private Button put_on_sale;

    private View userInfoPage;
    private TextView orderBuy;
    private TextView orderSale;

    //声明ViewPager的适配器
    private PagerAdapter mAdpater;
    //Tab的List
    private List<View> mTabs = new ArrayList<>();

    private UserInfo userInfo;
    private TextView money;
    private static int IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉TitleBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_pager);
        initViews();//初始化控件
        initDatas();//初始化数据
        initEvents();//初始化事件

        String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int storagePermission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //检测是否有权限，如果没有权限，就需要申请
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            requestPermissions(PERMISSIONS,0);
            //返回false。说明没有授权
        }


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
                intent.putExtra("authority","0");
                intent.setClass(HomeActivity.this,ProductDetails.class);
                startActivity(intent);

            }
        });
        myshopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int ID = HomeActivity.this.myshopAdapter.getItem(position).getID();
                Bundle b = new Bundle();
                b.putInt("id",ID);
                Intent intent = new Intent();
                intent.putExtras(b);
                intent.putExtra("authority","1");
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
        View.OnClickListener orderListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 跳转到订单页面
                Bundle b = new Bundle();
                if(v == orderBuy){
                    b.putInt("type",OrderActivity.BUY_ORDER);
                }else{
                    b.putInt("type",OrderActivity.SALE_ORDER);
                }
                Intent intent = new Intent();
                intent.putExtras(b);
                intent.setClass(HomeActivity.this,OrderActivity.class);
                startActivity(intent);
            }
        };
        orderBuy.setOnClickListener(orderListener);
        orderSale.setOnClickListener(orderListener);

        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.this.recharge_dialog();
            }
        });

        reg_saler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.this.confirm_reg();
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


        //获取到三个Tab
        LayoutInflater inflater = LayoutInflater.from(this);
        View tab1 = inflater.inflate(R.layout.shop, null);
        View tab2 = inflater.inflate(R.layout.myshop, null);
        View tab3 = inflater.inflate(R.layout.usr, null);

        searchView = (SearchView)tab1.findViewById(R.id.search_products);
        productList = (ListView)tab1.findViewById(R.id.id_shop_list);
        myshopList = (ListView)tab2.findViewById(R.id.id_myshop_list);
        orderBuy = (TextView)tab3.findViewById(R.id.manage_buy_order);
        orderSale = (TextView)tab3.findViewById(R.id.manage_sale_order);
        put_on_sale = (Button) tab2.findViewById(R.id.put_on_sale);
        money = (TextView) tab3.findViewById(R.id.balance);
        reg_saler = (Button) tab3.findViewById(R.id.reg_saler);

        userInfoPage = tab3;
        api().getUserInfo(new ApiCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo data) throws JSONException {
                HomeActivity.this.userInfo = data;
                if(userInfo.authority == 0){
                    orderSale.setVisibility(View.GONE);
                }else{
                    reg_saler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                //TODO 处理错误
            }
        });



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
                                HomeActivity.this.myDialog(s.id);//上架商品


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

    //上架商品对话弹窗
    protected void myDialog(int sid){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.fill_product_detail, null);
        dialog.setView(dialogView);
        dialog.show();
        api().showToast("create product show");

        img_file_path = dialogView.findViewById(R.id.img_file_path);
        final EditText product_name = dialogView.findViewById(R.id.fill_product_name);
        final EditText product_price = dialogView.findViewById(R.id.product_unit_price);
        final EditText product_amount = dialogView.findViewById(R.id.product_amount);
        final EditText product_description = dialogView.findViewById(R.id.product_description);
        final int storeid = sid;

        Button confirm = dialogView.findViewById(R.id.confirm_product_info);
        Button btn_cancel = dialogView.findViewById(R.id.cancel_product_info);
        Button upload_img = dialogView.findViewById(R.id.upload_img);

        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传商品的图片，要调用系统的接口
                Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
                api().showToast("上传图片中");
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
                            String path = img_file_path.getText().toString();
                            Log.i("tdd:","创建商品成功");
                            if(!path.equals("")) {
                                Log.e("Fuck",path);
                                File file = compressImg(path);
                                api().uploadImage(data.id, file, new ApiCallback<String>() {
                                    @Override
                                    public void onSuccess(String data) throws JSONException {
                                        api().showToast("上架商品成功");
                                        api().getAllProducts(storeid, 1, 10, new ApiCallback<Page<ProductInfo>>() {
                                            @Override
                                            public void onSuccess(Page<ProductInfo> data) throws JSONException {
                                                Log.e("tdd:","上传图片成功");
                                                HomeActivity.this.myshopAdapter.clear();
                                                if(data.data == null) Log.e("tdd:","获取商品列表为空");
                                                ProductItem.Factory.convertFromProductInfo(Arrays.asList(data.data), HomeActivity.this.myshopAdapter);
                                                myshopAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onError(int code, String message, Object data) throws JSONException {
                                                Log.e("tdd:","上传图片，上架失败：" + message + "code :" + code);
                                            }
                                        });//重新获取商品所有的信息
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onError(int code, String message, Object data) throws JSONException {

                                    }
                                });
                            }else {
                                api().showToast("img_file_path为空！");
                            }
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

    //动态获取权限
//    private void checkPermission() {
//        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
//                .WRITE_EXTERNAL_STORAGE)) {
//                api().showToast("请开通相关权限，否则无法正常使用本应用！");
//            }
//            //申请权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//
//        } else {
//            api().showToast("授权成功!");
//
//        }
//    }

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

    //处理选择图片后的回调，并传输数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        api().showToast("回调开始");
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE) {//是否选择，没选择就不会继续
//            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
//            String[] proj = {MediaStore.Images.Media.DATA};
//            Cursor actualimagecursor = getContentResolver().query(uri,proj,null,null,null);
//            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            actualimagecursor.moveToFirst();
//            String img_path = actualimagecursor.getString(actual_image_column_index);
//            img_file_path.setText(img_path);
//            api().showToast(img_path);
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            c.close();
            img_file_path.setText(imagePath);
            api().showToast(imagePath);

        }
    }

    protected File compressImg(String path){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        //图片名
        String filename = format.format(date);

        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(bitmap == null) Log.e("Fuck","bitmap is null");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 90;
            while (baos.toByteArray().length / 1024 > 1024) { // 循环判断如果压缩后图片是否大于1mb,大于继续压缩
                baos.reset(); // 重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
            //此时压缩的数据已经都在baos里面了,接下来把baos里面的数据传给file即可。
            try {
                if(!file.exists()) this.createFile(file);
                else Log.e("Fuck","file exist");
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    fos.write(baos.toByteArray());
                    fos.flush();
                    fos.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                Log.e("Fuck","can't come to this fos");
                e.printStackTrace();
            }

            try {


                fis.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
            // recycleBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e("Fuck","can't come to this fis    "+e.toString());
            e.printStackTrace();

        }
        //
//        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
//        newOpts.inJustDecodeBounds = true;
//        newOpts.inJustDecodeBounds = false;
//        int w = newOpts.outWidth;
//        int h = newOpts.outHeight;
//        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;// 这里设置高度为800f
//        float ww = 480f;// 这里设置宽度为480f
//        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//        int be = 1;// be=1表示不缩放
//        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
//            be = (int) (newOpts.outWidth / ww);
//        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
//            be = (int) (newOpts.outHeight / hh);
//        }
//        if (be <= 0)
//            be = 1;
//        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//        bitmap = BitmapFactory.decodeFile(path, newOpts);
        //以上完成按大小压缩图片，接下来再按质量压缩图片。

        return file;
    }

    private String createFile(File file){
        try{
            if(file.getParentFile().exists()){

                Log.e("Fuck","create file");
                file.createNewFile();
            }
            else {

                Log.e("Fuck","文件夹不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    protected void recharge_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.recharge, null);
        dialog.setView(dialogView);
        dialog.show();
        api().showToast("recharge show");


        final EditText recharge = dialogView.findViewById(R.id.recharge);

        Button confirm = dialogView.findViewById(R.id.confirm_recharge);
        Button btn_cancel = dialogView.findViewById(R.id.cancel_recharge);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = recharge.getText().toString();
                if(!num.equals("")){
                    api().recharge(Integer.parseInt(num), new ApiCallback<UserInfo>() {
                        @Override
                        public void onSuccess(UserInfo data) throws JSONException {
                            money.setText(String.valueOf(data.balance));
                            api().showToast("充值成功");
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(int code, String message, Object data) throws JSONException {
                            api().showToast("充值失败");
                        }
                    });
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    protected void confirm_reg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下架商品记得补充！！！！
                api().approveMerchant(new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) throws JSONException {
                        api().showToast("注册为商家成功");
                        reg_saler.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {

                    }
                });

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage("你确认要注册为商家吗？");
        builder.setTitle("提示");
        builder.show();
    }
}
