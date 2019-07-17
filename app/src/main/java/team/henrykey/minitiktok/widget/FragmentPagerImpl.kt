/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.widget

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

class FragmentPagerImpl constructor(
    fm: FragmentManager,
    private val fragments: Array<Fragment>,
    private val titles: Array<String>
): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // super.destroyItem(container, position, object);
    }

    override fun getItemPosition(o: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}