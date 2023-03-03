package com.example.myvault

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class launch_screen : AppCompatActivity() {
    private lateinit var loginbt:android.widget.Button
    private lateinit var signupbt:android.widget.Button
    private lateinit var UserAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_screen)
        loginbt=findViewById(R.id.loginButton)
        signupbt=findViewById(R.id.signUpButton)
        UserAuth= FirebaseAuth.getInstance()

        val prefs=getSharedPreferences("UserData", MODE_PRIVATE)
        val key= prefs.getString("UserUID","UID")
        if(!key.equals("UID"))
        {
            startActivity(Intent(this,MainActivity::class.java))
        }
        loginbt.setOnClickListener {
            val intent=Intent(this,signIn::class.java)
            startActivity(intent )
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        signupbt.setOnClickListener {
            val intent=Intent(this,SignUp::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }
}