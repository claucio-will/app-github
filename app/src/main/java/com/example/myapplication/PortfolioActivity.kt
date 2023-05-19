package com.example.myapplication
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityPortfolioBinding
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class PortfolioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPortfolioBinding
    private lateinit var projectsAdapter: ProjectsAdapter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Configurar o RecyclerView com layout horizontal
        binding.projectsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Configurar o ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Buscando projetos...")
        progressDialog.setCancelable(false)

        // Fazer solicitação à API do GitHub e exibir os projetos retornados
        FetchProjectsTask().execute()
    }

    private inner class FetchProjectsTask : AsyncTask<Unit, Unit, List<GitHubProject>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show()
        }

        override fun doInBackground(vararg params: Unit): List<GitHubProject> {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.github.com/users/claucio-will/repos")
                .build()

            try {
                val response = client.newCall(request).execute()
                val projects = parseProjectsResponse(response.body?.string())
                return projects
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return emptyList()
        }

        override fun onPostExecute(result: List<GitHubProject>) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            showProjects(result)
        }
    }

    private fun parseProjectsResponse(response: String?): List<GitHubProject> {
        val gson = Gson()
        return gson.fromJson(response, Array<GitHubProject>::class.java).toList()
    }

    private fun showProjects(projects: List<GitHubProject>) {
        // Inicializar e definir o adaptador para o RecyclerView
        projectsAdapter = ProjectsAdapter(projects) { project ->
            // Lógica para lidar com o clique em um projeto
        }
        binding.projectsList.adapter = projectsAdapter
    }
}

