package kr.ac.myungji.quickunderroute.activity

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
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kr.ac.myungji.quickunderroute.*

class RouteActivity : AppCompatActivity() {
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    private var routeCompute: RouteComputing = RouteComputing()
    private var infoArrAll: Array<Array<Int>>? = null   // 계산된 경로 정보

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        // 부속 화면(fragment)
        val fragTime = layoutInflater.inflate(R.layout.fragment_tab_time, null, false)
        val fragDist = layoutInflater.inflate(R.layout.fragment_tab_dist, null, false)
        val fragFare = layoutInflater.inflate(R.layout.fragment_tab_fare, null, false)

        // 정보를 표시할 화면 요소
        val infoEstiTime1: TextView = fragTime.findViewById(R.id.info_time1)
        val infoEstiTime2: TextView = fragDist.findViewById(R.id.info_time2)
        val infoEstiTime3: TextView = fragFare.findViewById(R.id.info_time3)
        val infoFare1: TextView = fragTime.findViewById(R.id.info_fare1)
        val infoFare2: TextView = fragDist.findViewById(R.id.info_fare2)
        val infoFare3: TextView = fragFare.findViewById(R.id.info_fare3)
        val infoTrans1: TextView = fragTime.findViewById(R.id.info_trans1)
        val infoTrans2: TextView = fragDist.findViewById(R.id.info_trans2)
        val infoTrans3: TextView = fragFare.findViewById(R.id.info_trans3)

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
        vp.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(vp)

        // 각 탭별 화면 설정
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position){
                    0 -> {
                        tab.select()
                        vp.currentItem = 0
                        infoEstiTime1.text = secToMin(infoArrAll!![0][0])
                        infoFare1.text = "${infoArrAll!![0][2]}"
                        infoTrans1.text = "${infoArrAll!![0][3]}"
                    }
                    1 -> {
                        tab.select()
                        vp.currentItem = 1
                        infoEstiTime2.text = secToMin(infoArrAll!![1][0])
                        infoFare2.text = "${infoArrAll!![1][2]}"
                        infoTrans2.text = "${infoArrAll!![1][3]}"
                    }
                    2 -> {
                        tab.select()
                        vp.currentItem = 2
                        infoEstiTime3.text = secToMin(infoArrAll!![2][0])
                        infoFare3.text = "${infoArrAll!![2][2]}"
                        infoTrans3.text = "${infoArrAll!![2][3]}"
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
                btnGetoffAlarm.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF0000"))
                isAlarmSet = false
            } else {
                setAlarm()
                btnGetoffAlarm.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C92424"))
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
        var min: Int
        var hour: Int

        min = sec / 60
        hour = min / 60
        min %= 60

        return hour.toString() + "시간 " + min.toString() + "분"
    }

    // 하차 알림을 설정한다.
    private fun setAlarm() {
        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        alarmMgr?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + infoArrAll!![0][0] * 1000,
            alarmIntent
        )
    }

    // 하차알림을 취소한다.
    private fun cancelAlarm() {
        alarmMgr?.cancel(alarmIntent)
    }
}