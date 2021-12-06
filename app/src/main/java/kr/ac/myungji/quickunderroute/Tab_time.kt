package kr.ac.myungji.quickunderroute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class Tab_time : Fragment() {
    var infoTime1: TextView? = null
    var infoFare1: TextView? = null
    var infoTrans1: TextView? = null
    var time: String? = null
    var fare: String? = null
    var trans: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_tab_time, container, false)
        infoTime1 = v.findViewById<View>(R.id.info_time1) as TextView
        infoFare1 = v.findViewById<View>(R.id.info_fare1) as TextView
        infoTrans1 = v.findViewById<View>(R.id.info_trans1) as TextView
        val bundle = arguments
        if (bundle != null) {
            time = bundle.getString("time")
            fare = bundle.getString("fare")
            trans = bundle.getString("trans")
            infoTime1!!.text = time
            infoFare1!!.text = fare
            infoTrans1!!.text = trans
        }
        return v
    }
}