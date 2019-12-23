package com.laka.shoppingchat.mvp.test.view.fragment

import android.os.Bundle
import android.view.View
import com.laka.androidlib.base.fragment.BaseFragment
import com.laka.shoppingchat.R

class TestFragment : BaseFragment() {

    companion object {
        fun getInstance(): TestFragment {
            return TestFragment()
        }
    }

    override fun setContentView(): Int {
        return R.layout.fragment_test
    }

    override fun initArgumentsData(arguments: Bundle?) {

    }

    override fun initView(rootView: View?, savedInstanceState: Bundle?) {

    }

    override fun initData() {

    }

    override fun initEvent() {

    }
}