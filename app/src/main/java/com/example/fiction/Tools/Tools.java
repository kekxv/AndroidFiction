package com.example.fiction.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Environment;
import android.os.SystemClock;
import android.renderscript.*;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.*;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Caesar on 2017/7/13.
 */

public class Tools {
    private final static String TAG = "工具类:";
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat IsoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatterInt = new SimpleDateFormat("yyyyMMdd");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * byte[] 转换为16进制字符串
     *
     * @param b byte[]
     * @return 16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        StringBuilder ret = new StringBuilder();
        for (byte aB : b) {
            String hex = Integer.toHexString(aB & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret.append(hex.toUpperCase());
        }
        return ret.toString();
    }

    //将指定byte数组以16进制的形式打印到控制台
    public static void printHexString(byte[] b) {
        Log.i(TAG, bytes2HexString(b));
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     * 16 进制字符串 转 byte[] 数组
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 合并 byte 数组
     *
     * @param byte_1 第一个byte数组
     * @param byte_2 第二个byte数组
     * @return 合并后的新的byte数组
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * double 转 byte[]
     *
     * @param d
     * @return
     */
    public static byte[] double2Bytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    /**
     * byte[] 转 double
     *
     * @param arr
     * @return
     */
    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }


    /**
     * 从byte数组获取长度
     *
     * @param data 数组
     * @return
     */
    public static int GetInt(byte[] data) {
        byte[] _len = new byte[4];
        System.arraycopy(data, 1, _len, 0, 4);
        return GetIntFromBytes(_len);
    }

    /**
     * 从byte数组获取长度
     *
     * @param data 数组
     * @return
     */
    private static int GetIntFromBytes(byte[] data) {
        int u = 0;
        int _i = 0;

        for (byte _data : data) {
            u = u | (_data << _i);
            _i += 8;
        }

        return u;
    }

    public static int GetIntFromBytes(byte[] bytes, int offset, int count) {
        int u = 0;
        int _i = 0;
        for (int i = 0; i < count; i++) {
            u = u | ((bytes[i + offset] & 0xFF) << _i);
            _i += 8;
        }

        return u;
    }

    /**
     * int转换为byte数组
     *
     * @param data 大小
     * @param len  byte数组大小
     * @return
     */
    public static byte[] GetBytesFromInt(int data, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[i] = (byte) (data >> 8 * (3 - i) & 0xFF);
        }
        return b;
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * 获取当前时间
     *
     * @return 获取当前时间 yyyy年MM月dd日   HH:mm:ss
     */
    public static String getTime() {
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 获取当前时间
     *
     * @return 获取当前时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getDateNew() {
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        return formatTime.format(curDate);
    }

    /**
     * 获取当前时间
     *
     * @return 获取当前时间 yyyyMMdd 格式时间
     */
    public static int getDate() {
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        return Integer.parseInt(formatterInt.format(curDate));
    }

    /**
     * 获取当前时间
     *
     * @return 获取当前时间 yyyyMMdd 格式时间
     */
    public static String getYMDDate() {
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        return formatterDate.format(curDate);
    }

    /**
     * 提取时间
     *
     * @param curDate 时间对象
     * @return yyyyMMdd 格式时间
     */
    static int getDate(Date curDate) {
        return Integer.parseInt(formatterInt.format(curDate));
    }

    /**
     * 睡眠
     *
     * @param ms 毫秒
     */
    public static void sleep(long ms) {
        long start = SystemClock.uptimeMillis();
        long duration = ms;
        boolean interrupted = false;

        do {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                interrupted = true;
            }
            duration = start + ms - SystemClock.uptimeMillis();
        } while (duration > 0);

        if (interrupted) {
            // Important: we don't want to quietly eat an interrupt() event,
            // so we make sure to re-interrupt the thread so that the next
            // call to Thread.sleep() or Object.wait() will be interrupted.
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 清除最后的多余空白
     *
     * @param src 数据
     * @return 清除后的数据
     */
    public static byte[] bytesTrim(final byte[] src) {
        for (int i = 0; i < src.length; i++) {
            if (src[i] == 0) {
                if (i == 0) break;
                byte[] btResult = new byte[i];

                System.arraycopy(src, 0, btResult, 0, i);
                return btResult;
            }
        }

        return src;
    }

    /**
     * 裁剪字符串
     *
     * @param src
     * @param offset
     * @param count
     * @return
     */
    public static byte[] cutBytes(byte[] src, int offset, int count) {
        if (offset + count > src.length) return null;
        byte[] _data = new byte[count];
        System.arraycopy(src, offset, _data, 0, count);
        return _data;
    }

    /**
     * byte[] 转换为int
     *
     * @param b      数据
     * @param offset 游标
     * @return int
     */
    public static int bytesToInt(byte[] b, int offset, int count) {
        if (count + offset > b.length) return 0;
        int value = 0;
        for (int i = 0; i < count; i++) {
            int shift = (count - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * 将从Message中获取的，表示图片的字符串解析为Bitmap对象
     *
     * @param strBmpBase64
     * @return
     */
    public static Bitmap decodeBitmap(final String strBmpBase64) {
        Bitmap bitmap = null;

        byte[] imgByte = null;
        InputStream input = null;
        try {
            imgByte = Base64.decode(strBmpBase64, Base64.DEFAULT);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            input = new ByteArrayInputStream(imgByte);
            SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(input, null, options));
            bitmap = (Bitmap) softRef.get();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imgByte != null) {
                imgByte = null;
            }

            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    public static Bitmap compressBitmap(final Bitmap bmpSrc, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bmpSrc.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bmpDst = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    /*
    Log.i("wechat", "压缩后图片的大小" + (bm.getByteCount() / 1024 / 1024)
      + "M宽度为" + bm.getWidth() + "高度为" + bm.getHeight()
      + "bytes.length=  " + (bytes.length / 1024) + "KB"
      + "quality=" + quality);
      */

        return bmpDst;
    }

    /***
     * 图片去色,返回灰度图片
     *
     * @param bmpOriginal
     *            传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return convertGreyImgByFloyd(bmpGrayscale);
    }

    private static Bitmap convertGreyImgByFloyd(Bitmap img) {
        int width = img.getWidth(); // 获取位图的宽
        int height = img.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] gray = new int[height * width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                gray[width * i + j] = red;
            }
        }
        int e = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int g = gray[width * i + j];
                if (g >= 128) {
                    pixels[width * i + j] = 0xffffffff;
                    e = g - 255;
                } else {
                    pixels[width * i + j] = 0xff000000;
                    e = g - 0;
                }
                if (j < width - 1 && i < height - 1) {
                    // 右边像素处理
                    gray[width * i + j + 1] += 3 * e / 8;
                    // 下
                    gray[width * (i + 1) + j] += 3 * e / 8;
                    // 右下
                    gray[width * (i + 1) + j + 1] += e / 4;
                } else if (j == width - 1 && i < height - 1) {// 靠右或靠下边的像素的情况
                    // 下方像素处理
                    gray[width * (i + 1) + j] += 3 * e / 8;
                } else if (j < width - 1 && i == height - 1) {
                    // 右边像素处理
                    gray[width * (i) + j + 1] += e / 4;
                }
            }
        }

        //Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return mBitmap;
    }

    /**
     * 摄像头回调数据转换为位图数据
     *
     * @param data        摄像头数据
     * @param previewSize 摄像头摄像头分辨率
     * @return 转换后Bitmap位图
     */
    public static Bitmap Bytes2Bitmap(byte[] data, Camera.Size previewSize) {
        //处理data
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                previewSize.width,
                previewSize.height,
                null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 80--JPG图片的质量[0-100],100最高
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);
        byte[] rawImage = baos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {
            baos.close();
            baos = null;
            yuvimage = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
    }


    /**
     * YUV图像数据转灰度RGB bitmap
     *
     * @param yuv
     * @param width
     * @param height
     * @return
     */
    public static Bitmap yuv2grayscale(byte[] yuv, int width, int height) {
        final int size = width * height;
        int[] pixels = new int[size];

        if (size % 8 == 0) {
            for (int i = 0; i < size; i += 8) {
                int y = yuv[i] & 0xff;
                pixels[i] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 1] & 0xff;
                pixels[i + 1] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 2] & 0xff;
                pixels[i + 2] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 3] & 0xff;
                pixels[i + 3] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 4] & 0xff;
                pixels[i + 4] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 5] & 0xff;
                pixels[i + 5] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 6] & 0xff;
                pixels[i + 6] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 7] & 0xff;
                pixels[i + 7] = 0xFF000000 | y << 16 | y << 8 | y;
            }
        } else if (size % 4 == 0) {
            for (int i = 0; i < size; i += 4) {
                int y = yuv[i] & 0xff;
                pixels[i] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 1] & 0xff;
                pixels[i + 1] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 2] & 0xff;
                pixels[i + 2] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 3] & 0xff;
                pixels[i + 3] = 0xFF000000 | y << 16 | y << 8 | y;
            }
        } else if (size % 2 == 0) {
            for (int i = 0; i < size; i += 2) {
                int y = yuv[i] & 0xff;
                pixels[i] = 0xFF000000 | y << 16 | y << 8 | y;

                y = yuv[i + 1] & 0xff;
                pixels[i + 1] = 0xFF000000 | y << 16 | y << 8 | y;
            }
        } else {
            for (int i = 0; i < size; i++) {
                int y = yuv[i] & 0xff;
                pixels[i] = 0xFF000000 | y << 16 | y << 8 | y;
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap Bytes2Bitmap2(byte[] data, Camera.Size previewSize) {
        int pixels[] = new int[previewSize.width * previewSize.height];

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        decodeYUV420(pixels, data, previewSize.width, previewSize.height);
        return Bitmap.createBitmap(pixels, previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap YuvToBmp(Context context, byte[] data, int width, int height) {
        RenderScript rs;
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
        byte[] outBytes = new byte[width * height * 4];

        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.RGBA_8888(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
                .setX(width).setY(height)
                .setYuvFormat(android.graphics.ImageFormat.NV21);

        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);

        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(data);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        out.copyTo(outBytes);

        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);

        return bmpout;
    }

    public static void decodeYUV420(int[] rgb, byte[] yuv420, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420[uvp++]) - 128;
                    u = (0xff & yuv420[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    /**
     * base64 图转换为Bitmap 图
     *
     * @param base64Str base64 图
     * @return Bitmap 图
     */
    public static Bitmap convert(String base64Str) {
        try {
            byte[] decodedBytes = Base64.decode(
                    base64Str.substring(base64Str.indexOf(",") + 1),
                    Base64.DEFAULT
            );

            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Bitmap 转换为 base64 数据
     *
     * @param bitmap Bitmap 图
     * @return Base64 图片对象
     */
    public static String convert(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建一个副本
     *
     * @param Bmp 需要副本的图
     * @return 副本之后的图
     */
    public static Bitmap CloneBitmap(Bitmap Bmp) {
        //创建图片副本
        //1.在内存中创建一个与原图一模一样大小的bitmap对象，创建与原图大小一致的白纸
        Bitmap bmCopy = Bitmap.createBitmap(Bmp.getWidth(), Bmp.getHeight(), Bmp.getConfig());

        //2.创建画笔对象
        Paint paint = new Paint();

        //3.创建画板对象，把白纸铺在画板上
        Canvas canvas = new Canvas(bmCopy);

        //4.开始作画，把原图的内容绘制在白纸上
        canvas.drawBitmap(Bmp, new Matrix(), paint);
        paint = null;
        canvas = null;
        return bmCopy;
    }

    /**
     * Bitmap 转换为 base64 数据
     *
     * @param bitmap Bitmap 图
     * @return Base64 图片对象
     */
    public static String ConvertNewBitmap(Bitmap bitmap) {
        try {
            Bitmap mTempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);//把当前bitmap赋值给待滤镜处理的bitmap
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mTempBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            recycleBitmap(mTempBitmap);
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * byte[] 转换为 base64 数据
     *
     * @param bitmapBytes byte[] 图
     * @return Base64 图片对象
     */
    public static String ByteToBase64(byte[] bitmapBytes) {
        return Base64.encodeToString(bitmapBytes, Base64.DEFAULT).replace("\n", "");
    }

    /**
     * 数组转Bitmap图片
     *
     * @param data 图片数组
     * @return bitmap图片
     */
    public static Bitmap BytesToBitmap(byte[] data) {
        if (data.length != 0) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } else {
            return null;
        }
    }

    /**
     * bitmap 转换为byte[]图
     *
     * @param bitmap 要转换的图 默认JPEG
     * @return 转换后的byte[]对象
     */
    public static byte[] BitmapToBytes(Bitmap bitmap) {
        return BitmapToBytes(bitmap, Bitmap.CompressFormat.JPEG);
    }

    /**
     * bitmap 转换为byte[]图
     *
     * @param bitmap 要转换的图
     * @param type   类型
     * @return 转换后的byte[]对象
     */
    public static byte[] BitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat type) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(type, 100, baos);
        byte[] data = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 回收背景图资源
     *
     * @param view ImageView对象
     */
    public static void recycleBackgroundBitMap(ImageView view) {
        if (view != null) {
            BitmapDrawable bd = (BitmapDrawable) view.getBackground();
            recycleBitmapDrawable(bd);
        }
    }

    /**
     * 回收 ImageView 资源
     *
     * @param imageView ImageView对象
     */
    public static void recycleImageViewBitMap(ImageView imageView) {
        if (imageView != null) {
            BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
            recycleBitmapDrawable(bd);
        }
    }

    /**
     * 回收 BitmapDrawable 资源
     *
     * @param bitmapDrawable BitmapDrawable
     */
    public static void recycleBitmapDrawable(BitmapDrawable bitmapDrawable) {
        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            recycleBitmap(bitmap);
        }
        bitmapDrawable = null;
    }

    /**
     * 回收bitmap
     *
     * @param bitmap 需要回收的图
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / (float) width;
        float scaleHeight = ((float) newHeight) / (float) height;
        if (scaleWidth < 0 || scaleHeight <= 0) return null;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        return newBM;
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    public static Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        cropWidth /= 2;
        int cropHeight = (int) (cropWidth / 1.2);
        return Bitmap.createBitmap(bitmap, w / 3, 0, cropWidth, cropHeight, null, false);
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 偏移效果
     *
     * @param origin 原图
     * @return 偏移后的bitmap
     */
    public static Bitmap skewBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.postSkew(-0.6f, -0.3f);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 判断文件是否存在 如果不存在则创建
     *
     * @param strFolder 文件路径
     * @return
     */
    public static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        return file.exists() || file.mkdir();
    }

    /**
     * 保存图片到sd卡
     *
     * @param mBitmap 需要保存的图片
     * @param bitName 保存的图片名
     * @return 是否保存成功
     */
    public static boolean saveBitmap(Bitmap mBitmap, String bitName) {
        @SuppressLint("SdCardPath") File f = new File("/sdcard/" + bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            try {
                fOut.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String ReadFile(String Name) throws Exception {
        String result = "";
        File file = null;
        FileInputStream is = null;
        try {
            file = new File(Environment.getExternalStorageDirectory(), Name);
            is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            int ret = is.read(b);
            if (ret < 0) {
                throw new Exception("没有读取到内容");
            }
//            result = new String(b,"GBK");
            result = new String(b);
            return result;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignored) {

                }
            }
        }
    }

    public static void WriteFile(String Name, String info) throws Exception {
        String result = "";
        File file = null;
        FileOutputStream fos = null;
        try {
            file = new File(Environment.getExternalStorageDirectory(), Name);
            fos = new FileOutputStream(file);
//            fos.write(info.getBytes("GBK"));
            fos.write(info.getBytes());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ignored) {

                }
            }
        }
    }

    /**
     * 对字符串加密,加密算法使用MD5,SHA-1,SHA-256,默认使用SHA-256
     *
     * @param strSrc  要加密的字符串
     * @param encName 加密类型
     * @return
     */
    public static String Encrypt(String strSrc, String encName) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            if (encName == null || encName.equals("")) {
                encName = "SHA-256";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytesToHexString(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    /**
     * 获取唯一UUID
     *
     * @param context
     * @return
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    private static byte[] intToByteArray(final int integer) {
        int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
        byte[] byteArray = new byte[4];

        for (int n = 0; n < byteNum; n++)
            byteArray[3 - n] = (byte) (integer >>> (n * 8));

        return (byteArray);
    }


    public static void restartAdbd() throws IOException, InterruptedException, SecurityException {
        Process su;
        try {
            su = Runtime.getRuntime().exec("/system/xbin/su");
        } catch (Exception e) {
            su = Runtime.getRuntime().exec("su");
        }
        String cmd = ""
                + "stop adbd\n"
                + "setprop service.adb.tcp.port 5555\n"
                + "start adbd\n"
                + "exit\n";
        su.getOutputStream().write(cmd.getBytes());
        if ((su.waitFor() != 0)) {
            throw new SecurityException();
        }
    }

    public static String ConvertCharacter(String s,String c1,String c2){
        try {
            return new String(s.getBytes(c1), c2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getMatcher(String regex, String source) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

}