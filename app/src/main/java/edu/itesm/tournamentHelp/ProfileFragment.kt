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
import edu.itesm.tournamentHelp.databinding.FragmentProfileBinding
import kotlinx.android.synthetic.main.fragment_profile.*


abstract class SwipeToDeletes (context: Context,
                               direccion: Int, direccionArrastre: Int):
        ItemTouchHelper.SimpleCallback(direccion, direccionArrastre) {
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

class ProfileFragment : Fragment() {
    lateinit var bind : FragmentProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = FragmentProfileBinding.inflate(layoutInflater)
        cargaDatos()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button4.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_profileFragment_to_createTeamFragment)
        }
    }
    private fun borraPokemon(pokemon: Team){

        val usuario = Firebase.auth.currentUser
        val referencia = FirebaseDatabase.getInstance().getReference("Teams") ///${usuario.uid}/${team.id}

        referencia.removeValue()


    }

    private fun cargaDatos(){
        var reference : DatabaseReference
        var database : FirebaseDatabase

        database = FirebaseDatabase.getInstance()
        val usuario = Firebase.auth.currentUser
        reference = database.getReference("Teams/${usuario.uid}")

        bind.TeamsRv.apply {

            activity?.let {
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var listaPokemon = ArrayList<Team>()
                        for (pokemon in snapshot.children) {
                            var objeto = pokemon.getValue(Team::class.java)
                            listaPokemon.add(objeto as Team)
                        }

                        if(listaPokemon.isEmpty()){
                            Toast.makeText(activity, "Error al obtener datos", Toast.LENGTH_LONG).show()
                        }
                        adapter = TeamsAdapter(listaPokemon)
                        layoutManager = LinearLayoutManager(requireActivity())

                        val item = object : SwipeToDelete(it,
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
                        Toast.makeText(activity, "No hubo carga de datos", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

}