package kr.ac.myungji.quickunderroute

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class VPAdapter: FragmentPagerAdapter {
    private var items: ArrayList<Fragment> = ArrayList<Fragment>()

    constructor(fm: FragmentManager) : super(fm) {
        items.add(tab_time())
        items.add(tab_dist())
        items.add(tab_fare())
    }

    @Override
    override fun getItem(position: Int): Fragment {
        return items[position]
    }

    @Override
    override fun getCount(): Int {
        return items.size
    }
}