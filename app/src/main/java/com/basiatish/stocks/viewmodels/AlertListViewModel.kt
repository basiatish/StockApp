package com.basiatish.stocks.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.basiatish.stocks.App
import com.basiatish.stocks.database.alertdatabase.Alert
import com.basiatish.stocks.database.alertdatabase.AlertDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertListViewModel(private val alertDao: AlertDao, app: Application) : AndroidViewModel(app) {

    private var _alertList = MutableLiveData<List<Alert>>()
    val alertList: LiveData<List<Alert>> = _alertList

    private var _deleteStatus = MutableLiveData<Boolean>()
    val deleteStatus: LiveData<Boolean> = _deleteStatus

    private val _removeAlertList: MutableList<Alert> = mutableListOf()

    private val _listEmpty = MutableLiveData<Boolean>()
    val listEmpty: LiveData<Boolean> = _listEmpty

    val activeAlerts: MutableList<Alert> = mutableListOf()
    val deactivatedAlerts: MutableList<Alert> = mutableListOf()

    fun getAlerts(compName: String) {
        viewModelScope.launch() {
            _alertList.value = alertDao.getCompanyAlerts(compName)
        }
    }

    fun updateAlert(alert: Alert, active: Boolean) {
        val updatedAlert = Alert(id = alert.id, compName = alert.compName, price = alert.price,
            time = System.currentTimeMillis(), above = alert.above, status = active)

        if (active) {
            activeAlerts.add(alert)
            deactivatedAlerts.remove(alert)
        }
        else {
            deactivatedAlerts.add(alert)
            activeAlerts.remove(alert)
        }

        viewModelScope.launch(IO) {
            alertDao.update(updatedAlert)
        }
    }

    fun updateAlertList(alert: Alert, remove: Boolean) {
        if (remove) {
            _removeAlertList.add(alert)
            if (_removeAlertList.isNotEmpty()) {
                _listEmpty.value = false
            }
            Log.i("Remove", "Added to remove list ${alert.price}")
        }
        else {
            _removeAlertList.remove(alert)
            if (_removeAlertList.isEmpty()) {
                _listEmpty.value = true
            }
            Log.i("Remove", "Removed from remove list ${alert.price}")
        }
    }

    fun deleteAlert() {
        Log.i("Remove", "Delete status: Size ${_removeAlertList.size}")
        viewModelScope.launch(IO) {
            _removeAlertList.forEach { alert ->
                alertDao.delete(alert)
                try {
                    activeAlerts.remove(alert)
                    deactivatedAlerts.add(alert)
                } catch (e: Exception) {
                    Log.e("deleteAlert", "unable to remove from lists")
                }
            }
            withContext(Main) {
                _deleteStatus.value = true
                _listEmpty.value = true
            }
        }
    }

    fun activateAlert() {
        viewModelScope.launch(IO) {
            if (activeAlerts.isNotEmpty()) {
                Log.e("Alert", "Activate fun")
                activeAlerts.forEach { alert ->
                    val tag = "${alert.id} ${alert.compName}"
                    getApplication<App>().scheduleWork(
                        alert.id,
                        tag,
                        alert.compName,
                        alert.price,
                        alert.time,
                        alert.above
                    )
                }
            }
        }
    }

    fun deactivateAlert() {
        viewModelScope.launch(IO) {
            if (deactivatedAlerts.isNotEmpty()) {
                Log.e("Alert", "Deactivate fun")
                deactivatedAlerts.forEach { alert ->
                    val tag = "${alert.id} ${alert.compName}"
                    getApplication<App>().destroyWork(tag)
                }
            }
        }
    }
}

class AlertListViewModelFactory(private val alertDao: AlertDao, private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertListViewModel::class.java)) {
            return AlertListViewModel(alertDao, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}