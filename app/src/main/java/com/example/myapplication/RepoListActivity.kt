package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class RepoListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        listView = findViewById(R.id.repoListView)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Carregando repositórios...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        loadRepoList()
    }

    private fun loadRepoList() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.github.com/users/claucio-will/repos")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (response.isSuccessful && !responseData.isNullOrEmpty()) {
                    val repos = JSONArray(responseData)
                    val repoNames = mutableListOf<String>()

                    for (i in 0 until repos.length()) {
                        val repo = repos.getJSONObject(i)
                        val repoName = repo.getString("name")
                        repoNames.add(repoName)
                    }

                    runOnUiThread {
                        progressDialog.dismiss()
                        showRepoList(repoNames)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    progressDialog.dismiss()
                    showNetworkError()
                }
            }
        })
    }

    private fun showRepoList(repoNames: List<String>) {
        val adapter = ArrayAdapter(this, R.layout.list_item_repo, repoNames)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val repoName = repoNames[position]
            val url = "https://github.com/claucio-will/$repoName"
            openRepoUrl(url)
        }
    }

    private fun openRepoUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showNetworkError() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Erro de conexão com a internet.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }

        val alert = dialogBuilder.create()
        alert.show()
    }
}
