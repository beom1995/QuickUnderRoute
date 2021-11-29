package kr.ac.myungji.quickunderroute.activity

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.myungji.quickunderroute.R


class StationActivity : AppCompatActivity() {
    lateinit var btnLine1:Button
    lateinit var btnLine2:Button
    lateinit var TextPreviousST:TextView
    lateinit var TextNowST:TextView
    lateinit var TextAfterST:TextView
    lateinit var btnStartST:Button
    lateinit var btnArrivalST:Button
    lateinit var btnFavorites:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station)

        btnLine1 = findViewById(R.id.btnLine1)
        btnLine2 = findViewById(R.id.btnLine2)
        TextPreviousST = findViewById(R.id.textPreviousST)
        TextNowST = findViewById(R.id.textNowST)
        TextAfterST = findViewById(R.id.textAfterST)
        btnStartST = findViewById(R.id.btnStartST)
        btnArrivalST = findViewById(R.id.btnArrivalST)
        btnFavorites = findViewById(R.id.btnFavorites)

        val no = getIntent().getStringExtra("no")
        var noText = no.toString()
        var STline = noText.substring(3, 4)
        var STline2 = noText.substring(10, 11).toInt()
        var nowST = noText.substring(0, 3)
        var previousST = noText.substring(4, 7)
        var afterST = noText.substring(7, 10)


        btnLine1.setText(STline)
        TextPreviousST.setText(previousST)
        TextAfterST.setText(afterST)

        SetButtonColor(STline.toInt(), btnLine1)
        btnLine2.setBackgroundColor(Color.parseColor("white"))

        if(STline2 != 0){
            btnLine2.visibility = View.VISIBLE
            btnLine2.setText(STline2.toString())
        }

        btnLine1.setOnClickListener(View.OnClickListener {
            previousST = noText.substring(4, 7)
            afterST = noText.substring(7, 10)
            TextPreviousST.setText(CheckEnd(previousST))
            TextAfterST.setText(CheckEnd(afterST))
            SetButtonColor(STline.toInt(), btnLine1)
            btnLine2.setBackgroundColor(Color.parseColor("white"))
        })

        btnLine2.setOnClickListener(View.OnClickListener {
            previousST = noText.substring(11, 14)
            afterST = noText.substring(14, 17)
            TextPreviousST.setText(CheckEnd(previousST))
            TextAfterST.setText(CheckEnd(afterST))
            SetButtonColor(STline2, btnLine2)
            btnLine1.setBackgroundColor(Color.parseColor("white"))
        })

        //현재 역
        TextNowST.setText(nowST)


        Toast.makeText(this, no.toString(), Toast.LENGTH_SHORT).show()

    }

    fun CheckEnd(str: String):String{
        var EndST = "a"
        if(str == "000"){
            EndST = "종착"
        }else{
            EndST = str
        }
        return EndST
    }

    fun SetTextColor(Linenum: Int, text: TextView){
        when(Linenum){
            1 -> text.setBackgroundColor(Color.parseColor("#ff4747"))
            2 -> text.setBackgroundColor(Color.parseColor("#ff8e45"))
            3 -> text.setBackgroundColor(Color.parseColor("#ffbf4a"))
            4 -> text.setBackgroundColor(Color.parseColor("#b7f35a"))
            5 -> text.setBackgroundColor(Color.parseColor("#6aebad"))
            6 -> text.setBackgroundColor(Color.parseColor("#66dff4"))
            7 -> text.setBackgroundColor(Color.parseColor("#738cfc"))
            8 -> text.setBackgroundColor(Color.parseColor("#9e77f8"))
            9 -> text.setBackgroundColor(Color.parseColor("#f387c1"))
        }
    }

    fun SetButtonColor(Linenum: Int, btn: Button){
        when(Linenum){
            1 -> btn.setBackgroundColor(Color.parseColor("#ff4747"))
            2 -> btn.setBackgroundColor(Color.parseColor("#ff8e45"))
            3 -> btn.setBackgroundColor(Color.parseColor("#ffbf4a"))
            4 -> btn.setBackgroundColor(Color.parseColor("#b7f35a"))
            5 -> btn.setBackgroundColor(Color.parseColor("#6aebad"))
            6 -> btn.setBackgroundColor(Color.parseColor("#66dff4"))
            7 -> btn.setBackgroundColor(Color.parseColor("#738cfc"))
            8 -> btn.setBackgroundColor(Color.parseColor("#9e77f8"))
            9 -> btn.setBackgroundColor(Color.parseColor("#f387c1"))
        }
    }
}