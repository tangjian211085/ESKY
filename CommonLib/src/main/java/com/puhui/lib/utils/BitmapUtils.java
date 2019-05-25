package com.puhui.lib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/4/20.
 * Description:
 * Modified By:
 */

public class BitmapUtils {

    /***
     * 复制一份新的Bitmap
     */
    public static Bitmap toRoundCorner(Bitmap bitmap) {
        return toRoundCorner(bitmap, 0);
    }

    /***
     * 复制一份新的Bitmap
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int roundPx) {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        float ratio = 1f;
        // 压缩Bitmap到对应尺寸
//        if (bitmap.getRowBytes() * bitmap.getHeight() > 1024 * 1024) {
//            ratio = (float) Math.sqrt(bitmap.getRowBytes() * bitmap.getHeight() / 1024 / 1024);
//        }
        Bitmap roundCornerBitmap = Bitmap.createBitmap((int) (bitmap.getWidth() / ratio),
                (int) (bitmap.getHeight() / ratio), Bitmap.Config.ARGB_8888);

//        Bitmap roundCornerBitmap = Bitmap.createBitmap(bitmap.getWidth(),
//                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundCornerBitmap);
        int color = 0xff424242;// int color = 0xff424242;
        Paint paint = new Paint();
        paint.setColor(color);
        // 防止锯齿
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, roundCornerBitmap.getWidth(), roundCornerBitmap.getHeight());
        RectF rectF = new RectF(rect);
        // 相当于清屏
        canvas.drawARGB(0, 0, 0, 0);
        // 先画了一个带圆角的矩形
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 再把原来的bitmap画到现在的bitmap！！！注意这个理解
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return roundCornerBitmap;
    }

    /***
     * 图片转Base4
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        try {
            byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 1. 质量压缩
     * 设置bitmap options属性，降低图片的质量，像素不会减少
     * 第一个参数为需要压缩的bitmap图片对象，第二个参数为压缩后图片保存的位置
     * 设置options 属性0-100，来实现压缩
     */
    public static void compressImageToFile(Bitmap bmp, File file) {
        // 0-100 100为不压缩
        int options = 100;
        int maxSize = 500;  //500kb
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // 把压缩后的数据存放到baos中
        while (baos.toByteArray().length / 1024 > maxSize) {
            baos.reset();
            options -= 5;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 2. 尺寸压缩
     * 通过缩放图片像素来减少图片占用内存大小
     */
    public static void compressBitmapToFile(Bitmap bmp, File file) {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        float ratio = 1.5f;
        // 压缩Bitmap到对应尺寸    内存字节数 bmp.getRowBytes() * bmp.getHeight()
        if (bmp.getRowBytes() * bmp.getHeight() > 1024 * 1024) {
            ratio = (float) Math.sqrt(bmp.getRowBytes() * bmp.getHeight() / 1024 / 1024);
        }
        Bitmap result = Bitmap.createBitmap((int) (bmp.getWidth() / ratio), (int) (bmp.getHeight() / ratio), Bitmap.Config.ARGB_8888);
        float size = result.getRowBytes() * result.getHeight() / 1024 / 1024f;
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, (int) (bmp.getWidth() / ratio), (int) (bmp.getHeight() / ratio));
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);//30 是压缩率，表示压缩70%; 如果不压缩是100，表示压缩率为0
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 2. 尺寸压缩
     * 通过缩放图片像素来减少图片占用内存大小
     */
    public static Bitmap compressBitmap(Bitmap bmp, boolean isCompress) {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        float ratio = 1f;
        if (bmp.getRowBytes() * bmp.getHeight() > 2 * 1024 * 1024 && isCompress) {
            ratio = (float) Math.sqrt(bmp.getRowBytes() * bmp.getHeight() / 1024 / 1024) * 1.5f;
        }
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap((int) (bmp.getWidth() / ratio), (int) (bmp.getHeight() / ratio), Bitmap.Config.ARGB_8888);
        float size = result.getRowBytes() * result.getHeight() / 1024 / 1024f;
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, (int) (bmp.getWidth() / ratio), (int) (bmp.getHeight() / ratio));
        canvas.drawBitmap(bmp, null, rect, null);
        return result;
    }

    /***
     * 设置图片的采样率，降低图片像素
     */
    public static void compressBitmap(String filePath, File file) {
        // 数值越高，图片像素越低
        int inSampleSize = 2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //采样率
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();  //earlier version
    }

    /**
     * 从服务器取图片, 转换成Bitmap
     */
    public static Bitmap returnBitMap(String url) {
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * set TextView DrawableLeft or DrawableRight
     *
     * @param leftOrRight true   left; false   right
     */
    public static void setTextViewDrawable(Context context, int resourceId, TextView textView, boolean leftOrRight) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, resourceId);
            if (null != drawable) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                if (leftOrRight) {
                    textView.setCompoundDrawables(drawable, null, null, null);
                } else {
                    textView.setCompoundDrawables(null, null, drawable, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
