package com.citconpay.sdk.ui.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citconpay.sdk.data.repository.ApiRepository
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel

class ViewModelFactory(private val apiRepository: ApiRepository, private val application: Application) : ViewModelProvider.Factory {
    val api by lazy { apiRepository }
    val app by lazy { application }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DropinViewModel::class.java)) {
            return DropinViewModel(api,app) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}