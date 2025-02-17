package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(asteroids: List<AsteroidEntity>)

    @Query("SELECT * FROM asteroid_entity WHERE Date(close_approach_date) >= Date(:today) ORDER BY Date(close_approach_date) ASC")
    fun getAsteroids(today: String): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroid_entity ORDER BY Date(close_approach_date) ASC")
    fun getSavedAsteroids(): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroid_entity WHERE Date(close_approach_date) == Date(:today) ORDER BY Date(close_approach_date) ASC")
    fun getTodayAsteroids(today: String): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroid_entity WHERE Date(close_approach_date) BETWEEN Date(:startDate) AND Date(:endDate) ORDER BY Date(close_approach_date) ASC")
    fun getWeekAsteroids(startDate: String, endDate: String): LiveData<List<AsteroidEntity>>

    @Query("DELETE FROM asteroid_entity")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pictureOfDayEntity: PictureOfDayEntity)

    @Query("SELECT * FROM picture_of_day_entity")
    fun getPictureOfDay(): LiveData<PictureOfDayEntity>

    @Query("DELETE FROM picture_of_day_entity")
    suspend fun clearPictureOfDay()
}