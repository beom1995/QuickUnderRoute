package kr.ac.myungji.quickunderroute

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        val appContext: Context = MyApp.getApplicationContext()
        var prefs: PreferenceUtil = PreferenceUtil(appContext)
    }
}
