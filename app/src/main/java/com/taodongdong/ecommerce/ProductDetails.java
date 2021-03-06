package com.taodongdong.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.taodongdong.ecommerce.api.ApiCallback;
import com.taodongdong.ecommerce.api.OrderInfo;
import com.taodongdong.ecommerce.api.ProductInfo;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductDetails extends AbstractActivity {

    Button purchase;
    Button modify;
    Button pull_off;
    ImageView imageView;
    TextView name;
    TextView amount;
    TextView detail;
    TextView price;
    TextView img_file_path;
    int ID;
    private static int IMAGE = 1;
    private Uri uploadImgUri;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        purchase = (Button) findViewById(R.id.purchase);
        modify = (Button) findViewById(R.id.modify);
        imageView = (ImageView) findViewById(R.id.ProductIcon);
        name = (TextView) findViewById(R.id.productName);
        amount = (TextView) findViewById(R.id.remainingNum);
        detail = (TextView) findViewById(R.id.detailOfProduct);
        price = (TextView) findViewById(R.id.priceOfProduct);
        pull_off = (Button) findViewById(R.id.pull_off_shelves);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        ID = intent.getIntExtra("id", 0);
        if (ID != 0) {
            api().getProductInfo(ID, new ApiCallback<ProductInfo>() {
                @Override
                public void onSuccess(ProductInfo data) throws JSONException {

                    ImageGetter ig = new ImageGetter(imageView);
                    ig.execute(data.productImage);
                    name.setText(data.productName);
                    amount.setText(String.valueOf(data.productAmount));
                    detail.setText(data.productDescription);
                    price.setText(data.getProductPriceReadable());

                    modify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProductDetails.this.modify_Dialog();
                        }
                    });//修改商品信息的事件监听
                    pull_off.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProductDetails.this.confirm_pulloff();
                        }
                    });
                    if (Integer.parseInt(uid) == data.merchantUserId) {
                        purchase.setVisibility(View.GONE); //商家权限，隐藏购买按钮
                    } else {
                        modify.setVisibility(View.GONE);
                        pull_off.setVisibility(View.GONE);//用户权限，隐藏修改和下架按钮
                    }

                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {

                }
            });
        }


        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseDialog();
            }
        });

    }

    protected void purchaseDialog() {
        final EditText amount = new EditText(this);
        amount.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确认要买吗？请输入购买的数量");
        builder.setView(amount);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String str = amount.getText().toString();
                int num = str.length() == 0 ? 1 : Integer.parseInt(str);
                ProductDetails.this.api().createOrder(ID,num, new ApiCallback<OrderInfo>() {
                    @Override
                    public void onSuccess(OrderInfo data) throws JSONException {
                        ProductDetails.this.api().payOrder(data.id, new ApiCallback<String>() {
                            @Override
                            public void onSuccess(String data) throws JSONException {
                                api().showToast("购买成功");
                                ProductDetails.this.refreshProduct();
                            }

                            @Override
                            public void onError(int code, String message, Object data) throws JSONException {
                                ProductDetails.this.api().showToast("支付失败：" + message);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        ProductDetails.this.api().showToast("创建订单失败：" + message);
                    }
                });

                dialogInterface.cancel();
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();

    }


    //上架商品和修改商品时的对话弹窗
    protected void modify_Dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.fill_product_detail, null);
        dialog.setView(dialogView);
        dialog.show();
        api().showToast("dialog show");

        img_file_path = dialogView.findViewById(R.id.img_file_path);
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
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
                api().showToast("上传图片中");
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, description, price, amount;
                name = product_name.getText().toString();
                price = product_price.getText().toString();
                amount = product_amount.getText().toString();
                description = product_description.getText().toString();
                if (name.equals("") || price.equals("") || amount.equals("") || description.equals("")) {
                    api().showToast("商品名，价格，数量，描述均不能为空");
                } else {
                    ProductInfo pi = new ProductInfo();
                    pi.id = ID;
                    pi.productName = name;
                    pi.productAmount = Integer.parseInt(amount);
                    pi.productDescription = description;
                    pi.productPrice = Integer.parseInt(price);


                    api().modifyProduct(pi, new ApiCallback<ProductInfo>() {
                        @Override
                        public void onSuccess(ProductInfo data) throws JSONException {
                            String path = img_file_path.getText().toString();
                            Log.i("tdd:", "修改商品成功");
                            if (!path.equals("")) {
                                Log.e("Fuck", path);
                                File file = null;
                                try {
                                    InputStream fin = getContentResolver().openInputStream(uploadImgUri);
                                    file = compressImg(fin);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return;
                                }

                                api().uploadImage(data.id, file, new ApiCallback<String>() {
                                    @Override
                                    public void onSuccess(String data) throws JSONException {
                                        api().showToast("修改商品成功并上传了图片");
                                        ProductDetails.this.refreshProduct();
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onError(int code, String message, Object data) throws JSONException {

                                    }
                                });
                            } else {
                                api().showToast("img_file_path为空！");
                            }
                        }

                        @Override
                        public void onError(int code, String message, Object data) throws JSONException {
                            api().showToast("修改商品失败");
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


    protected void refreshProduct(){
        api().getProductInfo(ID, new ApiCallback<ProductInfo>() {
            @Override
            public void onSuccess(ProductInfo data) throws JSONException {

                ImageGetter ig = new ImageGetter(imageView);
                ig.execute(data.productImage);
                name.setText(data.productName);
                amount.setText(String.valueOf(data.productAmount));
                detail.setText(data.productDescription);
                price.setText(data.getProductPriceReadable());

                modify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductDetails.this.modify_Dialog();
                    }
                });//修改商品信息的事件监听
                pull_off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductDetails.this.confirm_pulloff();
                    }
                });


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
            uploadImgUri = selectedImage;
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

    //压缩图片
    protected File compressImg(InputStream fin) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        //图片名
        String filename = format.format(date);

        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        Bitmap bitmap = BitmapFactory.decodeStream(fin);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap == null) Log.e("Fuck", "bitmap is null");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 1024) { // 循环判断如果压缩后图片是否大于1mb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        //此时压缩的数据已经都在baos里面了,接下来把baos里面的数据传给file即可。
        try {
            if (!file.exists()) this.createFile(file);
            else Log.e("Fuck", "file exist");
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            Log.e("Fuck", "can't come to this fos");
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

    private String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {

                Log.e("Fuck", "create file");
                file.createNewFile();
            } else {

                Log.e("Fuck", "文件夹不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    protected void confirm_pulloff() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下架商品记得补充！！！！
                api().removeProduct(ID, new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) throws JSONException {
                        api().showToast("下架商品成功");
//                        Intent intent = new Intent();
//                        intent.putExtra("tab", "1");
//                        intent.setClass(ProductDetails.this, HomeActivity.class);
//                        startActivity(intent);
                        ProductDetails.this.finish();
                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        api().showToast("下架商品失败");
                    }
                });
                dialog.cancel();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage("你确认要下架该商品吗？");
        builder.setTitle("提示");
        builder.show();
    }
}

class ImageGetter extends AsyncTask<String, Integer, Bitmap> {

    private ImageView iv;

    public ImageGetter(ImageView imageView) {
        iv = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... objs) {
        Bitmap image = null;
        try {
            Log.i("TDD","img URL:" + (String)objs[0]);
            InputStream is = new java.net.URL((String) objs[0]).openStream();
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap == null){
            return;
        }
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = 200f / src_w;
        float scale_h = 200f / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,true);
        iv.setImageBitmap(bitmap);

    }
}
