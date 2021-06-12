package edu.itesm.tournamentHelp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_carrito.*
import kotlinx.android.synthetic.main.fragment_tournament.*

abstract class SwipeToDelete (context: Context,
                              direccion: Int, direccionArrastre: Int):
        ItemTouchHelper.SimpleCallback(direccion, direccionArrastre){
    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
}

class CarritoFragment : Fragment() {

    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_carrito, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        manager = LinearLayoutManager(context)

        cargaDatos()
        BackButton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_carritoFragment_to_mainFragment)
        }
    }

    private fun cargaDatos(){

        val reference: DatabaseReference
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usuario = Firebase.auth.currentUser
        reference = database.getReference("comics/${usuario.uid}")

        recyclerCarrito.apply {

            reference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listaComics = ArrayList<miComic>()
                    for(comic in snapshot.children){
                        var objeto = comic.getValue(miComic::class.java)
                        listaComics.add(objeto as miComic)
                    }

                    if(listaComics.isEmpty()){
                        Toast.makeText(context,"You are not Signed Up for any tournaments at this time", Toast.LENGTH_LONG).show()
                    }
                    layoutManager = manager
                    myAdapter = CarritoAdapter(listaComics)
                    adapter = myAdapter

                    val item = object : SwipeToDelete( context,
                            ItemTouchHelper.UP, ItemTouchHelper.LEFT){
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            super.onSwiped(viewHolder, direction)

                            val comic = listaComics[viewHolder.adapterPosition]

                            borrarComic(comic)
                        }
                    }

                    val itemTouchHelper = ItemTouchHelper(item)
                    itemTouchHelper.attachToRecyclerView(recyclerCarrito)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"Error al obtener los datos", Toast.LENGTH_LONG).show()
                }
            })
        }


    }

    private fun borrarComic( comic : miComic){

        val usuario = Firebase.auth.currentUser
        val referencia = FirebaseDatabase.getInstance().getReference("comics/${usuario.uid}/${comic.id}")

        referencia.removeValue()
    }
}