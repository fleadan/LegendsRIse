package edu.itesm.tournamentHelp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ComicsAdapter(private val data: List<Comic>?) : RecyclerView.Adapter<ComicsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){

        fun bind(property: Comic){
            val title = view.findViewById<TextView>(R.id.tituloCarrito)
           // val imageView = view.findViewById<ImageView>(R.id.imagenCarrito)
            val description = view.findViewById<TextView>(R.id.descCarrito)
            val agregar = view.findViewById<Button>(R.id.comprar)

            title.text = property.title
            description.text =  property.description

                //.into(imageView)

            agregar.setOnClickListener {

                val usuario = Firebase.auth.currentUser
                reference = database.getReference("Tourneys/${usuario.uid}")
                val id = reference.push().key
                if(property.description == null){
                    property.description = ""
                }

                val comic = miComic(
                        id.toString(),
                        property.title,
                        property.description,

                )
                reference.child(id!!).setValue(comic)
                bundle.putString("edu_itesm_marvel_main", "added_comic")
                analytics.logEvent("main", bundle)
                Toast.makeText(view.context, "Signed up!", Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comic_renglon, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data!![position])
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

}