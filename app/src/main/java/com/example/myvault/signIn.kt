package com.example.myvault

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class signIn : AppCompatActivity() {
    private lateinit var mailField:com.google.android.material.textfield.TextInputEditText
    private lateinit var passField:com.google.android.material.textfield.TextInputEditText
    private lateinit var jumpBt:Button
    private lateinit var signInBt: Button
    private lateinit var UserAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        UserAuth= FirebaseAuth.getInstance()
        mailField=findViewById(R.id.emailField)
        passField=findViewById(R.id.passwordField)
        jumpBt=findViewById(R.id.JumpSignUpBt)
        signInBt=findViewById(R.id.signInButton)
        jumpBt.setOnClickListener {
            val intent=Intent(this,SignUp::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            finish()
        }
        signInBt.setOnClickListener {
            val mail=mailField.text.toString()
            val pass=passField.text.toString()
            if(TextUtils.isEmpty(mailField.text))
            {
                Toast.makeText(this,"Email Cannot Be Empty!", Toast.LENGTH_SHORT).show()
            }
            else
                if(TextUtils.isEmpty(passField.text))
                {
                    Toast.makeText(this,"Password Cannot Be Empty!", Toast.LENGTH_SHORT).show()
                }
                else {
                    SignUserIn(mail, pass)
                }
        }
    }
    private fun SignUserIn(email:String,password:String)
    {
        UserAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful)
                {
                    val prefs=getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor=prefs.edit()
                    editor.putString("UserUID",UserAuth.currentUser?.uid!!)
                    editor.commit()
                    val intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this,"User Sign In Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}