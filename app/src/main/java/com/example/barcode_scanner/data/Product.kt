package com.example.barcode_scanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val barcode: String,
    val name: String?,
    val description: String?
    // Możesz dodać więcej pól, np. cenę, ilość itp.
)