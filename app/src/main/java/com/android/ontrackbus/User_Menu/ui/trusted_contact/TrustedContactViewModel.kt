package com.android.ontrackbus.User_Menu.ui.trusted_contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrustedContactViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is trusted_contact Fragment"
    }
    val text: LiveData<String> = _text
}