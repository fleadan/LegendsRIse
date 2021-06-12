package edu.itesm.tournamentHelp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_tournament.*

lateinit var databasee: FirebaseDatabase
lateinit var referencee: DatabaseReference


lateinit var analyticse: FirebaseAnalytics
lateinit var bundlee: Bundle
class TournamentFrag : Fragment() {

    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    private lateinit var  results: Results
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tournament, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        manager = LinearLayoutManager(context)

        database = FirebaseDatabase.getInstance()

        analytics = FirebaseAnalytics.getInstance(context)
        bundle = Bundle()



        button.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_tournamentFrag_to_newTfragment)
        }


    }

}