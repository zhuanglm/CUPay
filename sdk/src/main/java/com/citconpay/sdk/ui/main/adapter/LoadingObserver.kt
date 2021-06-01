package com.citconpay.sdk.ui.main.adapter

import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer

class LoadingObserver(progress: ProgressBar) : Observer<Boolean> {
    private val pb = progress
    /*private val dialog = KProgressHUD(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)*/

    override fun onChanged(show: Boolean?) {
        if (show == null) return
        if (show) {
            pb.visibility = View.VISIBLE
        } else {
            pb.visibility = View.INVISIBLE
        }
    }
}
