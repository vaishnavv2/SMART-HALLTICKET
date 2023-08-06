package com.example.studentidentifier

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class  HallTicket : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hall_ticket)
        val studname = findViewById<TextView>(R.id.name)
        val branch = findViewById<TextView>(R.id.branch)
        val studphoto = findViewById<ImageView>(R.id.imageView)
        val sub1 = findViewById<TextView>(R.id.sub1)
        val sub2 = findViewById<TextView>(R.id.sub2)
        val sub3 = findViewById<TextView>(R.id.sub3)
        val sub4 = findViewById<TextView>(R.id.sub4)
        val dates1 = findViewById<TextView>(R.id.date1)
        val dates2 = findViewById<TextView>(R.id.date2)
        val dates3 = findViewById<TextView>(R.id.date3)
        val dates4 = findViewById<TextView>(R.id.date4)

        val intent = getIntent()
        val docid = intent.getStringExtra("key")
        val collectionReference = FirebaseFirestore.getInstance().collection("Student")
        collectionReference.document(docid.toString()).get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.exists()) {
                studname.text = querySnapshot.getString("Name")
                branch.text = querySnapshot.getString("Class")
                val subjects = querySnapshot.get("Subjects") as ArrayList<String>
                val date = querySnapshot.get("Dates") as ArrayList<String>
                sub1.text = subjects[0]
                sub2.text = subjects[1]
                sub3.text = subjects[2]
                sub4.text = subjects[3]
                dates1.text = date[0]
                dates2.text = date[1]
                dates3.text = date[2]
                dates4.text = date[3]



            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "$exception", Toast.LENGTH_LONG).show()
        }
    }
}
