package com.example.fiction.Tools;

import android.os.SystemClock;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Caesar on 2017/8/14.
 */

public class Http {
    private static final String Tag = "Http";

    /**
     * 发送 POST 请求
     *
     * @param Url      地址
     * @param data     POST数据 String data = "username=" + URLEncoder.encode(username, "UTF-8")+ "&password=" + URLEncoder.encode(password, "UTF-8");//传递的数据
     * @param CallBack 回调函数对象
     */
    public static void POST(final String Url, final Map<String, String> data, final CallBack CallBack) {
        POST(Url, data, CallBack, true);
    }

    /**
     * 发送 POST 请求
     *
     * @param Url      地址
     * @param data     POST数据 String data = "username=" + URLEncoder.encode(username, "UTF-8")+ "&password=" + URLEncoder.encode(password, "UTF-8");//传递的数据
     * @param CallBack 回调函数对象
     */
    public static void POST(final String Url, final Map<String, String> data, final CallBack CallBack, boolean asyn) {
        if (asyn) {
            new Thread(() -> {
                Post(Url, data, CallBack);
            }
            ).start();
        } else {
            Post(Url, data, CallBack);
        }
    }

    /**
     * 发送 POST 请求
     *
     * @param Url      地址
     * @param data     POST数据 String data = "username=" + URLEncoder.encode(username, "UTF-8")+ "&password=" + URLEncoder.encode(password, "UTF-8");//传递的数据
     * @param CallBack 回调函数对象
     */
    public static void Post(final String Url, final Map<String, String> data, final CallBack CallBack) {
        HttpURLConnection conn = null;

        try {
            long lastTime;
            //根据地址创建URL对象(网络访问的url)
            URL url = new URL(Url);
            //url.openConnection();打开网络链接
            conn = (HttpURLConnection) url.openConnection();
            //设置请求的方式
            conn.setRequestMethod("POST");

            conn.setDoInput(true);//发送POST请求必须设置允许输出
            conn.setDoOutput(true);//发送POST请求必须设置允许输入
            //设置请求的头
            conn.setRequestProperty("Content-Type", data.get("ContentType") == null ? "application/x-www-form-urlencoded" : data.get("ContentType"));//"application/x-www-form-urlencoded");
            conn.setRequestProperty("Charset", data.get("ContentType") == null ? "utf-8" : data.get("ContentType"));
            conn.setRequestProperty("Content-Length", String.valueOf(Objects.requireNonNull(data.get("data")).getBytes().length));

            try {
                CallBack.beforeSend(conn);
            } catch (Exception ignored) {
            }

            //获取输出流
            OutputStream os = conn.getOutputStream();
            os.write(Objects.requireNonNull(data.get("data")).getBytes());
            os.flush();
            //获取响应的输入流对象



            String contentType = conn.getHeaderField("content-type");
            String encode = "utf-8";
            InputStream _is = null;
            if (conn.getResponseCode() >= 400 ) {
                _is = conn.getErrorStream();
            }
            else{
                _is = conn.getInputStream();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = _is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            buffer = null;
            baos.flush();

            InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());
            baos.close();
            _is.close();


            if (contentType != null && contentType.contains("charset=")) {
                encode = contentType.substring(contentType.indexOf("charset=") + "charset=".length());
            } else {
                //获取响应的输入流对象
                InputStreamReader is = new InputStreamReader(stream1, encode);
                BufferedReader bufferedReader = new BufferedReader(is);
                String line = null;
                //读取服务器返回信息
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("charset=")) {
                        encode = Tools.getMatcher(".*charset=([A-Za-z0-9-\"]+).*", line);
                        break;
                    }
                }
                bufferedReader.close();
                is.close();
            }
            //获取响应的输入流对象
            stream1.reset();
            InputStreamReader is = new InputStreamReader(stream1, encode);
            BufferedReader bufferedReader = new BufferedReader(is);
            StringBuilder strBuffer = new StringBuilder();
            String line = null;

            lastTime = SystemClock.uptimeMillis();

            //读取服务器返回信息
            while ((line = bufferedReader.readLine()) != null) {
                if (SystemClock.uptimeMillis() - lastTime >= 15000) {
                    os.close();
                    is.close();
                    stream1.close();
                    conn.disconnect();

                    try {
                        CallBack.error(-2, "timeout", conn);
                    } catch (Exception ignored) {
                    }

                    return;
                }

                strBuffer.append(line);
            }

            String result = strBuffer.toString();//接收从服务器返回的数据
            //关闭InputStream、关闭http连接
            os.close();
            is.close();
            stream1.close();
            conn.disconnect();

            try {
                CallBack.success(result, conn);
            } catch (Exception ignored) {
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                CallBack.error(-1, e.getMessage(), conn);
            } catch (Exception ignored) {
            }
        } finally {
            try {
                CallBack.complete();
            } catch (Exception ignored) {

            }
        }
    }

    /**
     * 发送 Get 请求
     *
     * @param Url      地址
     * @param CallBack 回调函数对象
     */
    public static void GET(String Url, CallBack CallBack) {
        GET(Url, CallBack, true);
    }

    /**
     * 发送 Get 请求
     *
     * @param Url      地址
     * @param CallBack 回调函数对象
     */
    public static void GET(final String Url, final CallBack CallBack, boolean asyn) {
        if (asyn) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Get(Url, CallBack);
                }
            }).start();
        } else {
            Get(Url, CallBack);
        }
    }

    /**
     * 发送 Get 请求
     *
     * @param Url      地址
     * @param CallBack 回调函数对象
     */
    private static void Get(String Url, CallBack CallBack) {
        HttpURLConnection conn = null;
        try {
            long lastTime;
            //根据地址创建URL对象(网络访问的url)
            URL url = new URL(Url);
            url.openConnection();//打开网络链接
            conn = (HttpURLConnection)
                    url.openConnection();
            //设置请求的方式
            conn.setRequestMethod("GET");
            //设置请求的头
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Charset", "utf-8");
            try {
                CallBack.beforeSend(conn);
            } catch (Exception ignored) {
            }

            String contentType = conn.getHeaderField("content-type");
            String encode = "utf-8";
            InputStream _is = null;
            if (conn.getResponseCode() >= 400 ) {
                _is = conn.getErrorStream();
            }
            else{
                _is = conn.getInputStream();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = _is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            buffer = null;
            baos.flush();

            InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());
            baos.close();
            _is.close();


            if (contentType != null && contentType.contains("charset=")) {
                encode = contentType.substring(contentType.indexOf("charset=") + "charset=".length());
            } else {
                //获取响应的输入流对象
                InputStreamReader is = new InputStreamReader(stream1, encode);
                BufferedReader bufferedReader = new BufferedReader(is);
                String line = null;
                //读取服务器返回信息
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("charset=")) {
                        encode = Tools.getMatcher(".*charset=([A-Za-z0-9-\"]+).*", line);
                        break;
                    }
                }
                bufferedReader.close();
                is.close();
            }
            //获取响应的输入流对象
            stream1.reset();
            InputStreamReader is = new InputStreamReader(stream1, encode);
            BufferedReader bufferedReader = new BufferedReader(is);
            StringBuilder strBuffer = new StringBuilder();
            String line = null;

            lastTime = SystemClock.uptimeMillis();

            //读取服务器返回信息
            while ((line = bufferedReader.readLine()) != null) {
                if (SystemClock.uptimeMillis() - lastTime >= 15000) {
                    is.close();
                    stream1.close();
                    conn.disconnect();

                    try {
                        CallBack.error(-2, "timeout", conn);
                    } catch (Exception ignored) {
                    }

                    return;
                }

                strBuffer.append(line);
            }

            String result = strBuffer.toString();//接收从服务器返回的数据
            //关闭InputStream、关闭http连接
            is.close();
            stream1.close();
            conn.disconnect();

            try {
                CallBack.success(result, conn);
            } catch (Exception ignored) {
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                CallBack.error(-1, e.getMessage(), conn);
            } catch (Exception ignored) {
            }
        }
        try {
            CallBack.complete();
        } catch (Exception ignored) {
        }
    }

    /**
     * 定义回调接口
     */
    public interface CallBack {
        /**
         * 调用请求之前调用
         *
         * @param conn 请求对象
         */

        void beforeSend(HttpURLConnection conn);

        /**
         * 请求完成之后调用
         */
        void complete();

        /**
         * 请求成功后调用
         *
         * @param result 接收到的数据
         * @param conn   请求对象
         */
        void success(String result, HttpURLConnection conn);

        /**
         * 请求失败后调用
         *
         * @param Code 错误代码
         * @param Mes  错误信息
         * @param conn 请求对象
         */
        void error(int Code, String Mes, HttpURLConnection conn);
    }
}