package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.udacity.asteroidradar.domain.Asteroid

@Dao
interface AsteroidDatabaseDao {

    @Insert
    suspend fun insertAll(asteroids: List<AsteroidEntity>)

    @Query("SELECT * FROM asteroid_entity ")
    fun getAllAsteroids(): LiveData<List<AsteroidEntity>>

    @Query("DELETE FROM asteroid_entity")
    suspend fun clearAll()
}