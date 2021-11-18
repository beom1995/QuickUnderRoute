package kr.ac.myungji.quickunderroute

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import androidx.room.Room
import kotlinx.coroutines.*
import kr.ac.myungji.quickunderroute.entity.RoomStation
import java.lang.Runnable

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var job: Job
    private var db: AppDatabase? = null

    // MainActivity가 생성될 때
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)        // html로 UI를 구현하기 위해서 사용

        job = CoroutineScope(Dispatchers.IO).launch{
            DatabaseCopier.copyAttachedDatabase(context = applicationContext)
        }

        runBlocking {
            job.join()
        }

        db = DatabaseCopier.getAppDataBase(context = applicationContext)


        //데이터베이스를 자동완성과 연결
        val r = Runnable {
            RouteComputing(db).dijkstra(101,null,307)
            val stationList: List<RoomStation>? = db!!.roomStationDao().getAll()

            var arr1 = Array<String>(112,{"a"})

            if (stationList != null) {
                for (i in stationList.indices) {
                    arr1[i] = stationList[i].no.toString()
                }
            }

            //자동완성 연결
            var items = arr1
            var autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
            var adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, items)
            autoCompleteTextView.setAdapter(adapter)

        }

        val thread = Thread(r)
        thread.start()

        webView.apply {
            webViewClient = WebViewClientClass() // 클릭시 새창 안뜨게

            // WebView에서 별다른 설정없이도 크롬실행 가능하나 팝업 호출은 불가능함
            // 가능하게 하기 위한 작업
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                    val newWebView = WebView(this@MainActivity).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(this@MainActivity).apply {
                        setContentView(newWebView)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                        show()
                    }

                    newWebView.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }

                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }
            }

//            webView.addJavascriptInterface(WebAppInterface(this), "Android")


            //WebView 설정모음
            settings.javaScriptEnabled = true   //자바스크립트실행가능
            settings.setSupportMultipleWindows(true)    //새창생성허용
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true
            settings.displayZoomControls = true
        }

        webView.loadUrl("file:///android_asset/UI/subwayMap.html")


        //임시 자동완성 코드
        /*
        var items = arrayOf("101","102","103","104")//arr1//임시 데이터
        var autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextView.setAdapter(adapter)
        */




        //메뉴
        var layMenu:LinearLayout = findViewById(R.id.LayMenu)
        var btnMenu:Button = findViewById(R.id.BtnMenu)
        var btnNum:Boolean = true;

        btnMenu.setOnClickListener {
            if(btnNum == true) {
                layMenu.visibility = View.VISIBLE
                btnNum = false;
            }else{
                layMenu.visibility = View.INVISIBLE
                btnNum = true;
            }
        }

        //즐겨찾기 화면 전환
        var btnFav:Button = findViewById(R.id.BtnFav)

        btnFav.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        //유실물 화면 전환
        var btnLost: Button = findViewById(R.id.BtnLost)

        btnLost.setOnClickListener {
            val intent = Intent(this, LostActivity::class.java)
            startActivity(intent)
        }

    }





    //웹뷰에서 홈페이지를 띄웠을때 새창이 아닌 기존창에서 실행이 되도록 아래 코드를 넣어준다.
    inner class WebViewClientClass : WebViewClient() {
        //페이지 이동
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
/*
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mProgressBar.visibility = ProgressBar.VISIBLE
            webView.visibility = View.INVISIBLE
        }

        override fun onPageCommitVisible(view: WebView, url: String) {
            super.onPageCommitVisible(view, url)
            mProgressBar.visibility = ProgressBar.GONE
            webView.visibility = View.VISIBLE
        }
*/

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            var builder: android.app.AlertDialog.Builder =
                android.app.AlertDialog.Builder(this@MainActivity)
            var message = "SSL Certificate error."
            when (error.primaryError) {
                SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                SslError.SSL_EXPIRED -> message = "The certificate has expired."
                SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
            }
            message += " Do you want to continue anyway?"
            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue",
                DialogInterface.OnClickListener { _, _ -> handler.proceed() })
            builder.setNegativeButton("cancel",
                DialogInterface.OnClickListener { dialog, which -> handler.cancel() })
            val dialog: android.app.AlertDialog? = builder.create()
            dialog?.show()
        }
    }


    // MainActivity가 종료될 때
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}