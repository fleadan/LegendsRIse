package edu.itesm.tournamentHelp
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


lateinit var database: FirebaseDatabase
lateinit var reference: DatabaseReference

// Incluye las variables de Analytics:
lateinit var analytics: FirebaseAnalytics
lateinit var bundle: Bundle

class MainFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val BASE_URL = "https://gateway.marvel.com/v1/public/"
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    private lateinit var  results: Results
    lateinit var option : Spinner

    val teams: Array<String> = arrayOf("TSM", "G2","C9", "TL", "SKT")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        option = view.findViewById(R.id.TeamSpinner) as Spinner
        ArrayAdapter.createFromResource(
                requireActivity().baseContext,
                R.array.TeamsArray,
                android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            TeamSpinner?.adapter = adapter



        }
        val spinner: Spinner = view.findViewById(R.id.TeamSpinner)

        spinner.onItemSelectedListener = this





        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        manager = LinearLayoutManager(context)

        database = FirebaseDatabase.getInstance()

        analytics = FirebaseAnalytics.getInstance(context)
        bundle = Bundle()

        getAllData()

        imageViewLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }

        buttonMisComics.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_carritoFragment)
        }
        button2.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_tourneysActivity)
        }
        button3.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_myTeams)
        }
        button7.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_newTfragment)
        }
        button8.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_createTeamFragment)
        }

    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    fun getAllData(){

        val callToService = getRetrofit().create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val responseFromService = callToService.getComics()
            activity?.runOnUiThread {


                if(responseFromService.isSuccessful){
                    results = responseFromService.body() as Results


                    //recyclerView = recyclerComics.apply {

                       // layoutManager = manager
                        myAdapter = ComicsAdapter(results.data?.results)
                       // adapter = myAdapter

                   // }
                } //else {
                    //Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show()

                //}
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        var back_images = intArrayOf(
                R.drawable.tsm, R.drawable.yitu,
                R.drawable.esekate, R.drawable.liq,
                R.drawable.cloud, R.drawable.heretics
        )
        var res = position

        /*if(res ==1){
            clmain!!.setBackgroundResource(R.drawable.tsm)}*/
        //Toast.makeText(getActivity(),res,Toast.LENGTH_SHORT).show()

        when (res){
            1 -> {
                clmain!!.setBackgroundResource(back_images[0])
                data1.text = "1"
            }
            2 -> {
                clmain!!.setBackgroundResource(back_images[1])
                data1.text = "2"
            }
            3 -> {
                clmain!!.setBackgroundResource(back_images[2])
                data1.text = "3"
            }
            4 -> {
                clmain!!.setBackgroundResource(back_images[3])
                data1.text = "4"
            }
            5 -> {
                clmain!!.setBackgroundResource(back_images[4])
                data1.text = "5"
            }
            6 -> {
                clmain!!.setBackgroundResource(back_images[5])
                data1.text = "6"
            }

        }
        //Toast.makeText(getActivity(),res,Toast.LENGTH_SHORT).show();
    }

}