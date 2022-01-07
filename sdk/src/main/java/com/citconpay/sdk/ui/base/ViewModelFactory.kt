package com.citconpay.sdk.ui.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citconpay.sdk.data.model.CPayRequest
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel

class ViewModelFactory(private val request: CPayRequest, private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DropinViewModel::class.java)) {
            return DropinViewModel(request, application) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}