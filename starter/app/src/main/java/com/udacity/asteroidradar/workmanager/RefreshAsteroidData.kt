package com.udacity.asteroidradar.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.common.DateTimeUtil
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRadarRepository
import retrofit2.HttpException

class RefreshAsteroidData(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_MANAGER_NAME = "RefreshAsteroidData"
    }

    override suspend fun doWork(): Result {
        val asteroidDao = AsteroidDatabase.getInstance(applicationContext).asteroidDao
        val repository = AsteroidRadarRepository(asteroidDao)
        return try {
            repository.getAsteroids(DateTimeUtil.getTodayDate(), DateTimeUtil.getEndDate()) {
                it.printStackTrace()
                Result.retry()
            }
            Result.success()
        } catch (ex: HttpException) {
            Result.retry()
        }
    }
}