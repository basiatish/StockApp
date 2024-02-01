package com.basiatish.stocks.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.basiatish.stocks.App
import com.basiatish.stocks.database.alertdatabase.Alert
import com.basiatish.stocks.database.alertdatabase.AlertDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddAlertViewModel(private val alertDao: AlertDao, app: Application) : AndroidViewModel(app) {

    private val _alert = MutableLiveData<Alert>()

    val alert: LiveData<Alert> = _alert

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
            try {
                val alertId = alertDao.insert(alert)
                val tag = "$alertId ${alert.compName}"
                getApplication<App>().scheduleWork(alertId.toInt(), tag, alert.compName, alert.price, alert.time, alert.above)
            } catch (e: Exception) {
                Log.e("Alert", "Error with worker ${e.message}")
            }
        }
    }

    private fun updateAlert(alert: Alert) {
        viewModelScope.launch(IO) {
            try {
                alertDao.update(alert)
                val tag = "${alert.id} ${alert.compName}"
                getApplication<App>().scheduleWork(alert.id, tag, alert.compName, alert.price, alert.time, alert.above)
            } catch (e: Exception) {
                Log.e("Alert", "Error with worker ${e.message}")
            }
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