package com.basiatish.stocks.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.basiatish.stocks.ui.fragments.authentication.FirstPageFragment
import com.basiatish.stocks.ui.fragments.authentication.SecondPageFragment
import com.basiatish.stocks.ui.fragments.authentication.ThirdPageFragment

class ViewPagerAdapter(fragmentActivity: Fragment) : FragmentStateAdapter(fragmentActivity) {


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstPageFragment()
            1 -> SecondPageFragment()
            else -> ThirdPageFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}