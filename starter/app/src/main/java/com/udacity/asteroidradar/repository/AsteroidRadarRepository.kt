package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

class AsteroidRadarRepository(private val asteroidDao: AsteroidDatabaseDao) {

    fun getAsteroidsFromLocal(startDate: String, endDate: String): LiveData<List<Asteroid>> {
        return Transformations.map(asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }
    }

    suspend fun getAsteroids(startDate: String, endDate: String, onError: (ex: Exception) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val resultString = Network.networkService.getAsteroidsAsync(
                    startDate,
                    endDate,
                    BuildConfig.API_KEY
                ).await()
                val jsonObject = JSONObject(resultString)
                val items = parseAsteroidsJsonResult(jsonObject)
                val itemEntities = items.asDatabaseModel()
                asteroidDao.insertAll(itemEntities)
            } catch (ex: Exception) {
                ex.printStackTrace()
                return@withContext onError(ex)
            }
        }
    }

    fun getPictureOfDayFromLocal(): LiveData<PictureOfDay> {
        return Transformations.map(asteroidDao.getPictureOfDay()) { entity ->
            entity?.let {
                it.asDomainModel()
            }
        }
    }

    suspend fun getPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                val pictureOfDay =
                    Network.networkService.getAsteroidImagesAsync(BuildConfig.API_KEY).await()
                val pictureOfDayEntity = pictureOfDay.asDatabaseModel()
                asteroidDao.insert(pictureOfDayEntity)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}