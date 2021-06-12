package edu.itesm.tournamentHelp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CarritoAdapter(private val data: MutableList<miComic>?) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>()  {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){

        fun bind(property : miComic){
            val titulo = view.findViewById<TextView>(R.id.tituloCarrito)
            //var imagen = view.findViewById<ImageView>(R.id.imagenCarrito)
            var descripcion = view.findViewById<TextView>(R.id.descCarrito)

            titulo.text = property.titulo
            descripcion.text = property.descripcion


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.carrito_renglon,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data!![position])
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun deleteItem(index:Int){
        data?.removeAt(index)
        notifyDataSetChanged()
    }

    fun getItem(index: Int) : miComic? {
        return data?.get(index)
    }
}