package kr.ac.myungji.quickunderroute

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import kotlinx.coroutines.*
import kr.ac.myungji.quickunderroute.activity.FavoritesActivity
import kr.ac.myungji.quickunderroute.activity.LostActivity
import kr.ac.myungji.quickunderroute.activity.RouteActivity
import kr.ac.myungji.quickunderroute.entity.RoomStation
import org.w3c.dom.Text
import java.lang.Runnable

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    // UI 관련
    private lateinit var webView: WebView
    private var toolbar: Toolbar? = null
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    
    // 로직 관련
    private lateinit var job: Job
    private var db: AppDatabase? = null
    private val stationList: List<RoomStation>? = null

    // MainActivity가 생성될 때
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // drawerlayout 사용을 위한 toolbar
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)


        var btnMenu: ImageView = findViewById(R.id.btn_menu)
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView = findViewById(R.id.main_navi)
        navigationView.setNavigationItemSelectedListener(this)

        // 지하철 노선도
        webView = findViewById(R.id.webView)        // html로 UI를 구현하기 위해서 사용

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

        job = CoroutineScope(Dispatchers.IO).launch{
            DatabaseCopier.copyAttachedDatabase(context = applicationContext)
        }

        runBlocking {
            job.join()
        }

        db = DatabaseCopier.getAppDataBase(context = applicationContext)

        // 데이터베이스를 사용하는 작업
        val r = Runnable {
            // 경로찾기
            RouteComputing(db).dijkstra(101, null, 307)

            // 역정보 추출
            val stationList: List<RoomStation>? = db!!.roomStationDao().getAll()
        }

        val thread = Thread(r)
        thread.start()

        var arr1 = Array<String>(112, { "a" })

        if (stationList != null) {
            for (i in stationList.indices) {
                arr1[i] = stationList[i].no.toString()
            }
        }

        //자동완성 연결
        var items = arr1
        var autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        var adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_dropdown_item_1line,
            items)
        autoCompleteTextView.setAdapter(adapter)


        /*
        // 메뉴
        var layMenu: LinearLayout = findViewById(R.id.LayMenu)
        var btnMenu: Button = findViewById(R.id.btn_menu)
        var btnNum: Boolean = true;

        btnMenu.setOnClickListener {
            if(btnNum) {
                layMenu.visibility = View.VISIBLE
                btnNum = false;
            }else{
                layMenu.visibility = View.INVISIBLE
                btnNum = true;
            }
        }

         */

        // 즐겨찾기 화면 전환
        var btnFav:Button = findViewById(R.id.btn_fav)

        btnFav.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        // 유실물 화면 전환
        var btnLost: Button = findViewById(R.id.btn_lost)

        btnLost.setOnClickListener {
            val intent = Intent(this, LostActivity::class.java)
            startActivity(intent)
        }

        // 루트 테스트 화면 전환
        var btnTest: Button = findViewById(R.id.btn_test)

        btnTest.setOnClickListener {
            val intent = Intent(this, RouteActivity::class.java)
            startActivity(intent)
        }

    }

    // 토스트 메시지 띄우기
    fun displayMessage(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // 툴바 메뉴 버튼이 클릭 됐을 때 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when(item!!.itemId){
            R.id.main_navi -> {
                // 네비게이션 드로어 열기
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_camera -> {
                displayMessage("camera selected")
            }
            R.id.nav_photo -> {
                displayMessage("photo selected")
            }
            R.id.nav_slideShow -> {
                displayMessage("slideshow selected")
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

    // 웹뷰에서 홈페이지를 띄웠을때 새창이 아닌 기존창에서 실행이 되도록 아래 코드를 넣어준다.
    inner class WebViewClientClass : WebViewClient() {
        //페이지 이동
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
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