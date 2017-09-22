package com.facepp.demo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facepp.library.FaceppActionActivity;
import com.facepp.library.util.Util;
import com.google.gson.Gson;
import com.megvii.awesomedemo.facepp.R;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;
import com.megvii.cloud.http.Response;
import com.megvii.facepp.sdk.Facepp;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MainActivity extends Activity implements OnClickListener {
    private String similarity;
    private ImageView iv_img1;
    private ImageView iv_img2;
    private Button btn_choose_img1;
    private Button btn_choose_img2;
    private EditText et_similar;
    private Button btnPreview;
    private Button btn_submit;
    private Button btn_add;
    //本人博客中有博文
    private final static String API_key = "j8rzpGyLLGNX4UWMtTMgCSPuVOfMSiz0";
    private final static String API_Secret = "eUpri7B925IczgPagqcBsR9tUxeD0mNe";
    private static final int CHOOSE_PHOTO = 0;
    private static final int CHOOSE_PHOTO_2 = 1;
    private static final int CHOOSE_PHOTO_3 = 2;

    private String face_id1;
    private String face_id2;
    private Bitmap bitmap = null;
    private Bitmap bitmap2 = null;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private List<Uri> mSelected;
    RxPermissions rxPermissions;
    private boolean isError =false;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(!isError) {
                        String similar = et_similar.getText().toString();
                        if(TextUtils.isEmpty(similar)) {
                            similar = "85";
                        }
                        if (Double.parseDouble(similarity)>= Double.parseDouble(similar)) {
                            showDialog("识别结果","相似度为:",similarity,",是同一个人!");
                        }else {
                            showDialog("识别结果","相似度为:",similarity,",不是同一个人!");
                        }
                        btn_submit.setText("开始识别");
                    }else {
                        showDialog("识别结果","","服务端返回错误","");
                    }

                    break;
                case 2:
                    showDialog("识别结果","相似度为:","0",",未识别到Face");
                    btn_submit.setText("开始识别");
                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity);
        initView();
        initListener();
        rxPermissions = new RxPermissions(this);

    }

    private void initListener() {
        btn_choose_img1.setOnClickListener(this);
        btn_choose_img2.setOnClickListener(this);

        btn_submit.setOnClickListener(this);
        btnPreview.setOnClickListener(this);
      //  btn_add.setOnClickListener(this);
    }

    private void initView() {
        iv_img1 = (ImageView) findViewById(R.id.iv_img1);
        iv_img2 = (ImageView) findViewById(R.id.iv_img2);

        btn_choose_img1 = (Button) findViewById(R.id.btn_choose_img1);
        btn_choose_img2 = (Button) findViewById(R.id.btn_choose_img2);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btnPreview = (Button)findViewById(R.id.btn_preview);
        btn_add = (Button)findViewById(R.id.btn_add);
        et_similar = (EditText) findViewById(R.id.et_similar);

    }

    private void showDialog(String tittle,String mark1,String message,String mark) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(tittle)
                .setMessage(mark1+ message+ mark)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
    }

    /**
     * @param bitmap 第一张人脸图像
     * @param bitmap2 第二章人脸图像
     */
    private void detectFace(final Bitmap bitmap,final Bitmap bitmap2) {
        new Thread(){
            public void run() {
                CommonOperate commonOperate = new CommonOperate(Util.API_KEY,Util.API_SECRET,false);
                FaceSetOperate faceSetOperate = new FaceSetOperate(Util.API_KEY,Util.API_SECRET,false);
                ArrayList<String> faces = new ArrayList<String>();
                try {
                    Response response1 = commonOperate.detectByte(getArray(bitmap),0,null);
                    String faceToken1 = getFaceToken(response1);
                    faces.add(faceToken1);
                    Response response2 = commonOperate.detectByte(getArray(bitmap2),0,null);
                    String faceToken2 = getFaceToken(response2);
                    faces.add(faceToken2);
                    //创建人脸库，并往里加人脸
                    //create faceSet and add face
                    String faceTokens = creatFaceTokens(faces);
             //       Response deleteFaceSet = faceSetOperate.deleteFaceSetByOuterId("test",0);
              //      Log.e("faceSetResult",new String(deleteFaceSet.getContent()));
                    Response faceset = faceSetOperate.createFaceSet(null,"test",null,faceTokens,null, 1);
                    String faceSetResult = new String(faceset.getContent());
                    if(faceSetResult.contains("error_message") || faceSetResult.contains("ERROR_MESSAGE")) {
                        isError = true;
                    }
                    if(!TextUtils.isEmpty(faceToken1) && !TextUtils.isEmpty(faceToken2)) {
                        Response response3 = commonOperate.compare(faceToken1,"",getArray(bitmap),"",faceToken2,"",getArray(bitmap2),"");
                        if(new String(response3.getContent()).contains("error_message") || new String(response3.getContent()).contains("ERROR_MESSAGE")) {
                            isError = true;
                        }else {
                            similarity = getConfidence(response3);
                        }
                        Message message = Message.obtain();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    }else {
                        Message message = Message.obtain();
                        message.what = 2;
                        mHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }

    private String getConfidence(Response response) throws JSONException {
        if(response.getStatus() != 200) {
            return new String(response.getContent());
        }

        FaceCompareData data = new Gson().fromJson(new String(response.getContent()),FaceCompareData.class);
       return data.getConfidence()+"";
    }

    private String creatFaceTokens(ArrayList<String> faceTokens){
        if(faceTokens == null || faceTokens.size() == 0){
            return "";
        }
        StringBuffer face = new StringBuffer();
        for (int i = 0; i < faceTokens.size(); i++){
            if(i == 0){
                face.append(faceTokens.get(i));
            }else{
                face.append(",");
                face.append(faceTokens.get(i));
            }
        }
        return face.toString();
    }

    private String getFaceToken(Response response) throws JSONException {
        if(response.getStatus() != 200){
            return new String(response.getContent());
        }
        String res = new String(response.getContent());
        Log.e("response", res);
        JSONObject json = new JSONObject(res);
        JSONArray array = json.optJSONArray("faces");
        if(array.length() != 0) {
            String faceToken = array.optJSONObject(0).optString("face_token");
            return faceToken;
        }
        return "";
    }

    /**
     * 将bitmap转换为byte[]
     * @param bitmap 需要转换的bitmap
     * @return 返回byte[]
     */
    private byte[] getArray(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.toByteArray();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        byte[] array = baos.toByteArray();
        return array;
    }

    @Override
    public void onClick(final View v) {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            switch (v.getId()) {
                                case R.id.btn_choose_img1:
                                    openPicture(CHOOSE_PHOTO);
                                    break;
                                case R.id.btn_choose_img2:
                                    openPicture(CHOOSE_PHOTO_2);
                                    break;
                                default:
                                    break;
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Permission request denied", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        switch (v.getId()) {
            case R.id.btn_submit:
                btn_submit.setText("正在比对...");
                detectFace(bitmap, bitmap2);
                break;
            case R.id.btn_preview:
                Intent intent = new Intent(this,FaceppActionActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 打开相册
     */

    private void openPicture(int choose) {

        Matisse.from(MainActivity.this)
                .choose(MimeType.allOf())
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
                .maxSelectable(1)
                .countable(true)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(choose);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    mSelected = Matisse.obtainResult(data);
                    try {
                        bitmap = getBitmapFormUri(this,mSelected.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Glide.with(this).load(mSelected.get(0)).into(iv_img1);
                }
                break;
            case CHOOSE_PHOTO_2:
                if (resultCode == RESULT_OK) {
                    mSelected = Matisse.obtainResult(data);
                    try {
                        bitmap2 = getBitmapFormUri(this,mSelected.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Glide.with(this).load(mSelected.get(0)).into(iv_img2);
                }
            case CHOOSE_PHOTO_3:
            //    showDialog("添加结果","","正在添加到人脸集合","");
                break;
            default:
                break;
        }
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
