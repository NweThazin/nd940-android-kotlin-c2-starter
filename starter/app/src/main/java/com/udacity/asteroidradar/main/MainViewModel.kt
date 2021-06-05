package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRadarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.lang.IllegalArgumentException

class MainViewModel(
    val application: Application,
    dao: AsteroidDatabaseDao
) : ViewModel() {

    private val repository = AsteroidRadarRepository(dao)

    val asteroids: LiveData<List<Asteroid>> = repository.asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay> = _pictureOfDay

    fun fetchAsteroids() {
        viewModelScope.launch {
            repository.getAsteroids("2021-06-05", "2021-06-12")
        }
    }

    fun fetchPhotoOfDay() {
        viewModelScope.launch {
            val pictureOfDay = repository.getPhotoOfDay().await()
            _pictureOfDay.value = pictureOfDay
        }
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
}