package com.citconpay.sdk.ui.base

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() {

    private lateinit var mActivity: BaseActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            mActivity = context
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity.mCurrentFragment = this
    }

    open fun onBackPressed() = false

}