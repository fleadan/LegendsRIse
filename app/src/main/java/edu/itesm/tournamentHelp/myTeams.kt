package edu.itesm.tournamentHelp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import edu.itesm.tournamentHelp.databinding.ActivityMyTeamsBinding

abstract class SwipeToDeletesws (context: Context,
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
class myTeams : AppCompatActivity() {
    private lateinit var bind : ActivityMyTeamsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMyTeamsBinding.inflate(layoutInflater)
        setContentView(bind.root)
        cargaDatos()

    }

    override fun onStart() {
        super.onStart()
        /*back3.setOnClickListener {
            it.findNavController().navigate(R.id.Tourn_to_newTFragment)`F
        }*/
    }
    private fun borraPokemon(pokemon: Team){

        val usuario = Firebase.auth.currentUser
        val referencia = FirebaseDatabase.getInstance().getReference("Tournaments") ///${usuario.uid}/${pokemon.id}

        referencia.removeValue()


    }

    private fun cargaDatos(){
        var reference : DatabaseReference
        var database : FirebaseDatabase

        database = FirebaseDatabase.getInstance()
        val usuario = Firebase.auth.currentUser
        reference = database.getReference("Teams/${usuario.uid}")

        bind.TeamsRv.apply {

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listaPokemon = ArrayList<Team>()
                    for (pokemon in snapshot.children) {
                        var objeto = pokemon.getValue(Team::class.java)
                        listaPokemon.add(objeto as Team)
                    }

                    if(listaPokemon.isEmpty()){
                        Toast.makeText(this@myTeams, "Error al obtener datos", Toast.LENGTH_LONG).show()
                    }
                    adapter = TeamsAdapter(listaPokemon)
                    layoutManager = LinearLayoutManager(this@myTeams)

                    val item = object : SwipeToDelete(this@myTeams,
                            ItemTouchHelper.UP, ItemTouchHelper.LEFT){
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            super.onSwiped(viewHolder, direction)
                            val pokemon = listaPokemon[ viewHolder.adapterPosition ]

                            borraPokemon(pokemon)

                        }
                    }
                    val itemTouchHelper = ItemTouchHelper(item)
                    itemTouchHelper.attachToRecyclerView(bind.TeamsRv)

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@myTeams, "No hubo carga de datos", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
