package kr.ac.myungji.quickunderroute.activity

import android.annotation.SuppressLint
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.*
import kr.ac.myungji.quickunderroute.AppDatabase
import kr.ac.myungji.quickunderroute.DatabaseCopier
import kr.ac.myungji.quickunderroute.R
import kr.ac.myungji.quickunderroute.activity.*
import kr.ac.myungji.quickunderroute.entity.RoomStation
import java.lang.Runnable
import java.util.*
import java.util.Arrays.*


// context 공유를 위해
class MyApp: Application() {
    lateinit var context: Context

    init{
        instance = this
    }

    companion object {
        private var instance: MyApp? = null
        fun getApplicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val TIME: Int = 0
        const val DIST: Int = 1
        const val FARE: Int = 2
    }

    // UI 관련 변수
    private lateinit var webView: WebView
    private var toolbar: Toolbar? = null
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    
    // 로직 관련 변수
    private lateinit var job: Job
    private var db: AppDatabase? = null
    private var stationList: List<RoomStation>? = null
    private var stNo: Array<String> = Array<String>(112) { "a" }

    // MainActivity가 생성될 때
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 어플 최초 구동 확인 및 상태 저장
        val pref = getSharedPreferences("pref", MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", false)
        if (!first) {
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.putInt("num", 0)
            editor.putInt("timeDelete",0)
            editor.commit()
        }

        // 액션바(타이틀바) 없애기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        // drawerlayout 사용을 위한 toolbar 설정
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        // 메뉴버튼 클릭동작
        var btnMenu: ImageView = findViewById(R.id.btn_menu)
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 메뉴 네비게이션 설정
        navigationView = findViewById(R.id.main_navi)
        navigationView.setNavigationItemSelectedListener(this)

        // 지하철 노선도 연결
        webView = findViewById(R.id.web_view)        // html로 UI를 구현하기 위해서 사용
        webView.addJavascriptInterface(WebViewConnector(this), "Android")

        webView.apply {
            webViewClient = WebViewClientClass() // 클릭시 새창 안뜨게

            // WebView에서 별다른 설정없이도 크롬실행 가능하나 팝업 호출은 불가능해서 가능하게 함
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
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

            // WebView 설정모음
            settings.javaScriptEnabled = true   //자바스크립트실행가능
            settings.setSupportMultipleWindows(true)    //새창생성허용
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true

        }

        webView.loadUrl("file:///android_asset/UI/subwayMap.html")

        // 스레드 관련 처리
        job = CoroutineScope(Dispatchers.IO).launch{
            DatabaseCopier.copyAttachedDatabase(context = applicationContext)
        }

        runBlocking {
            job.join()
        }

        db = DatabaseCopier.getAppDataBase(context = applicationContext)

        // 데이터베이스를 사용하는 작업은 메인스레드가 아닌 별도 스레드에서
        val r = Runnable {
            // 역정보 추출
            stationList = db!!.roomStationDao().getAll()

            if(stationList != null) {
                for (i in stationList!!.indices) {
                    stNo[i] = stationList!![i].no.toString()
                }
            }
        }
        val thread = Thread(r)
        thread.start()

        // 자동완성 연결
        var autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, stNo)
        autoCompleteTextView.setAdapter(adapter)
        
        // 검색버튼 클릭동작
        var btnSearch: ImageView = findViewById(R.id.btn_search)
        btnSearch.setOnClickListener {
            val searchNo = autoCompleteTextView.text.toString()
            val intent = Intent(this, StationActivity::class.java)
            for (i in stationList!!.indices) {
                if (searchNo == stNo[i]) {
                    intent.putExtra("no", searchNo)
                    startActivity(intent)
                }
            }
        }

    }

    // 토스트 메시지 띄우기
    fun displayMessage(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // 툴바 메뉴 버튼이 클릭 됐을 때 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when(item.itemId){
            R.id.main_navi -> {
                // 네비게이션 드로어 열기
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 메뉴 네비게이션 목록과 선택 시 기능
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btn_fav -> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_notice -> {
                val intent = Intent(this, NoticeActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_lost -> {
                val intent = Intent(this, LostActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_settings -> {
                displayMessage("settings selected")
            }
        }
        drawerLayout.closeDrawers()
        return false
    }

    // 드로어가 열려있을 때 뒤로가기 누르면 앱 종료안하고 드로어 닫기
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }



    // 슬라이딩업패널 리스너
    inner class PanelEventListener : SlidingUpPanelLayout.PanelSlideListener {
        // 패널이 슬라이드 중일 때
        override fun onPanelSlide(panel: View?, slideOffset: Float) {
        }

        // 패널의 상태가 변했을 때
        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
        }
    }

    // 웹뷰에서 홈페이지를 띄웠을때 새창이 아닌 기존창에서 실행이 되도록 아래 코드를 넣어준다.
    inner class WebViewClientClass : WebViewClient() {
        //페이지 이동
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith("app://")) {
                val intent = Intent(this@MainActivity, StationActivity::class.java)
                val no = url.substring(6, 9)//바꿈
                Log.d("webview", no)
                intent.putExtra("no", no)
                startActivity(intent)
            }
            else {
                view.loadUrl(url)
            }
            return true
        }

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

class WebViewConnector(val context: Context) : AppCompatActivity(){

    @JavascriptInterface
    fun getStationInfo(no: String) {
        val intent = Intent(context, StationActivity::class.java)
        intent.putExtra("no", no)
        startActivity(intent)
    }

}