package com.example.barcode_scanner

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.barcode_scanner.data.AppDatabase
import com.example.barcode_scanner.data.Product
import com.example.barcode_scanner.data.ProductDao
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Zmień deklarację klasy, aby implementowała interfejs
class MainActivity : AppCompatActivity(), AddProductDialogFragment.AddProductListener {

    private lateinit var scanBtn: Button
    private lateinit var addProductBtn: Button // Dodaj nowy przycisk
    private lateinit var statusTextView: TextView
    private lateinit var productNameTextView: TextView
    private lateinit var productDescriptionTextView: TextView

    private lateinit var productDao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicjalizacja widoków
        scanBtn = findViewById(R.id.scanBtn)
        addProductBtn = findViewById(R.id.addProductBtn) // Znajdź nowy przycisk
        statusTextView = findViewById(R.id.statusTextView)
        productNameTextView = findViewById(R.id.productNameTextView)
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView)

        // Inicjalizacja bazy danych i DAO
        val database = AppDatabase.getDatabase(applicationContext)
        productDao = database.productDao()

        hideProductDetails()
        addSampleDataIfNeeded()
        registerUiListeners() // Zmień nazwę funkcji dla obu przycisków
    }

    // Zmień nazwę i zawartość funkcji listenerów
    private fun registerUiListeners() {
        scanBtn.setOnClickListener {
            scannerLauncher.launch(
                ScanOptions()
                    .setPrompt("Zeskanuj kod kreskowy produktu")
                    .setBeepEnabled(true)
                    .setOrientationLocked(false)
                    .setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
            )
        }

        // Listener dla nowego przycisku "Dodaj Produkt"
        addProductBtn.setOnClickListener {
            // Utwórz i pokaż okno dialogowe
            AddProductDialogFragment().show(supportFragmentManager, "AddProductDialog")
        }
    }

    // Implementacja metody z interfejsu AddProductListener
    override fun onProductAdded(product: Product) {
        lifecycleScope.launch {
            // Zapisz produkt w bazie danych w tle
            withContext(Dispatchers.IO) {
                try {
                    productDao.insertProduct(product)
                    // Przełącz z powrotem do wątku głównego, aby pokazać Toast
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Produkt \"${product.name}\" dodany!", Toast.LENGTH_SHORT).show()
                    }
                    Log.i("PRODUCT_ADD", "Product added: ${product.barcode} - ${product.name}")
                } catch (e: Exception) {
                    // Obsługa błędu zapisu (np. naruszenie klucza głównego, jeśli kod już istnieje)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Błąd podczas dodawania produktu: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    Log.e("PRODUCT_ADD", "Error adding product ${product.barcode}", e)
                }
            }
        }
    }

    // ... (reszta kodu MainActivity bez zmian: scannerLauncher, searchForProduct, displayProductInfo, itd.) ...

    // Rejestracja wyniku skanowania
    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Skanowanie anulowane", Toast.LENGTH_SHORT).show()
            statusTextView.text = "Anulowano"
            hideProductDetails()
        } else {
            val scannedBarcode = result.contents
            statusTextView.text = "Zeskanowano: $scannedBarcode"
            searchForProduct(scannedBarcode)
        }
    }

    private fun searchForProduct(barcode: String) {
        lifecycleScope.launch {
            try {
                val product = withContext(Dispatchers.IO) {
                    productDao.getProductByBarcode(barcode)
                }
                if (product != null) {
                    displayProductInfo(product)
                } else {
                    showNotFoundMessage(barcode)
                }
            } catch (e: Exception) {
                showErrorMessage("Błąd podczas wyszukiwania w bazie: ${e.localizedMessage}")
                Log.e("DB_SEARCH_ERROR", "Error searching for barcode $barcode", e)
            }
        }
    }

    private fun displayProductInfo(product: Product) {
        productNameTextView.text = product.name ?: "Brak nazwy"
        productDescriptionTextView.text = product.description ?: "Brak opisu"
        productNameTextView.visibility = View.VISIBLE
        productDescriptionTextView.visibility = View.VISIBLE
    }

    private fun showNotFoundMessage(barcode: String) {
        statusTextView.text = "Nie znaleziono produktu dla kodu: $barcode"
        hideProductDetails()
    }

    private fun showErrorMessage(message: String) {
        statusTextView.text = message
        hideProductDetails()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun hideProductDetails() {
        productNameTextView.visibility = View.GONE
        productDescriptionTextView.visibility = View.GONE
        productNameTextView.text = ""
        productDescriptionTextView.text = ""
    }

    private fun addSampleDataIfNeeded() {
        lifecycleScope.launch {
            val products = withContext(Dispatchers.IO) { productDao.getAllProducts() }
            if (products.isEmpty()) {
                withContext(Dispatchers.IO) {
                    Log.i("DB_INIT", "Adding initial sample data...")
                    productDao.insertProduct(Product("1234567890123", "Przykładowy Produkt A", "To jest opis produktu A."))
                    productDao.insertProduct(Product("9876543210987", "Testowy Towar B", "Bardzo fajny towar B do testów."))
                    productDao.insertProduct(Product("1111111111111", "Produkt C", null))
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Dodano przykładowe dane.", Toast.LENGTH_SHORT).show()
                }
                Log.i("DB_INIT", "Sample data added successfully.")
            } else {
                Log.i("DB_INIT", "Database already contains data or check condition not met. No sample data added this time.")
            }
        }
    }
} // Koniec klasy MainActivity