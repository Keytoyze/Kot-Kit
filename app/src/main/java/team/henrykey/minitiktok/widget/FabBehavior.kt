/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {
    private var visible = true

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
                            child: FloatingActionButton, directTargetChild: View, target: View,
                            nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
            super.onStartNestedScroll(coordinatorLayout, child, directTargetChild,
            target, nestedScrollAxes)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout,
                       child: FloatingActionButton, target: View, dxConsumed: Int,
                       dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
            dxUnconsumed, dyUnconsumed)
        if (dyConsumed > 0 && visible) {
            visible = false
            onHide(child)
        } else if (dyConsumed < 0) {
            visible = true
            onShow(child)
        }

    }

    fun onHide(fab: FloatingActionButton) {
        fab.post {
            fab.animate()
                .translationY(fab.height.toFloat() +
                    (fab.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin)
                .interpolator = AccelerateInterpolator(3f)
        }

    }

    fun onShow(fab: FloatingActionButton) {
        fab.animate()
            .translationY(0f)
            .interpolator = DecelerateInterpolator(3f)
    }

}