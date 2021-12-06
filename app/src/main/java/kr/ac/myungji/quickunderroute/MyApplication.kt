package kr.ac.myungji.quickunderroute

import android.app.Application
import android.content.Context
import kr.ac.myungji.quickunderroute.activity.MyApp

class MyApplication : Application() {
    companion object {
        private val appContext: Context = MyApp.getApplicationContext()
        var prefs: PreferenceUtil = PreferenceUtil(appContext)
    }
}
