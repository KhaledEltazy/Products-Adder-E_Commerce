package com.android.productsaddere_commerce

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.productsaddere_commerce.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var selectedImages = mutableListOf<Uri>()
    private val selectedColors = mutableListOf<Int>()
    private val productStorage = Firebase.storage.reference
    private val firestore = Firebase.firestore

    private val categoryList = listOf<String>("Special Products","Best deals","Chair","Cupboard","Table","Accessory","Furniture")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateDropDownMenu()

        val selectImagesActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> if(result.resultCode == RESULT_OK){
                val intent = result.data

            //Multiple Images selected
            if(intent?.clipData != null){
                val count = intent.clipData?.itemCount ?: 0
                (0 until count).forEach {
                    val imageUri = intent.clipData?.getItemAt(it)?.uri
                    imageUri?.let {
                        selectedImages.add(it)
                    }
                }
            }   else {
                 val imageUri = intent?.data
                imageUri?.let { selectedImages.add(it) }
                }
            updateImages()
            }
        }

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

            //handling uploading images from gallery
            btnImages.setOnClickListener {
                val intent = Intent(ACTION_GET_CONTENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                intent.type = "image/*"
                selectImagesActivityResult.launch(intent)
            }

            //handling save data
            fabSave.setOnClickListener {
                val productValidation = validateInformation()
                if (!productValidation){
                    Toast.makeText(this@MainActivity,"Check your inputs",Toast.LENGTH_LONG).show()
                } else {
                    saveProduct()
                }
            }
        }

    }

    //implementation of dropDownMenu of category
    private fun populateDropDownMenu(){
        val adapter = ArrayAdapter(this@MainActivity,R.layout.drop_down_list,categoryList)
        binding.category.setAdapter(adapter)
    }

    //showing counts of images in UI
    private fun updateImages() {
        binding.tvImagesCount.text =selectedImages.size.toString()
    }

    private fun saveProduct(){
        val name = binding.etName.text.toString().trim()
        val category = binding.category.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val offer = binding.etOffer.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val sizes = getSizesList(binding.etSizes.text.toString().trim())
        val imageByteArrays = getImagesByteArrays()
        val images = mutableListOf<String>()

        //uploading all info to fireStore
        lifecycleScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                showingProgressBar()
            }

            try{
                async {
                    imageByteArrays.forEach { byteArray ->
                        val id = UUID.randomUUID().toString()
                        try {
                            val imageStorage = productStorage.child("product/images/$id")
                            val result = imageStorage.putBytes(byteArray).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        } catch (e: Exception) {
                            Log.e("FirebaseUpload", "Failed to upload image: $id", e)
                        }
                    }
                }.await()

            } catch(e: java.lang.Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    hidingProgressBar()
                }
            }

            val product = Product(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if(offer.isEmpty()) null else offer.toFloat(),
                if(description.isEmpty()) null else description,
                if(selectedColors.isEmpty()) null else selectedColors,
                sizes,
                images
            )

            firestore.collection("Products").add(product).addOnSuccessListener {
                Toast.makeText(this@MainActivity,"product saved successfully",Toast.LENGTH_LONG).show()
                hidingProgressBar()
            }.addOnFailureListener {
                Log.e("Error",it.message.toString())
                hidingProgressBar()
            }
        }
    }

    //hiding progressbar
    private fun hidingProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    //showing ProgressBar
    private fun showingProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    // get Images in bytesArrays
    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            try {
                val stream = ByteArrayOutputStream()
                val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
                if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    imagesByteArray.add(stream.toByteArray())
                }
            } catch (e: IOException) {
                Log.e("Error", "Failed to process image: ${it.path}", e)
            }
        }
        return imagesByteArray
    }

    //getting sizes
    private fun getSizesList(sizes : String) : List<String>?{
        if(sizes.isEmpty())
            return null
        val sizesList = sizes.split(",")
        return sizesList
    }

    //update color list
    private fun updateColors(){
        var colorString = " "
        selectedColors.forEach{
            colorString = "$colorString ${Integer.toHexString(it)}"
        }
        binding.tvUpdatedColors.text = colorString
    }

    //checking if all require info are provided
    private fun validateInformation() : Boolean{
        if(binding.etName.text.toString().trim().isEmpty()) return false
        if(binding.etPrice.text.toString().trim().isEmpty()) return false
        if(binding.category.text.toString().trim().isEmpty()) return false
        if(selectedImages.isEmpty()) return false
        else return true
    }

}