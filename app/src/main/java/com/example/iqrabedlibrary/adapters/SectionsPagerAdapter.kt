package com.example.iqrabedlibrary.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.fragments.StudentsFragment
import com.example.iqrabedlibrary.fragments.TeachersFragment
import java.lang.IllegalArgumentException

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = when (position) {
            0 -> TeachersFragment()
            1 -> StudentsFragment()
            else -> throw IllegalArgumentException()
        }


    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}