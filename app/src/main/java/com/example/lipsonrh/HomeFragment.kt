package com.example.lipsonrh

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var txtBoasVindas: TextView
    private val sliderHandler = Handler(Looper.getMainLooper())

    // Timer para o carrossel automático
    private val sliderRunnable = object : Runnable {
        override fun run() {
            val size = viewPager.adapter?.itemCount ?: 0
            if (size > 0) {
                var nextItem = viewPager.currentItem + 1
                if (nextItem >= size) nextItem = 0
                viewPager.currentItem = nextItem
                sliderHandler.postDelayed(this, 5000)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewPager = view.findViewById(R.id.viewPagerCarousel)
        txtBoasVindas = view.findViewById(R.id.txtBoasVindas)

        // 1. Configurar o Retrofit para falar com o Spring Boot
        // 10.0.2.2 é o endereço que o emulador usa para acessar o localhost do seu PC
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // 2. Chamar a API buscando o usuário com ID 1 (exemplo)
        buscarDadosDoUsuario(apiService, 1L)

        // 3. Configuração do Carrossel (Imagens devem estar em res/drawable)
        val images = listOf(
            R.drawable.imagema,
            R.drawable.imagemb,
            R.drawable.imagemc
        )

        viewPager.adapter = CarouselAdapter(images)
        sliderHandler.postDelayed(sliderRunnable, 5000)

        return view
    }

    private fun buscarDadosDoUsuario(apiService: ApiService, userId: Long) {
        apiService.getUsuario(userId).enqueue(object : Callback<UsuarioResponse> {
            override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                if (response.isSuccessful) {
                    val usuario = response.body()
                    // Atualiza o texto com o nome vindo do MySQL
                    txtBoasVindas.text = "Feliz aniversário, ${usuario?.nome}!"
                } else {
                    txtBoasVindas.text = "Bem-vindo, Colaborador!"
                }
            }

            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                // Se o servidor Spring Boot estiver desligado, cairá aqui
                txtBoasVindas.text = "Modo Offline"
                Toast.makeText(context, "Erro ao conectar ao servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}