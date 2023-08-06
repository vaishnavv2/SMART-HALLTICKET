package com.example.studentidentifier

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class loginpage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var isButtonPressed=false
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)
        auth = Firebase.auth
        val logem=findViewById<EditText>(R.id.logemail)
        val logpass=findViewById<EditText>(R.id.logpswd)
        val login=findViewById<Button>(R.id.logsign)
        val eye=findViewById<Button>(R.id.passwordview)
        val bar=findViewById<ProgressBar>(R.id.barlog)
        eye.setOnClickListener {
        if(isButtonPressed){
            logpass.transformationMethod=PasswordTransformationMethod()
            isButtonPressed=false
        }
            else{
                logpass.transformationMethod=null
            isButtonPressed=true
            }
        }
         login.setOnClickListener {
             bar.visibility=View.VISIBLE
             var lemail:String=logem.text.toString()
             var lpassword:String=logpass.text.toString()
             if(logem.text.isEmpty()||logpass.text.isEmpty()){
                 Toast.makeText(this,"FIELDS CANNOT BE EMPTY", Toast.LENGTH_LONG).show()
                 bar.visibility= View.GONE
                 return@setOnClickListener
             }

             else{
                 auth.signInWithEmailAndPassword(lemail, lpassword)
                     .addOnCompleteListener(this) { task ->
                         if (task.isSuccessful) {
                             bar.visibility=View.GONE
                             val user = auth.currentUser
                             val intent4= Intent(this,functionpage::class.java)
                             startActivity(intent4)

                         }
                           else {
                             bar.visibility= View.GONE
                             Toast.makeText(
                                 baseContext,
                                 "Authentication failed.",
                                 Toast.LENGTH_SHORT,
                             ).show()

                         }
                     }
             }
         }
    }}
