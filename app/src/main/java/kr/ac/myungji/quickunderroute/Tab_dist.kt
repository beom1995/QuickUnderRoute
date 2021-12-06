package kr.ac.myungji.quickunderroute

import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kr.ac.myungji.quickunderroute.R

class Tab_dist : Fragment() {
    var infoTime2: TextView? = null
    var infoFare2: TextView? = null
    var infoTrans2: TextView? = null
    var time: String? = null
    var fare: String? = null
    var trans: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_tab_dist, container, false)
        infoTime2 = v.findViewById<View>(R.id.info_time2) as TextView
        infoFare2 = v.findViewById<View>(R.id.info_fare2) as TextView
        infoTrans2 = v.findViewById<View>(R.id.info_trans2) as TextView
        val bundle = arguments
        if (bundle != null) {
            time = bundle.getString("time")
            fare = bundle.getString("fare")
            trans = bundle.getString("trans")
            infoTime2!!.text = time
            infoFare2!!.text = fare
            infoTrans2!!.text = trans
        }
        return v
    }
}