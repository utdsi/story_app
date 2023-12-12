package com.example.storyapp

data class RegistrationRequest(
    val user_id: Int,
    val email: String,
    val name: String,
    val dob: String,
    val gender: Int,
    val created_at: String? = null,
    val profile_status: Int? = null,
    val profile_pic: String? = null,

)

data class ApiResponse(
    val status: Int,
    val message: String,
    val data: List<RegistrationRequest>?
)

data class LanguageRequest(
    var languages_id : Int,
    var name: String,

)

data class LanguageApiResponse(
    var status: Int,
    var message: String,
    var data: List<LanguageRequest>?

)

//category_id

data class CategoryApiResponse(
    var status: Int,
    var message: String,
    var data: List<CategoryRequest>?

)
data class CategoryRequest(
    val category_id: Int,
    val name: String
)

data class ImageApiResponse(
    var status: Int,
    var message: String,
    var data: List<ImageRequest>?
)
data class ImageRequest(

    val post_image_id: Int,
    val title: String,
    val description: String,
    val category_id: Int,
    val language_id: Int,
    val status: Int,
    val image: String

)

data class ImageShare(
    val image_id: Int,
    val image: String
)
