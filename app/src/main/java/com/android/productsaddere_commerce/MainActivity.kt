package com.android.productsaddere_commerce

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.productsaddere_commerce.databinding.ActivityMainBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var selectedImages = mutableListOf<Uri>()
    private val selectedColors = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            //handling color btn
            btnColors.setOnClickListener {
                ColorPickerDialog.Builder(this@MainActivity)
                    .setTitle("Select item color")
                    .setPositiveButton("Save",object :ColorEnvelopeListener{
                        override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                            envelope?.let {
                                selectedColors.add(it.color)
                                updateColors()
                            }
                        }
                    })
                    .setNegativeButton("Cancel"){colorPicker, _ ->
                        colorPicker.dismiss()
                    }
                    .show()
            }

            //handling uploading images
            btnImages.setOnClickListener {

            }

            //handling save data
            fabSave.setOnClickListener {
                val productValidation = validateInformation()
                if (!productValidation){
                    Toast.makeText(this@MainActivity,"Check your inputs",Toast.LENGTH_LONG).show()
                }
                saveProduct()
            }
        }

    }

    private fun saveProduct(){
        val name = binding.etName.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val offer = binding.etOffer.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val sizes = getSizesList(binding.etSizes.text.toString().trim())
    }

    private fun getSizesList(sizes : String) : List<String>?{
        if(sizes.isEmpty())
            return null
        val sizesList = sizes.split(",")
        return sizesList
    }

    private fun updateColors(){
        var colorString = " "
        selectedColors.forEach{
            colorString = "$colorString ${Integer.toHexString(it)}"
        }
        binding.tvUpdatedColors.text = colorString
    }

    private fun validateInformation() : Boolean{
        if(binding.etName.text.trim().toString().isEmpty()) return false
        if(binding.etPrice.text.toString().trim().isEmpty()) return false
        if(binding.etCategory.text.toString().trim().isEmpty()) return false
        if(binding.etPrice.text.toString().trim().isEmpty()) return false
        if(selectedImages.isEmpty()) return false
        else return true
    }

}