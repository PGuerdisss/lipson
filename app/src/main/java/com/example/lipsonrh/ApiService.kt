package com.example.lipsonrh

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/usuarios/{id}")
    fun getUsuario(@Path("id") id: Long): Call<UsuarioResponse>
}

// Data class para representar o que vem do Spring
data class UsuarioResponse(
    val id: Long,
    val nome: String,
    val cargo: String
)