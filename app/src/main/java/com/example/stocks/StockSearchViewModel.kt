package com.example.stocks

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class StockSearchViewModel: ViewModel() {

    private val _status = MutableLiveData<StockApiStatus>()

    val status: LiveData<StockApiStatus> = _status

    private val _companies = MutableLiveData<List<StockSearch>>()

    private val apiKey = BuildConfig.API_KEY

    val companies: LiveData<List<StockSearch>> = _companies

    fun findCompany(name: String) : LiveData<List<StockSearch>> {
        viewModelScope.launch {
            _status.value = StockApiStatus.LOADING
            try {
                _companies.value = StockApi.retrofitService
                    .getCompanies(name, "20", apiKey)
                _status.value = StockApiStatus.DONE
            } catch (e: Exception) {
                _status.value = StockApiStatus.ERROR
                _companies.value = listOf()
            }

        }
        return companies
    }

}

class StockSearchViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockSearchViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}