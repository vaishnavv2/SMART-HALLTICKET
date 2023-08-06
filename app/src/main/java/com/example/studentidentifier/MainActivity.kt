package com.example.studentidentifier

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginp = findViewById<Button>(R.id.btn1)
        val signupp = findViewById<Button>(R.id.btn2)



        loginp.setOnClickListener {
            val intent1= Intent(this,loginpage::class.java)
            startActivity(intent1)
        }
        signupp.setOnClickListener {
            val intent2= Intent(this,signuppage::class.java)
            startActivity(intent2)
        }
    }
}

