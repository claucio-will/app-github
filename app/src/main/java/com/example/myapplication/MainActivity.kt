package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.repoListView)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.github.com/users/claucio-will/repos")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (response.isSuccessful && !responseData.isNullOrEmpty()) {
                    val repos = JSONArray(responseData)
                    val repoNames = ArrayList<String>()

                    for (i in 0 until repos.length()) {
                        val repo = repos.getJSONObject(i)
                        val repoName = repo.getString("name")
                        repoNames.add(repoName)
                    }

                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            R.layout.list_item_repo,
                            R.id.repoTextView,
                            repoNames
                        )
                        listView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }
}
