package com.example.myvault

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var nameField:com.google.android.material.textfield.TextInputEditText
    private lateinit var mailField:com.google.android.material.textfield.TextInputEditText
    private lateinit var passField:com.google.android.material.textfield.TextInputEditText
    private lateinit var jumpBt: Button
    private lateinit var signUpBt: Button
    private lateinit var UserAuth: FirebaseAuth
    private lateinit var dbRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)
        UserAuth= FirebaseAuth.getInstance()
        nameField=findViewById(R.id.nameField)
        mailField=findViewById(R.id.emailField)
        passField=findViewById(R.id.passwordField)
        jumpBt=findViewById(R.id.JumpSignInBt)
        signUpBt=findViewById(R.id.signUpButton)

        jumpBt.setOnClickListener {
            val intent= Intent(this,signIn::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            finish()
        }

        signUpBt.setOnClickListener {
            val name=nameField.text.toString()
            val mail=mailField.text.toString()
            val pass=passField.text.toString()
            if(TextUtils.isEmpty(nameField.text))
            {
                Toast.makeText(this,"Name Cannot Be Empty!", Toast.LENGTH_SHORT).show()
            }
            else
               if(TextUtils.isEmpty(mailField.text))
               {
                Toast.makeText(this,"Email Cannot Be Empty!", Toast.LENGTH_SHORT).show()
               }
               else
                if(TextUtils.isEmpty(passField.text))
                {
                    Toast.makeText(this,"Password Cannot Be Empty!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    SignUserUp(name,mail,pass)

                }


        }

    }
    private fun SignUserUp(name:String,email:String,password:String)
    {
        UserAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful)
                {
                    val prefs=getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor=prefs.edit()
                    editor.putString("UserUID",UserAuth.currentUser?.uid)
                    editor.commit()
                    addUserToDatabase(name,email,UserAuth.currentUser?.uid!!)
                    val intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this,"User Sign Up Failed",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String)
    {
        dbRef=FirebaseDatabase.getInstance().getReference()

        dbRef.child("User").child(uid).setValue(User(name, email, uid))
    }
}