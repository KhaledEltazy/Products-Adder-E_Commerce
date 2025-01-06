package com.android.productsaddere_commerce

data class Product(
    val id : Int,
    val productName : String,
    val category : String,
    val productDescription : String? = null,
    val price : Float,
    val offer : Float? = null,
    val color : List<String>? = null,
    val sizes : List<String>? = null,
    val images : List<String>
) {

}