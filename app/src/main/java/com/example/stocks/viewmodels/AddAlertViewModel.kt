package com.example.stocks.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.stocks.App
import com.example.stocks.database.alertdatabase.AlertDao
import com.example.stocks.database.alertdatabase.Alert
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddAlertViewModel(private val alertDao: AlertDao, app: Application) : AndroidViewModel(app) {

    private val _alert = MutableLiveData<Alert>()

    val alert: LiveData<Alert> = _alert

    private var alertId: Long? = null

    fun createAlert(update: Boolean, id: Int, compName: String, price: Double, above: Boolean) {
        val time = System.currentTimeMillis()
        val alert =
            if (!update) Alert(compName = compName, price = price, time = time, above = above, status = true)
            else Alert(id = id, compName = compName, price = price, time = time, above = above, status = true)
        if (!update) addAlert(alert)
        else updateAlert(alert)
    }

    private fun addAlert(alert: Alert) {
        viewModelScope.launch(IO) {
            alertId = alertDao.insert(alert)
            val tag = "$alertId ${alert.compName}"
            getApplication<App>().scheduleWork(tag, alert.compName, alert.price, alert.time, alert.above)
        }
    }

    private fun updateAlert(alert: Alert) {
        viewModelScope.launch(IO) {
            alertDao.update(alert)
            val tag = "${alert.id} ${alert.compName}"
            getApplication<App>().scheduleWork(tag, alert.compName, alert.price, alert.time, alert.above)
        }
    }

    fun getAlert(id: Int) {
        viewModelScope.launch {
            _alert.value = alertDao.getAlert(id)
        }
    }
}

class AddAlertViewModelFactory(private val alertDao: AlertDao, private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddAlertViewModel::class.java)) {
            return AddAlertViewModel(alertDao, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}