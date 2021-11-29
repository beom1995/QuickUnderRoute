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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.ac.myungji.quickunderroute.*

class RouteActivity : AppCompatActivity() {
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    private var routeCompute: RouteComputing = RouteComputing()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        var src: Int = 101
        var dstn: Int = 307

        // 정보를 표시할 화면 요소
        val infoArriveTime: TextView = findViewById(R.id.info_time)
        val infoFare: TextView = findViewById(R.id.info_fare)
        val infoTrans: TextView = findViewById(R.id.info_trans)

        // 계산된 경로 정보
        var infoArrAll: Array<Array<Int>>? = null

        val r = Runnable {
            infoArrAll = routeCompute.dijkstra(src, null, dstn)

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

        // 하차알림
        val btnGetoffAlarm: FloatingActionButton = findViewById(R.id.btn_getoff)
        var isAlarmSet = false

        btnGetoffAlarm.setOnClickListener {
            if (isAlarmSet) {
                cancelAlarm()
                btnGetoffAlarm.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8EBDD"))
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
                putExtra(Intent.EXTRA_TEXT, "${infoArrAll?.get(0)?.get(0)}분 뒤에 ${dstn}역에 도착 예정입니다.")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    // 하차알림을 취소한다.
    private fun secToMin() {
        // wait
    }

    // 하차 알림을 설정한다.
    private fun setAlarm() {
        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        alarmMgr?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60 * 1000,
            alarmIntent
        )
    }

    // 하차알림을 취소한다.
    private fun cancelAlarm() {
        alarmMgr?.cancel(alarmIntent)
    }
}