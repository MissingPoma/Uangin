package com.example.uangin.database

import androidx.room.TypeConverter
import java.util.Date

object DateConverter {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }
}
