package edu.itesm.tournamentHelp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.itesm.tournamentHelp.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {


    private lateinit var bind : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        auth = Firebase.auth
        setLoginRegister()
    }

    override fun onStart() {
        super.onStart()

        val usuarioActivo = auth.currentUser
        if(usuarioActivo != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
    }

    private fun usuarioCreado(){
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle("Usuario")
            setMessage("Usuario creado! Bienvenido")
            setPositiveButton("Close",null)
            show()
        }
    }


    private fun setLoginRegister(){

        bind.registerbtn.setOnClickListener {
            if (bind.correo.text.isNotEmpty() && bind.password.text.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(

                        bind.correo.text.toString(),
                        bind.password.text.toString()

                ).addOnCompleteListener{
                    if(it.isSuccessful){
                        usuarioCreado()
                        bind.correo.text.clear()
                        bind.password.text.clear()
                    }
                }.addOnFailureListener {
                    when(it){
                    is FirebaseAuthInvalidCredentialsException ->  Toast.makeText(this, "Error: Correo no esta en el formato correcto ", Toast.LENGTH_LONG).show()
                    is FirebaseNetworkException -> Toast.makeText(this, "Error: No hay conexion", Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()}

            }}else{Toast.makeText(this, "Usuario y/o contrqaseña no pueden ir vacios", Toast.LENGTH_LONG).show()}
        }

        bind.loginbtn.setOnClickListener {
            if (bind.correo.text.isNotEmpty() && bind.password.text.isNotEmpty()){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    bind.correo.text.toString(),
                    bind.password.text.toString()
            ).addOnCompleteListener{
                if(it.isSuccessful){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this,"Correo y/o contraseña incorrectos", Toast.LENGTH_LONG).show()
                }}
            } else{Toast.makeText(this, "Usuario y/o contrqaseña no pueden ir vacios", Toast.LENGTH_LONG).show()}
        }
    }
}