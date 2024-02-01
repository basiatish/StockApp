package com.basiatish.stocks.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _companyName = MutableLiveData<String>()

    val companyName: LiveData<String> get() = _companyName

    private val _companyShortName = MutableLiveData<String>()

    val companyShortName: LiveData<String> get() = _companyShortName

    fun saveCompanyName(name: String, shortName: String) {
        _companyName.value = name
        _companyShortName.value = shortName
    }

}