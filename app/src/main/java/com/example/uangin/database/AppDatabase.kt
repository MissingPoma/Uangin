package com.example.uangin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.dao.PemasukanDao
import com.example.uangin.database.dao.PengeluaranDao
import com.example.uangin.database.entity.Kategori
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Pemasukan::class, Pengeluaran::class, Kategori::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pengeluaranDao(): PengeluaranDao
    abstract fun pemasukanDao(): PemasukanDao
    abstract fun kategoriDao(): KategoriDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}

