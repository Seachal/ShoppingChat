package com.laka.shoppingchat.mvp.nim.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.main.view.fragment.HomeFragment
import com.laka.shoppingchat.mvp.nim.activity.AddFriendsActivity
import com.laka.shoppingchat.mvp.nim.activity.GlobalSearchActivity
import com.laka.shoppingchat.mvp.nim.constract.INimConstract
import com.laka.shoppingchat.mvp.nim.main.viewholder.FuncViewHolder
import com.laka.shoppingchat.mvp.nim.presenter.NimPresenter
import com.netease.nim.uikit.api.model.contact.ContactsCustomization
import com.netease.nim.uikit.business.contact.ContactsFragment
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem
import com.netease.nim.uikit.business.contact.core.viewholder.AbsContactViewHolder
import kotlinx.android.synthetic.main.fragment_contact_list.*

/**
 *  联系人列表
 */
class ContactListFragment : HomeFragment(), INimConstract.IBaseNimView {
    private lateinit var mNimPresenter: NimPresenter
    private lateinit var fragment: ContactsFragment
    override fun createPresenter(): IBasePresenter<*> {
        mNimPresenter = NimPresenter()
        return mNimPresenter
    }

    override fun setContentView(): Int = R.layout.fragment_contact_list

    override fun initArgumentsData(arguments: Bundle?) {

    }

    override fun initView(rootView: View?, savedInstanceState: Bundle?) {
        handleStatusBarOffset()
        addContactFragment()
        title_bar
            .setBackGroundColor(R.color.color_gray_bg)
            .setTitleTextColor(R.color.color_2d2d2d)
            .setTitleTextSize(18)
            .setRightIcon(R.drawable.seletor_add_friend)
            .showDivider(false)
            .setOnRightClickListener {
                AddFriendsActivity.start(context!!)
            }
        llSelect.onClick {
            GlobalSearchActivity.start(context)
        }
    }

    override fun initDataLazy() {

    }

    private fun handleStatusBarOffset() {
        val layoutParams = cl_title_root.layoutParams as? LinearLayout.LayoutParams
        layoutParams?.let {
            val statusBarHeight = StatusBarUtil.getStatusBarHeight(activity)
            it.height = ScreenUtils.dp2px(44f) + statusBarHeight
        }
    }

    override fun initEvent() {

    }

    private fun addContactFragment() {
        fragment = ContactsFragment()


        fragment.containerId = R.id.messages_fragment

//        val activity = activity as UI

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
//        fragment = activity.addFragment(fragment) as RecentContactsFragment
        var transition = fragmentManager!!.beginTransaction()
        transition.add(R.id.flcContraint, fragment)
        transition.show(fragment)
        transition.commit()
        // 功能项定制
        fragment.setContactsCustomization(object : ContactsCustomization {
            override fun onGetFuncViewHolderClass(): Class<out AbsContactViewHolder<out AbsContactItem>> {
                return FuncViewHolder::class.java
            }

            override fun onGetFuncItems(): List<AbsContactItem> {
                return FuncViewHolder.FuncItem.provide()
            }

            override fun onFuncItemClick(item: AbsContactItem) {
                FuncViewHolder.FuncItem.handle(activity, item)
            }
        })
//        DataCacheManager.buildDataCache()
    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        FuncViewHolder.unRegisterUnreadNumChangedCallback()
    }

    fun reloadData() {
        if (::fragment.isInitialized) {
            fragment.reload()
        }
    }
}