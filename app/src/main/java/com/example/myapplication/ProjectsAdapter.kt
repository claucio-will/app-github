package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso



class ProjectsAdapter(
    private val projects: List<GitHubProject>,
    private val onItemClick: (GitHubProject) -> Unit
) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(project: GitHubProject) {
            itemView.findViewById<TextView>(R.id.projectTitle).text = project.name
            itemView.findViewById<TextView>(R.id.projectDescription).text = project.description

            // Carregar a imagem do projeto utilizando a biblioteca Picasso
            Picasso.get().load(project.imageUrl).placeholder(R.drawable.ic_android_black_24dp)
                .into(itemView.findViewById<ImageView>(R.id.projectImage))

            // Configurar o clique no item do projeto
            itemView.setOnClickListener {
                onItemClick(project)
            }
        }
    }


}
