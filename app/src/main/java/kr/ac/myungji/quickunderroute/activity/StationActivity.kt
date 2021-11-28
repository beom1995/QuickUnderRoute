package kr.ac.myungji.quickunderroute.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.ac.myungji.quickunderroute.R
import java.net.URI

class StationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station)

        val no = getIntent().getStringExtra("no")
        Toast.makeText(this, no.toString(), Toast.LENGTH_SHORT).show()


    }
}