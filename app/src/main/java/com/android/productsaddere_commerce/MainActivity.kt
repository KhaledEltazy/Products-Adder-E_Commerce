package com.android.productsaddere_commerce

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.productsaddere_commerce.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            //handling color btn
            btnColors.setOnClickListener {

            }

            //handling uploading images
            btnImages.setOnClickListener {

            }

            //handling save data
            fabSave.setOnClickListener {
                val productName = etName.text.toString()
                val category = etCategory.text.toString()
                val description = etProductDescription.text.toString()
                val price = etPrice.text.toString()
                val offer = etOffer.text.toString()
            }
        }
    }

}