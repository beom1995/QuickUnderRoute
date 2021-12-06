package kr.ac.myungji.quickunderroute

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class VPAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {
    private val items = ArrayList<Fragment>()
    private val titleList = ArrayList<String>()

    @Override
    override fun getItem(position: Int): Fragment {
        return items[position]
    }

    @Override
    override fun getCount(): Int {
        return items.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        items.add(fragment)
        titleList.add(title)
    }
}