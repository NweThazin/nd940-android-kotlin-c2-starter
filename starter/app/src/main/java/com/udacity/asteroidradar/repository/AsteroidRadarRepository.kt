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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

class AsteroidRadarRepository(private val asteroidDao: AsteroidDatabaseDao) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(asteroidDao.getAllAsteroids()) {
        it.asDomainModel()
    }

    suspend fun getAsteroids(startDate: String, endDate: String) {
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
            }
        }
    }

    fun getPhotoOfDay(): Deferred<PictureOfDay> {
        return Network.networkService.getAsteroidImagesAsync(BuildConfig.API_KEY)
    }
}