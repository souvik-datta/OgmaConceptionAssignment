package com.souvik.assignmentogma.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.souvik.assignmentogma.HomeFragment
import com.souvik.assignmentogma.MapsFragment

class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // tab titles
    private val tabTitles = arrayOf("HOME", "MAP")

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0->{
                return HomeFragment()
            }
            1->{
                return MapsFragment()
            }
            else->{
                return HomeFragment()
            }
        }
    }
}