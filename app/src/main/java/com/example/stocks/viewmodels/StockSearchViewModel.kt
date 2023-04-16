package com.example.stocks

import androidx.lifecycle.*
import com.example.stocks.models.remote.StockSearch
import com.example.stocks.utils.network.StockStatus
import kotlinx.coroutines.launch

class StockSearchViewModel: ViewModel() {

    private val _status = MutableLiveData<StockStatus>()

    val status: LiveData<StockStatus> = _status

    private val _companies = MutableLiveData<List<StockSearch>>()

    private val apiKey = BuildConfig.API_KEY

    val companies: LiveData<List<StockSearch>> = _companies

    fun findCompany(name: String) : LiveData<List<StockSearch>> {
        viewModelScope.launch {
            _status.value = StockStatus.LOADING
            try {
                _companies.value = StockApi.retrofitService
                    .getCompanies(name, "20", apiKey)
                _status.value = StockStatus.DONE
            } catch (e: Exception) {
                _status.value = StockStatus.ERROR
                _companies.value = listOf()
            }

        }
        return companies
    }

}

//class StockSearchViewModelFactory() : ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(StockSearchViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return StockSearchViewModel() as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}