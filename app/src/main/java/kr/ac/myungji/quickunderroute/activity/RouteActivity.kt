package kr.ac.myungji.quickunderroute.activity

import kr.ac.myungji.quickunderroute.activity.MainActivity.Companion.TIME
import kr.ac.myungji.quickunderroute.activity.MainActivity.Companion.DIST
import kr.ac.myungji.quickunderroute.activity.MainActivity.Companion.FARE
import kr.ac.myungji.quickunderroute.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class RouteActivity : AppCompatActivity() {
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    private var routeCompute: RouteComputing = RouteComputing()
    private var infoArrAll: Array<Array<Int>>? = null   // 계산된 경로 정보
    private var infoRoute: ArrayList<ArrayList<Int>>? = null   // 계산된 경로 역 목록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        val fragTime: Fragment = TabTime()
        val fragDist: Fragment = TabDist()
        val fragFare: Fragment = TabFare()
        val bundleTime = Bundle()
        val bundleDist = Bundle()
        val bundleFare = Bundle()

        // 부속 화면(fragment)
//        val fragTime = layoutInflater.inflate(R.layout.fragment_tab_time, null, false)
//        val fragDist = layoutInflater.inflate(R.layout.fragment_tab_dist, null, false)
//        val fragFare = layoutInflater.inflate(R.layout.fragment_tab_fare, null, false)

        // 정보를 표시할 화면 요소
//        var infoEstiTime1: TextView = fragTime.findViewById(R.id.info_time1)
//        var infoEstiTime2: TextView = fragDist.findViewById(R.id.info_time2)
//        var infoEstiTime3: TextView = fragFare.findViewById(R.id.info_time3)
//        var infoFare1: TextView = fragTime.findViewById(R.id.info_fare1)
//        var infoFare2: TextView = fragDist.findViewById(R.id.info_fare2)
//        var infoFare3: TextView = fragFare.findViewById(R.id.info_fare3)
//        var infoTrans1: TextView = fragTime.findViewById(R.id.info_trans1)
//        var infoTrans2: TextView = fragDist.findViewById(R.id.info_trans2)
//        var infoTrans3: TextView = fragFare.findViewById(R.id.info_trans3)

        // 경로 검색 정보 받아오기
        var src: Int = MyApplication.prefs.getInt("src", 0)
        var dstn: Int = MyApplication.prefs.getInt("dstn", 0)
        var via = if(MyApplication.prefs.getString("via", null) != null){
            MyApplication.prefs.getString("via", null)!!.toInt()
        } else {
            null
        }

        // 경로 검색 정보 초기화
        MyApplication.prefs.setInt("src", 0)
        MyApplication.prefs.setString("via", null)
        MyApplication.prefs.setInt("dstn", 0)

        val r = Runnable {
            infoArrAll = routeCompute.dijkstra(src, via, dstn)
            infoRoute = routeCompute.getRoute()

            if(infoArrAll != null){
                for(i in 0 until 3) {
                    for(j in 0 until 4) {
                        Log.d("infoArrAll[${i}][${j}]", "${infoArrAll!![i][j]}")
                    }
                }
            }
        }
        val thread = Thread(r)
        thread.start()

        // tabLayout에서 viewpaging 사용
        var vp: ViewPager = findViewById(R.id.view_pager)
        var adapter: VPAdapter = VPAdapter(supportFragmentManager)

        adapter.addFragment(fragTime, "최단시간")
        adapter.addFragment(fragDist, "최단거리")
        adapter.addFragment(fragFare, "최소요금")

        vp.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(vp)

        // 각 탭별 화면 설정
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position){
                    TIME -> {
                        tab.select()
                        vp.currentItem = TIME

                        val time: String = secToMin(infoArrAll!![TIME][0])
                        val dist: String = "${infoArrAll!![TIME][1]}"
                        val fare: String = "${infoArrAll!![TIME][2]}"
                        val trans: String = "${infoArrAll!![TIME][3]}"

                        var routeString: String? = null

                        for(e in infoRoute?.get(TIME)!!.indices) {
                            routeString.plus("${infoRoute!![TIME][e]}"+"\n")
                        }

                        MyApplication.prefs.setString("time1", time)
                        MyApplication.prefs.setString("dist1", dist)
                        MyApplication.prefs.setString("fare1", fare)
                        MyApplication.prefs.setString("trans1", trans)
                        MyApplication.prefs.setString("route1", routeString)
                    }
                    DIST -> {
                        tab.select()
                        vp.currentItem = DIST

                        val time: String = secToMin(infoArrAll!![DIST][0])
                        val dist: String = "${infoArrAll!![DIST][1]}"
                        val fare: String = "${infoArrAll!![DIST][2]}"
                        val trans: String = "${infoArrAll!![DIST][3]}"

                        var routeString: String? = null

                        for(e in infoRoute?.get(DIST)!!.indices) {
                            routeString.plus("${infoRoute!![DIST][e]}"+"\n")
                        }

                        MyApplication.prefs.setString("time2", time)
                        MyApplication.prefs.setString("dist2", dist)
                        MyApplication.prefs.setString("fare2", fare)
                        MyApplication.prefs.setString("trans2", trans)
                        MyApplication.prefs.setString("route2", routeString)
                    }
                    FARE -> {
                        tab.select()
                        vp.currentItem = FARE

                        val time: String = secToMin(infoArrAll!![FARE][0])
                        val dist: String = "${infoArrAll!![FARE][1]}"
                        val fare: String = "${infoArrAll!![FARE][2]}"
                        val trans: String = "${infoArrAll!![FARE][3]}"

                        var routeString: String? = null

                        for(e in infoRoute?.get(FARE)!!.indices) {
                            routeString.plus("${infoRoute!![FARE][e]}"+"\n")
                        }

                        MyApplication.prefs.setString("time3", time)
                        MyApplication.prefs.setString("dist3", dist)
                        MyApplication.prefs.setString("fare3", fare)
                        MyApplication.prefs.setString("trans3", trans)
                        MyApplication.prefs.setString("route3", routeString)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })

        // 하차알림
        val btnGetoffAlarm: FloatingActionButton = findViewById(R.id.btn_getoff)
        var isAlarmSet = false

        btnGetoffAlarm.setOnClickListener {
            if (isAlarmSet) {
                cancelAlarm()
                btnGetoffAlarm.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F2E2C6"))
                isAlarmSet = false
            } else {
                setAlarm()
                btnGetoffAlarm.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF0000"))
                isAlarmSet = true
            }
        }

        // 도착시간 공유
        val btnSendText: Button = findViewById(R.id.btn_share)
        btnSendText.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${secToMin(infoArrAll!![0][0])} 뒤에 ${dstn}역에 도착 예정입니다.")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    // 초를 시/분 단위로 환산, 초단위 절삭
    private fun secToMin(sec: Int): String {
        var min: Int = sec / 60
        var hour: Int = min / 60

        min %= 60

        return if(hour > 0) {
            hour.toString() + "시간 " + min.toString() + "분"
        } else {
            min.toString() + "분"
        }
    }

    // 하차 알림을 설정한다.
    private fun setAlarm() {
        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        alarmMgr?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + (infoArrAll!![0][0] - 180) * 1000,
            alarmIntent
        )
    }

    // 하차알림을 취소한다.
    private fun cancelAlarm() {
        alarmMgr?.cancel(alarmIntent)
    }
}