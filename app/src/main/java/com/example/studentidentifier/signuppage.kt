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

class signuppage : AppCompatActivity() {
    private var isButtonPressed=false
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var auth: FirebaseAuth
        auth = Firebase.auth
        setContentView(R.layout.activity_signuppage)
        val regem=findViewById<EditText>(R.id.regemail)
        val regpass=findViewById<EditText>(R.id.regpassword)
        val reg = findViewById<Button>(R.id.register)
        val eye=findViewById<Button>(R.id.regpassview)
        val bar=findViewById<ProgressBar>(R.id.regprog)
        eye.setOnClickListener {
            if(isButtonPressed){
                regpass.transformationMethod= PasswordTransformationMethod()
                isButtonPressed=false
            }
            else{
                regpass.transformationMethod=null
                isButtonPressed=true
            }
        }
        reg.setOnClickListener {
            bar.visibility=View.VISIBLE
            var email:String=regem.text.toString()
            var password:String=regpass.text.toString()
            if(regem.text.isEmpty()||regpass.text.isEmpty()){
                Toast.makeText(this,"FIELDS CANNOT BE EMPTY",Toast.LENGTH_LONG).show()
                bar.visibility=View.GONE
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                      bar.visibility=View.GONE
                        val intent3= Intent(this,MainActivity::class.java)
                        startActivity(intent3)
                        Toast.makeText(this,"REGISTERED",Toast.LENGTH_LONG).show()
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        bar.visibility=View.GONE
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

        }
    }
}