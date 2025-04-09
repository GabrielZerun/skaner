package com.example.barcode_scanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile // Zapewnia widoczność instancji dla wszystkich wątków
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Zwróć istniejącą instancję lub stwórz nową (thread-safe)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "product_database" // Nazwa pliku bazy danych
                )
                    // .fallbackToDestructiveMigration() // Prosta strategia migracji (usuwa i tworzy na nowo) - ostrożnie!
                    .build()
                INSTANCE = instance
                instance // Zwróć nowo utworzoną instancję
            }
        }
    }
}