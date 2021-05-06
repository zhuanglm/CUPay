package com.citconpay.sdk.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {
    lateinit var mCurrentFragment: BaseFragment
    companion object {
        const val DROP_IN_REQUEST = 1
    }
//    private lateinit var mAppbarConfiguration: AppBarConfiguration
//    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*mNavController = Navigation.findNavController(this, R.id.payment_nav_graph)
        mAppbarConfiguration = AppBarConfiguration.Builder(mNavController.graph).build()

        //将App bar与 NavController绑定起来
        NavigationUI.setupActionBarWithNavController(this,mNavController,mAppbarConfiguration)*/
    }

    override fun onBackPressed() {
        /*when {
            mCurrentFragment.onBackPressed() -> {
            }
            else -> super.onBackPressed()
        }*/
        if(!mCurrentFragment.onBackPressed())
            super.onBackPressed()
    }

}