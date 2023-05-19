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
    private lateinit var repoCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileImageView = findViewById(R.id.profileImageView)
        nameTextView = findViewById(R.id.nameTextView)
        repoCountTextView = findViewById(R.id.repoCountTextView)

        val profileCard: LinearLayout = findViewById(R.id.profileCard)
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

                if (response.isSuccessful && !responseData.isNullOrEmpty()) {
                    val user = JSONObject(responseData)
                    val name = user.getString("name")
                    val avatarUrl = user.getString("avatar_url")
                    val repoCount = user.getInt("public_repos")

                    runOnUiThread {
                        nameTextView.text = name
                        repoCountTextView.text = resources.getQuantityString(
                            R.plurals.repo_count,
                            repoCount,
                            repoCount
                        )

                        Glide.with(this@MainActivity)
                            .load(avatarUrl)
                            .placeholder(R.drawable.profile_placeholder)
                            .into(profileImageView)

                        findViewById<LinearLayout>(R.id.profileCard).setOnClickListener {
                            openRepoList()
                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun openRepoList() {
        val intent = Intent(this, RepoListActivity::class.java)
        startActivity(intent)
    }
}

