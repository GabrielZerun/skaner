package com.example.barcode_scanner // Upewnij się, że pakiet jest poprawny

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.barcode_scanner.data.Product // Importuj klasę Product

class AddProductDialogFragment : DialogFragment() {

    // Interfejs do komunikacji z MainActivity
    interface AddProductListener {
        fun onProductAdded(product: Product)
    }

    private var listener: AddProductListener? = null
    private lateinit var editTextBarcode: EditText
    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductDescription: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Upewnij się, że aktywność implementuje interfejs listenera
        listener = context as? AddProductListener
        if (listener == null) {
            throw ClassCastException("$context must implement AddProductListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_product, null) // Użyj stworzonego layoutu

        // Znajdź pola EditText w layoucie dialogu
        editTextBarcode = view.findViewById(R.id.editTextBarcode)
        editTextProductName = view.findViewById(R.id.editTextProductName)
        editTextProductDescription = view.findViewById(R.id.editTextProductDescription)

        builder.setView(view)
            // Dodaj przyciski akcji
            .setPositiveButton("Zapisz") { dialog, id ->
                val barcode = editTextBarcode.text.toString().trim()
                val name = editTextProductName.text.toString().trim()
                val description = editTextProductDescription.text.toString().trim()

                // Prosta walidacja - kod i nazwa nie mogą być puste
                if (barcode.isNotEmpty() && name.isNotEmpty()) {
                    val product = Product(
                        barcode = barcode,
                        name = name,
                        description = description.ifEmpty { null } // Zapisz null jeśli opis jest pusty
                    )
                    // Wywołaj metodę interfejsu w MainActivity
                    listener?.onProductAdded(product)
                } else {
                    // Można dodać informację dla użytkownika, np. Toast
                    android.widget.Toast.makeText(requireContext(), "Kod kreskowy i nazwa nie mogą być puste!", android.widget.Toast.LENGTH_SHORT).show()
                    // Tutaj nie zamykamy dialogu automatycznie, aby użytkownik mógł poprawić
                    // (standardowe zachowanie AlertDialog zamknie go, trzeba by to obejść jeśli chcemy inaczej)
                }
            }
            .setNegativeButton("Anuluj") { dialog, id ->
                // Użytkownik anulował dialog - nic nie rób, dialog sam się zamknie
                dismiss()
            }

        return builder.create()
    }

    override fun onDetach() {
        super.onDetach()
        // Zwolnij referencję do listenera, aby uniknąć wycieków pamięci
        listener = null
    }
}