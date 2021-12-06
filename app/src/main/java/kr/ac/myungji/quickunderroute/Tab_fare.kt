package kr.ac.myungji.quickunderroute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class Tab_fare : Fragment() {
    var infoTime3: TextView? = null
    var infoFare3: TextView? = null
    var infoTrans3: TextView? = null
    var time: String? = null
    var fare: String? = null
    var trans: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_tab_fare, container, false)
        infoTime3 = v.findViewById<View>(R.id.info_time3) as TextView
        infoFare3 = v.findViewById<View>(R.id.info_fare3) as TextView
        infoTrans3 = v.findViewById<View>(R.id.info_trans3) as TextView
        val bundle = arguments
        if (bundle != null) {
            time = bundle.getString("time")
            fare = bundle.getString("fare")
            trans = bundle.getString("trans")
            infoTime3!!.text = time
            infoFare3!!.text = fare
            infoTrans3!!.text = trans
        }
        return v
    }
}