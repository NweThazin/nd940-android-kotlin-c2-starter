package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.common.Constants
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRadarRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    val application: Application,
    dao: AsteroidDatabaseDao
) : ViewModel() {

    private val repository = AsteroidRadarRepository(dao)

    enum class ApiStatus {
        LOADING,
        SUCCESS,
        ERROR
    }

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus> = _apiStatus

    private var _filterMenuItem = MutableLiveData<FilterMenuItem>()
    val filterMenuItem: LiveData<FilterMenuItem> = _filterMenuItem

    val asteroidsByFilter: LiveData<List<Asteroid>> =
        Transformations.switchMap<FilterMenuItem, List<Asteroid>>(filterMenuItem) {
            when (it) {
                FilterMenuItem.TODAY -> {
                    repository.getTodayAsteroids(getTodayDate())
                }
                FilterMenuItem.SAVED -> {
                    repository.getSavedAsteroids()
                }
                FilterMenuItem.WEEK -> {
                    repository.getWeekAsteroids(getTodayDate(), getEndDate())
                }
                else -> {
                    repository.getAsteroidsFromLocal(getTodayDate())
                }
            }
        }

    val defaultAsteroids: LiveData<List<Asteroid>> =
        repository.getAsteroidsFromLocal(getTodayDate())

    fun fetchAsteroids() {
        updateApiStatus(ApiStatus.LOADING)
        viewModelScope.launch {
            repository.getAsteroids(getTodayDate(), getEndDate()) {
                updateApiStatus(ApiStatus.ERROR)
            }
        }
    }

    private fun getTodayDate(): String {
        val currentTime = Calendar.getInstance().time
        val format = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return format.format(currentTime)
    }

    private fun getEndDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        val format = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return format.format(calendar.time)

    }

    fun updateApiStatus(apiStatus: ApiStatus) {
        _apiStatus.postValue(apiStatus)
    }

    val pictureOfDay: LiveData<PictureOfDay> = repository.getPictureOfDayFromLocal()
    fun fetchPhotoOfDay() {
        viewModelScope.launch {
            repository.getPictureOfDay()
        }
    }

    fun onMenuItemSelected(item: FilterMenuItem?) {
        _filterMenuItem.postValue(item)
    }

    class ViewModelFactory(
        private val application: Application,
        private val dao: AsteroidDatabaseDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application, dao) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }

    }

    enum class FilterMenuItem {
        WEEK,
        TODAY,
        SAVED
    }
}