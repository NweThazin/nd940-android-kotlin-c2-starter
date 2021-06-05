package com.udacity.asteroidradar.common

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    fun getTodayDate(): String {
        val currentTime = Calendar.getInstance().time
        val format = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return format.format(currentTime)
    }

    fun getEndDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        val format = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return format.format(calendar.time)
    }
}