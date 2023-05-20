package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var repoCountTextView: TextView

    private lateinit var followersTexView: TextView
    private lateinit var followingTexView: TextView
    private lateinit var loginTexView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Recuperando as views do arquivo de xml
        profileImageView = findViewById(R.id.profileImageView)
        nameTextView = findViewById(R.id.nameTextView)
        bioTextView = findViewById(R.id.bioTextView)
        repoCountTextView = findViewById(R.id.repoCountTextView)

        followingTexView = findViewById(R.id.followingTexView)
        followersTexView = findViewById(R.id.followersTexView)
        loginTexView = findViewById(R.id.loginTexView)


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
                        nameTextView.text = name
                        loginTexView.text = "@${login}"
                        followersTexView.text = followers
                        followingTexView.text = following
                        bioTextView.text = bio
                        repoCountTextView.text = repoCount.toString()
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
                            .into(profileImageView)

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

