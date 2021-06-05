package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.domain.Asteroid

class MainViewModel : ViewModel() {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>> = _asteroids

    fun fetchAsteroids() {
        _asteroids.postValue(tempData())
    }

    fun tempData(): List<Asteroid> {
        return listOf(
            Asteroid(
                2465633,
                "465633 (2009 JR5)",
                "2015-09-08",
                20.36,
                0.5035469604,
                18.1279547773,
                0.3027478814,
                true
            ),
            Asteroid(
                3426410,
                "465633 (2009 JR5)",
                "2015-09-08",
                20.36,
                0.5035469604,
                18.1279547773,
                0.3027478814,
                false
            ),
            Asteroid(
                3553060,
                "(2010 XT10)",
                "2015-09-08",
                20.36,
                0.5035469604,
                18.1279547773,
                0.3027478814,
                false
            ),
            Asteroid(
                3727181,
                "(2015 RO36)",
                "2015-09-08",
                20.36,
                0.5035469604,
                18.1279547773,
                0.3027478814,
                true
            )
        )
    }

}