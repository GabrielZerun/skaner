package com.example.barcode_scanner.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
    // Zapytanie o produkt po kodzie kreskowym
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product? // suspend dla Coroutines

    // Metoda do wstawiania/aktualizacji produktu (dla testów/dodawania danych)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Opcjonalnie: Metoda do pobierania wszystkich produktów
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>
}