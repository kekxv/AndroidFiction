package com.example.fiction

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.*
import android.webkit.WebView
import android.graphics.Bitmap
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.ConsoleMessage
import android.webkit.WebSettings
import android.net.http.SslError
import android.view.KeyEvent
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.view.View
import com.example.fiction.Tools.Http
import com.example.fiction.Tools.Tools
import java.net.HttpURLConnection
import java.util.regex.Pattern
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()


        WebView.setWebContentsDebuggingEnabled(true)


        val webSettings = webView.settings
        // 支持 Js 使用
        webSettings.javaScriptEnabled = true
        // 开启DOM缓存,默认状态下是不支持LocalStorage的
        webSettings.domStorageEnabled = true
        // 开启数据库缓存
        webSettings.databaseEnabled = true
        // 设置 WebView 的缓存模式
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
//        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE

        webView.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
                        //                        时的操作
                        webView.goBack()   //后退
                        //webview.goForward();//前进
                        return true    //已处理
                    }
                }
                return false
            }
        })
        // 支持启用缓存模式
        webSettings.run {
            setAppCacheEnabled(true)
            // Android 私有缓存存储，如果你不调用setAppCachePath方法，WebView将不会产生这个目录
            setAppCachePath(cacheDir.absolutePath)
            // 不支持缩放
            setSupportZoom(false)
            // 允许加载本地 html 文件/false
            allowFileAccess = true
            // 允许通过 file url 加载的 Javascript 读取其他的本地文件,Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
            allowFileAccessFromFileURLs = false
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源，
            // Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
            // 如果此设置是允许，则 setAllowFileAccessFromFileURLs 不起做用
            allowUniversalAccessFromFileURLs = false
        }
        webView.webViewClient = object : WebViewClient() {

            /**
             * 当WebView得页面Scale值发生改变时回调
             */
            override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
                super.onScaleChanged(view, oldScale, newScale)
            }

            /**
             * 是否在 WebView 内加载页面
             *
             * @param view
             * @param url
             * @return
             */
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            /**
             * WebView 开始加载页面时回调，一次Frame加载对应一次回调
             *
             * @param view
             * @param url
             * @param favicon
             */
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)
            }

            /**
             * WebView 完成加载页面时回调，一次Frame加载对应一次回调
             *
             * @param view
             * @param url
             */
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                webView.evaluateJavascript(
                    "window.NavigatorCallback = window.NavigatorCallback || new (function(){\n" +
                            "   let self = this;\n" +
                            "   let callbackList = [];\n" +
                            "   let callbackId = 0;\n" +
                            "   self.pushCallback = function(cb,that){\n" +
                            "       if(callbackId>999999){callbackId=0;}\n" +
                            "       callbackList[callbackId] = {\n" +
                            "           cb:cb,\n" +
                            "           that:that\n" +
                            "       };\n" +
                            "       return callbackId++;\n" +
                            "   };\n" +
                            "   self.runCallback = function(cbId,...values){\n" +
                            "       if(!callbackList[cbId])return;\n" +
                            "       let ret = callbackList[cbId].cb.apply(callbackList[cbId].that||window,values);\n" +
                            "       callbackList[cbId] = null;\n" +
                            "       delete callbackList[cbId];\n" +
                            "       return ret;\n" +
                            "   };\n" +
                            "})();\n" +
                            "\n"
                ) {
                    //此处为 js 返回的结果
                }
            }

            /**
             * WebView 加载页面资源时会回调，每一个资源产生的一次网络加载，除非本地有当前 url 对应有缓存，否则就会加载。
             *
             * @param view WebView
             * @param url  url
             */
            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            /**
             * WebView 访问 url 出错
             *
             * @param view
             * @param request
             * @param error
             */
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
            }

            /**
             * WebView ssl 访问证书出错，handler.cancel()取消加载，handler.proceed()对然错误也继续加载
             *
             * @param view
             * @param handler
             * @param error
             */
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
            }
        }
        //设置WebChromeClient
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                return super.onConsoleMessage(consoleMessage)
            }

            /**
             * 当前 WebView 加载网页进度
             *
             * @param view
             * @param newProgress
             */
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            /**
             * Js 中调用 alert() 函数，产生的对话框
             *
             * @param view
             * @param url
             * @param message
             * @param result
             * @return
             */
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                return super.onJsAlert(view, url, message, result)
            }

            /**
             * 处理 Js 中的 Confirm 对话框
             *
             * @param view
             * @param url
             * @param message
             * @param result
             * @return
             */
            override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
                return super.onJsConfirm(view, url, message, result)
            }

            /**
             * 处理 JS 中的 Prompt对话框
             *
             * @param view
             * @param url
             * @param message
             * @param defaultValue
             * @param result
             * @return
             */
            override fun onJsPrompt(
                view: WebView,
                url: String,
                message: String,
                defaultValue: String,
                result: JsPromptResult
            ): Boolean {
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }

            /**
             * 接收web页面的icon
             *
             * @param view
             * @param icon
             */
            override fun onReceivedIcon(view: WebView, icon: Bitmap) {
                super.onReceivedIcon(view, icon)
            }

            /**
             * 接收web页面的 Title
             *
             * @param view
             * @param title
             */
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
            }
        }

        webView.addJavascriptInterface(NavigatorAPI(this, webView), "NavigatorAPI")

//        webView.loadUrl("http://192.168.88.107/Fiction/?ProxyUrl=http://192.168.88.107/ProxyCrossDomain/&Model=biquguan&ModelUrl=http://192.168.88.107/Fiction/JavaScript/model/biquguan.js")
//        webView.loadUrl("http://192.168.88.107/Fiction/")
//        webView.loadUrl("http://192.168.1.220/Fiction/?ProxyUrl=http://192.168.1.220/ProxyCrossDomain/&Model=mingzhuxiaoshuo&ModelUrl=http://192.168.1.220/Fiction/JavaScript/model/mingzhuxiaoshuo.js")
        webView.loadUrl("https://fictionpi.kekxv.com/")

    }

    class NavigatorAPI(private var context: Activity, private var webView: WebView) {
        private val UserAgent: String =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1"
        public val PcUserAgent: String =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36"

        @JavascriptInterface
        public fun GetData(url: String, callbackID: Int, errID: Int) {
            Http.GET(url,
                object : Http.CallBack {
                    override fun beforeSend(conn: HttpURLConnection) {
                        conn.connectTimeout = 20 * 1000
                        var host = ""
                        try {
                            val p =
                                Pattern.compile("[^/]*?(\\.[^/]+)+", Pattern.CASE_INSENSITIVE)
                            val matcher = p.matcher(url)
                            matcher.find()
                            host = matcher.group()
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        conn.setRequestProperty("host", host)
                        conn.setRequestProperty("User-Agent", UserAgent)
                    }

                    override fun complete() {}

                    override fun success(result: String, conn: HttpURLConnection) {
                        val json: JsonElement = JsonObject()
                        json["Code"] = 0
                        json["Message"] = ""
                        json["Result"] = result
                        context.runOnUiThread {
                            webView.evaluateJavascript("window.NavigatorCallback.runCallback($callbackID,${json.toString()})") {

                            }
                        }
                    }

                    override fun error(Code: Int, Mes: String, conn: HttpURLConnection) {
                        context.runOnUiThread {
                            webView.evaluateJavascript("window.NavigatorCallback.runCallback($errID,\"$Mes\")") {

                            }
                        }
                    }
                }
            )
        }

        @JavascriptInterface
        public fun PutData(url: String, Data: String, callbackID: Int, errID: Int) {
            val jsonElement: JsonElement = Gson().fromJson(Data)
            val json: JsonObject? = jsonElement.asJsonObject
            val map: MutableMap<String, String> = mutableMapOf()
            val it = json!!.keys()
            it.forEach {
                val key: String = it
                val value: String = json[key].asString
                map[key] = value
            }

            Http.POST(url, map,
                object : Http.CallBack {
                    override fun beforeSend(conn: HttpURLConnection) {
                        conn.connectTimeout = 20 * 1000
                        var host = ""
                        try {
                            val p =
                                Pattern.compile("[^/]*?(\\.[^/]+)+", Pattern.CASE_INSENSITIVE)
                            val matcher = p.matcher(url)
                            matcher.find()
                            host = matcher.group()
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        conn.setRequestProperty("host", host)
                        conn.setRequestProperty("User-Agent", UserAgent)
                    }

                    override fun complete() {}

                    override fun success(result: String, conn: HttpURLConnection) {
                        val _json: JsonElement = JsonObject()
                        _json["Code"] = 0
                        _json["Message"] = ""
                        _json["Result"] = result
                        context.runOnUiThread {
                            webView.evaluateJavascript("window.NavigatorCallback.runCallback($callbackID,${_json.toString()})") {

                            }
                        }
                    }

                    override fun error(Code: Int, Mes: String, conn: HttpURLConnection) {
                        context.runOnUiThread {
                            webView.evaluateJavascript("window.NavigatorCallback.runCallback($errID,\"$Mes\")") {

                            }
                        }
                    }
                }
            )
        }

        public fun PutJson(url: String, Data: String, callbackID: Int, errID: Int) {
            val jsonElement: JsonElement = Gson().fromJson(Data)
            val json: JsonObject? = jsonElement.asJsonObject
            val map: MutableMap<String, String> = mutableMapOf()
            val it = json!!.keys()
            it.forEach {
                val key: String = it
                val value: String = json[key].asString
                map[key] = value
            }
            map["ContentType"] = "application/json"
            Http.POST(url, map,
                object : Http.CallBack {
                    override fun beforeSend(conn: HttpURLConnection) {
                        conn.connectTimeout = 20 * 1000
                        var host = ""
                        try {
                            val p =
                                Pattern.compile("[^/]*?(\\.[^/]+)+", Pattern.CASE_INSENSITIVE)
                            val matcher = p.matcher(url)
                            matcher.find()
                            host = matcher.group()
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        conn.setRequestProperty("host", host)
                        conn.setRequestProperty("User-Agent", UserAgent)
                    }

                    override fun complete() {}

                    override fun success(result: String, conn: HttpURLConnection) {
                        val _json: JsonElement = JsonObject()
                        _json["Code"] = 0
                        _json["Message"] = ""
                        _json["Result"] = result
                        context.runOnUiThread {
                            webView.evaluateJavascript("window.NavigatorCallback.runCallback($callbackID,${_json.toString()})") {

                            }
                        }
                    }

                    override fun error(Code: Int, Mes: String, conn: HttpURLConnection) {
                        context.runOnUiThread {
                            webView.evaluateJavascript("window.NavigatorCallback.runCallback($errID,\"$Mes\")") {

                            }
                        }
                    }
                }
            )
        }

        companion object {
            fun getEncoding(str: String, _encode: String): String {
                var encode = "GB2312"
                try {
                    if (str == String(str.toByteArray(charset(encode)), charset(encode))) {
                        //判断是不是GB2312
                        return Tools.ConvertCharacter(str, encode, _encode)      //是的话，返回“GB2312“，以下代码同理
                    }
                } catch (exception: Exception) {
                }

                encode = "ISO-8859-1"
                try {
                    if (str == String(str.toByteArray(charset(encode)), charset(encode))) {
                        //判断是不是ISO-8859-1
                        return Tools.ConvertCharacter(str, encode, _encode)
                    }
                } catch (exception1: Exception) {
                }

                encode = "UTF-8"
                try {
                    if (str == String(str.toByteArray(charset(encode)), charset(encode))) {
                        //判断是不是UTF-8
                        return Tools.ConvertCharacter(str, encode, _encode)
                    }
                } catch (exception2: Exception) {
                }

                encode = "GBK"
                try {
                    if (str == String(str.toByteArray(charset(encode)), charset(encode))) {
                        //判断是不是GBK
                        return Tools.ConvertCharacter(str, encode, _encode)
                    }
                } catch (exception3: Exception) {
                }

                return str
            }
        }
    }
}
