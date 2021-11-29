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
    lateinit var btnStopOverST:Button
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
        btnStopOverST = findViewById(R.id.btnStopOverST)
        btnArrivalST = findViewById(R.id.btnArrivalST)
        btnFavorites = findViewById(R.id.btnFavorites)

        val no = getIntent().getStringExtra("no")

        //Toast.makeText(this, no.toString(), Toast.LENGTH_SHORT).show()

        //역 배열
        var array = arrayOf(arrayOf("101","102","103","104","105","106","107","108","109","110","111","112","113","114","115","116","117","118","119","120","121","122","123"),
            arrayOf("101","201","202","203","204","205","206","207","208","209","210","211","212","213","214","215","216","217"),
            arrayOf("207","301","302","303","304","123","305","306","307","308","107"),
            arrayOf("104","401","307","402","403","404","405","406","407","115","408","409","410","411","412","413","414","415","416","417","216"),
            arrayOf("209","501","502","503","504","122","505","506","403","507","109"),
            arrayOf("601","602","121","603","604","605","606","116","607","608","609","412","610","611","612","613","614","615","616","417","617","618","619","620","621","622"),
            arrayOf("202","303","503","601","701","702","703","704","705","706","416","707","614"),
            arrayOf("113","801","802","803","409","608","804","805","806","705","618","214"),
            arrayOf("112","901","406","605","902","119","903","702","904","621","211"))

        var previousST = no.toString() //이전역
        var afterST = no.toString() //이후역

        //배열 위치
        var STnum = arrayOf(0,0);
        var STnum2 = arrayOf(0,0);
        var numSTLine = 0


        var noText = no.toString()

        var nowST = noText //현재역


        //배열에서 역 위치 찾기
        for(i in 0..(array.size-1)){
            for(j in 0..(array[i].size-1)){
                if(noText == array[i][j]){
                    STnum[numSTLine] = i;
                    STnum2[numSTLine] = j;
                    numSTLine++;
                }
            }
        }

        //초기 설정
        var STline = (STnum[0]+1).toString()

        if(STnum2[0]-1 < 0){
            if(STnum[0] ==0 || STnum[0] == 5) {
                previousST = array[STnum[0]][array[STnum[0]].size - 1]
                afterST = array[STnum[0]][STnum2[0] + 1]
            }else{
                previousST = "000"
                afterST = array[STnum[0]][STnum2[0] + 1]
            }
        }else if(STnum2[0]+2 > array[STnum[0]].size){
            if(STnum[0] ==0 || STnum[0] == 5) {
                previousST = array[STnum[0]][STnum2[0] - 1]
                afterST = array[STnum[0]][0]
            }else{
                previousST = array[STnum[0]][STnum2[0] - 1]
                afterST = "000"
            }
        }else{
            previousST = array[STnum[0]][STnum2[0]-1]
            afterST = array[STnum[0]][STnum2[0]+1]
        }


        btnLine1.setText(STline)
        TextPreviousST.setText(previousST)
        TextAfterST.setText(afterST)

        SetButtonColor(STline.toInt(), btnLine1)
        btnLine2.setBackgroundColor(Color.parseColor("white"))

        var STline2 = (STnum[1]+1).toString()

        if(numSTLine == 2){
            btnLine2.visibility = View.VISIBLE
            btnLine2.setText(STline2.toString())
        }
//
        TextPreviousST.setText(CheckEnd(previousST))
        TextAfterST.setText(CheckEnd(afterST))
//
          btnLine1.setOnClickListener(View.OnClickListener {
              if(STnum2[0]-1 < 0){
                  if(STnum[0] ==0 || STnum[0] == 5) {
                      previousST = array[STnum[0]][array[STnum[0]].size - 1]
                      afterST = array[STnum[0]][STnum2[0] + 1]
                  }else{
                      previousST = "000"
                      afterST = array[STnum[0]][STnum2[0] + 1]
                  }
              }else if(STnum2[0]+2 > array[STnum[0]].size){
                  if(STnum[0] ==0 || STnum[0] == 5) {
                      previousST = array[STnum[0]][STnum2[0] - 1]
                      afterST = array[STnum[0]][0]
                  }else{
                      previousST = array[STnum[0]][STnum2[0] - 1]
                      afterST = "000"
                  }
              }else{
                  previousST = array[STnum[0]][STnum2[0]-1]
                  afterST = array[STnum[0]][STnum2[0]+1]
              }

              TextPreviousST.setText(previousST)
              TextAfterST.setText(afterST)
              SetButtonColor(STline.toInt(), btnLine1)
              btnLine2.setBackgroundColor(Color.parseColor("white"))
          })

        btnLine2.setOnClickListener(View.OnClickListener {
            if(STnum2[1]-1 < 0){
                if(STnum[1] ==0 || STnum[1] == 5) {
                    previousST = array[STnum[1]][array[STnum[1]].size - 1]
                    afterST = array[STnum[1]][STnum2[1] + 1]
                }else{
                    previousST = "000"
                    afterST = array[STnum[1]][STnum2[1] + 1]
                }
            }else if(STnum2[1]+2 > array[STnum[1]].size){
                if(STnum[1] ==0 || STnum[1] == 5) {
                    previousST = array[STnum[1]][STnum2[1] - 1]
                    afterST = array[STnum[1]][1]
                }else{
                    previousST = array[STnum[1]][STnum2[1] - 1]
                    afterST = "000"
                }
            }else{
                previousST = array[STnum[1]][STnum2[1]-1]
                afterST = array[STnum[1]][STnum2[1]+1]
            }

            TextPreviousST.setText(CheckEnd(previousST))
            TextAfterST.setText(CheckEnd(afterST))
            SetButtonColor(STline2.toInt(), btnLine2)
            btnLine1.setBackgroundColor(Color.parseColor("white"))
        })

//        //현재 역
          TextNowST.setText(nowST)

    }

    fun CheckEnd(str: String):String{
        var EndST = "a"
        if(str == "000"){
            EndST = "없음"
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