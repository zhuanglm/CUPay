package com.citconpay.sdk.ui.base

import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {
    lateinit var mCurrentFragment: BaseFragment
    companion object {
        const val DROP_IN_REQUEST = 1
    }

    override fun onBackPressed() {
        if(!mCurrentFragment.onBackPressed())
            super.onBackPressed()
    }

}