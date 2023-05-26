package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Registrando o evento de click no card de perfil
        val profileCard: LinearLayout = findViewById(R.id.layoutRepo)
        profileCard.setOnClickListener {
            openRepoList()

        }
        loadUserInfo()  
    }

    private fun loadUserInfo() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.github.com/users/claucio-will")
            .build()

        client.newCall(request).enqueue(object : Callback {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                // Pegando os dados que tem dentro da api
                if (response.isSuccessful && !responseData.isNullOrEmpty()) {
                    val user = JSONObject(responseData)
                    val name = user.getString("name")
                    val login = user.getString("login")
                    val followers = user.getString("followers")
                    val following = user.getString("following")
                    val bio = user.getString("bio")
                    val avatarUrl = user.getString("avatar_url")
                    val repoCount = user.getInt("public_repos")

                    // Populando os dados dentro das views
                    runOnUiThread {
                        binding.nameTextView.text = name
                        binding.loginTexView.text = "@${login}"
                        binding.followersTexView.text = followers
                        binding.followingTexView.text = following
                        binding.bioTextView.text = bio
                        binding.repoCountTextView.text = repoCount.toString()
//                        repoCountTextView.text = resources.getQuantityString(
//                            R.plurals.repo_count,
//                            repoCount,
//                            repoCount
//                        )

                        // Criando o car, Colocando a imagem  do github e caso não tenha
                        // Pega a imagem do drawable
                        Glide.with(this@MainActivity)
                            .load(avatarUrl)
                            .placeholder(R.drawable.profile_placeholder)
                            .into(binding.profileImage)

                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    // Metodo que abre a tela de RepoListActivity
    private fun openRepoList() {
        val intent = Intent(this, RepoListActivity::class.java)
        startActivity(intent)
    }
}

