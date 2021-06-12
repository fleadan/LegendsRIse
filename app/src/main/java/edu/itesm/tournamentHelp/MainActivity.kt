package edu.itesm.tournamentHelp





import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import edu.itesm.tournamentHelp.Security.verifyPurchase
import edu.itesm.tournamentHelp.databinding.ActivityMainBinding
import edu.itesm.tournamentHelp.databinding.ActivityTourneysBinding
import java.io.IOException


data class Pokemon(val id: String, val nombre: String,
                   val desc: String, val numb: String){
    constructor():this("","", "", "")
}
data class Team(val id: String, val nombre: String,
                   val membs: String){
    constructor():this("","", "")
}

class MainActivity : AppCompatActivity(), PurchasesUpdatedListener{
    private var billingClient : BillingClient? = null
    private lateinit var bind : ActivityMainBinding
    var purchaseButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        purchaseButton = findViewById<View>(R.id.purchase_button) as Button?
        billingClient = BillingClient.newBuilder(this@MainActivity)
                .enablePendingPurchases().setListener(this).build()



        val ring: MediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.lol)
        ring.start()






    }
    public fun addPokemon(view: View){
        val nombre = findViewById<EditText>(R.id.namee).text
        val desc = findViewById<EditText>(R.id.desc).text
        val numb = findViewById<EditText>(R.id.teamz).text


        if(nombre.isNotEmpty() && nombre.isNotBlank() && desc.isNotEmpty() && desc.isNotBlank()){

            val usuario = Firebase.auth.currentUser
            reference = database.getReference("Tournaments")

            val id = reference.push().key
            val pokemon = Pokemon(
                id.toString(),
                nombre.toString(),
                desc.toString(),
                numb.toString()

            )



            reference.child(id!!).setValue(pokemon)

            Toast.makeText(this, "New Tournament Registered", Toast.LENGTH_LONG).show()

        }else{
            Toast.makeText(this, "Cannot Register New Tournament, check data again", Toast.LENGTH_LONG).show()
        }
        bundle.putString("edu_itesm_pokedex_main", "added_pokemon")
        analytics.logEvent("main", bundle)
    }
    public fun addTeam(view: View){
        val nombre = findViewById<EditText>(R.id.namee).text
        val memb = findViewById<EditText>(R.id.desc).text



        if(nombre.isNotEmpty() && nombre.isNotBlank() && memb.isNotEmpty() && memb.isNotBlank()){

            val usuario = Firebase.auth.currentUser
            reference = database.getReference("Teams/${usuario.uid}")

            val id = reference.push().key
            val pokemon = Team(
                    id.toString(),
                    nombre.toString(),
                    memb.toString(),

            )



            reference.child(id!!).setValue(pokemon)

            Toast.makeText(this, "New Team added", Toast.LENGTH_LONG).show()

        }else{
            Toast.makeText(this, "Cannot Register New Team, check data again", Toast.LENGTH_LONG).show()
        }
        bundle.putString("edu_itesm_pokedex_main", "added_pokemon")
        analytics.logEvent("main", bundle)
    }
    public fun getPokemons(view : View){

        startActivity(Intent(this, TourneysActivity::class.java))

    }
    private val preferenceObject: SharedPreferences
        get() = applicationContext.getSharedPreferences(PREF_FILE, 0)

    private val preferenceEditObject: SharedPreferences.Editor
        get() {
            val pref: SharedPreferences = applicationContext.getSharedPreferences(PREF_FILE, 0)
            return pref.edit()
        }

    private val purchaseValueFromPref: Boolean
        get() = preferenceObject.getBoolean(PURCHASE_KEY, false)

    private fun savePurchaseValueToPref(value: Boolean) {
        preferenceEditObject.putBoolean(PURCHASE_KEY, value).commit()
    }
    //initiate purchase on button click
    fun purchase(view: View?) {
        //check if service is already connected

        if (billingClient!!.isReady) {
            initiatePurchase()
        }
        //else reconnect service
        else {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase()
                    } else {
                        Toast.makeText(applicationContext, "Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onBillingServiceDisconnected() {}
            })
        }
    }
    private fun initiatePurchase() {

        val skuList: MutableList<String> = ArrayList()
        skuList.add(PRODUCT_ID)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(INAPP)

        billingClient!!.querySkuDetailsAsync(params.build())
        { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (skuDetailsList != null && skuDetailsList.size > 0) {
                    val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[0])
                            .build()
                    billingClient!!.launchBillingFlow(this@MainActivity, flowParams)
                } else {
                    //try to add item/product id "purchase" inside managed product in google play console

                    Toast.makeText(applicationContext, "Purchase Item not Found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext,
                        " Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        //if item newly purchased

        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            val queryAlreadyPurchasesResult = billingClient!!.queryPurchases(INAPP)
            val alreadyPurchases: List<Purchase>? = queryAlreadyPurchasesResult.purchasesList
            if (alreadyPurchases != null) {
                handlePurchases(alreadyPurchases)
            }
        }
        //if purchase cancelled
        else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(applicationContext, "Purchase Canceled", Toast.LENGTH_SHORT).show()
        }
        // Handle any other error msgs
        else {
            Toast.makeText(applicationContext, "Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
        }
    }
    fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            //if item is purchased

            if (PRODUCT_ID == purchase.sku && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
                    // Invalid purchase
                    // show error to user

                    Toast.makeText(applicationContext, "Error : Invalid Purchase", Toast.LENGTH_SHORT).show()
                    return
                }
                // else purchase is valid
                //if item is purchased and not acknowledged


                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()
                    billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase)
                }
                //else item is purchased and also acknowledged
                else {
                    // Grant entitlement to the user on item purchase
                    // restart activity

                    if (!purchaseValueFromPref) {
                        savePurchaseValueToPref(true)
                        Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT).show()
                        recreate()
                    }
                }
            }
            //if purchase is pending
            else if (PRODUCT_ID == purchase.sku && purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Toast.makeText(applicationContext,
                        "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show()
            }
            //if purchase is refunded or unknown
            else if (PRODUCT_ID == purchase.sku && purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                savePurchaseValueToPref(false)
                purchaseButton!!.visibility = View.VISIBLE
                Toast.makeText(applicationContext, "Purchase Status Unknown", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var ackPurchase = AcknowledgePurchaseResponseListener { billingResult ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            //if purchase is acknowledged
            // Grant entitlement to the user. and restart activity

            savePurchaseValueToPref(true)
            Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT).show()
            recreate()
        }


    }
    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     *
     * Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     *
     */
    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.

            val base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6ZyxX/sy6M51pZAabDQwqiZv09NZMbYCr3P6AmowYiKJtALwqxfijZk8dWuvII0aXi7pNT7FLr7AzuieAB58F08VeYG48iNhMDwgs9ZAuF8cFh2dYXTPC663r6+c0upzwf1vLW0LMyawcqSf8dibtj6VQAFvhLtuHt6j/5k2bbx7GtI/NoZrD0FOU1NJius4C6aQM080Fk4ff3ZywPx7YhC+C8vW3TE2qNWQ+YcWw7cP56zoF00RoRlWDCjp+fGZbL5aCYydj6I4CkrQBfSqvEfDcij04PEACXv9OAw8Bs4FUAHBTOFXUCwpIKO7royAiRt6swl+t387tvrhlfUqWwIDAQAB"
            verifyPurchase(base64Key, signedData, signature)
        } catch (e: IOException) {
            false
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (billingClient != null) {
            billingClient!!.endConnection()
        }
    }

    companion object {
        const val PREF_FILE = "MyPref"
        const val PURCHASE_KEY = "purchase"
        const val PRODUCT_ID = "002"
    }
}


