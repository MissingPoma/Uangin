package com.example.uangin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.uangin.database.da.PemasukanDao
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.dao.PengeluaranDao
import com.example.uangin.database.entity.Kategori
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran

@Database(entities = [Pemasukan::class, Pengeluaran::class, Kategori::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pengeluaranDao(): PengeluaranDao
    abstract fun pemasukanDao(): PemasukanDao
    abstract fun kategoriDao(): KategoriDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Not recommended for production.
                    .build()
            }
            return instance!!
        }
    }
}