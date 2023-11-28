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
    val languages_id : Int,
    val name: String,

)

data class LanguageApiResponse(
    val status: Int,
    val message: String,
    val data: List<LanguageRequest>?

)
