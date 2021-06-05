package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.common.Constants
import com.udacity.asteroidradar.common.DateTimeUtil
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
                    repository.getTodayAsteroids(DateTimeUtil.getTodayDate())
                }
                FilterMenuItem.SAVED -> {
                    repository.getSavedAsteroids()
                }
                FilterMenuItem.WEEK -> {
                    repository.getWeekAsteroids(
                        DateTimeUtil.getTodayDate(),
                        DateTimeUtil.getEndDate()
                    )
                }
                else -> {
                    repository.getAsteroidsFromLocal(DateTimeUtil.getTodayDate())
                }
            }
        }

    val defaultAsteroids: LiveData<List<Asteroid>> =
        repository.getAsteroidsFromLocal(DateTimeUtil.getTodayDate())

    fun fetchAsteroids() {
        updateApiStatus(ApiStatus.LOADING)
        viewModelScope.launch {
            repository.getAsteroids(DateTimeUtil.getTodayDate(), DateTimeUtil.getEndDate()) {
                updateApiStatus(ApiStatus.ERROR)
            }
        }
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