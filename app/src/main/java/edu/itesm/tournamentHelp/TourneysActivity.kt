package edu.itesm.tournamentHelp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import edu.itesm.tournamentHelp.databinding.ActivityTourneysBinding
import kotlinx.android.synthetic.main.activity_tourneys.*
import kotlinx.android.synthetic.main.fragment_tournament.*


abstract class SwipeToDeletee (context: Context,
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

class TourneysActivity : AppCompatActivity() {
    private lateinit var bind : ActivityTourneysBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityTourneysBinding.inflate(layoutInflater)
        setContentView(bind.root)
        cargaDatos()

    }

    override fun onStart() {
        super.onStart()
        /*back3.setOnClickListener {
            it.findNavController().navigate(R.id.Tourn_to_newTFragment)`F
        }*/
    }
    private fun borraPokemon(pokemon : Pokemon){

            val usuario = Firebase.auth.currentUser
            val referencia = FirebaseDatabase.getInstance().getReference("Tournaments") ///${usuario.uid}/${pokemon.id}

            referencia.removeValue()


    }

    private fun cargaDatos(){
        var reference : DatabaseReference
        var database : FirebaseDatabase

        database = FirebaseDatabase.getInstance()
        val usuario = Firebase.auth.currentUser
        reference = database.getReference("Tournaments")

        bind.TournRV.apply {

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listaPokemon = ArrayList<Pokemon>()
                    for (pokemon in snapshot.children) {
                        var objeto = pokemon.getValue(Pokemon::class.java)
                        listaPokemon.add(objeto as Pokemon)
                    }

                    if(listaPokemon.isEmpty()){
                        Toast.makeText(this@TourneysActivity, "Error al obtener datos", Toast.LENGTH_LONG).show()
                    }
                    adapter = PokemonAdapter(listaPokemon)
                    layoutManager = LinearLayoutManager(this@TourneysActivity)

                    val item = object : SwipeToDelete(this@TourneysActivity,
                        ItemTouchHelper.UP,ItemTouchHelper.LEFT){
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            super.onSwiped(viewHolder, direction)
                            val pokemon = listaPokemon[ viewHolder.adapterPosition ]

                            borraPokemon(pokemon)

                        }
                    }
                    val itemTouchHelper = ItemTouchHelper(item)
                    itemTouchHelper.attachToRecyclerView(bind.TournRV)

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@TourneysActivity, "No hubo carga de datos", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}