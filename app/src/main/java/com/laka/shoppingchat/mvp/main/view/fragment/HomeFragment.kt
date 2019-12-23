package com.laka.shoppingchat.mvp.main.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.laka.androidlib.base.fragment.BaseLazyLoadFragment
import com.laka.shoppingchat.mvp.constant.MainConstant
import com.laka.shoppingchat.mvp.constant.MainConstant.HOMEPAGE_FRAGMENT_INDEX
import com.laka.shoppingchat.mvp.nim.fragment.ContactListFragment
import com.laka.shoppingchat.mvp.nim.fragment.SessionListFragment
import com.laka.shoppingchat.mvp.shopping.center.view.fragment.ShoppingHomeFragment
import com.laka.shoppingchat.mvp.test.view.fragment.TestFragment
import com.laka.shoppingchat.mvp.user.view.fragment.UserCenterFragment

/**
 * @Author:Rayman
 * @Date:2018/12/21
 * @Description:主页类型Fragment，统一使用懒加载的方式去加载数据 .
 * 主页的Fragment都需继承它。
 */
abstract class HomeFragment : BaseLazyLoadFragment() {

    /**
     * description:定义静态内存块
     **/
    companion object {

        /**
         * description:新建Fragment
         **/
        @JvmStatic
        fun newInstance(@MainConstant.HomePageFragmentType fragmentType: Int): Fragment {

            var bundle = Bundle()
            bundle.putInt(HOMEPAGE_FRAGMENT_INDEX, fragmentType)

            when (fragmentType) {
                MainConstant.HOMEPAGE_SHOPPING -> return ShoppingHomeFragment()
                MainConstant.HOMEPAGE_MINE -> return UserCenterFragment()
                MainConstant.HOMEPAGE_RECENT_CONTACT -> return SessionListFragment()
                MainConstant.HOMEPAGE_CONTRACT_LIST -> return ContactListFragment()
            }

            // 默认返回ShoppingHomeFragment()----
            return ShoppingHomeFragment()
//            return TestFragment()
        }
    }
}