package com.example.myvault

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var nameField:com.google.android.material.textfield.TextInputEditText
    private lateinit var mailField:com.google.android.material.textfield.TextInputEditText
    private lateinit var nameBox:com.google.android.material.textfield.TextInputLayout
    private lateinit var passField:com.google.android.material.textfield.TextInputEditText
    private lateinit var mailBox:com.google.android.material.textfield.TextInputLayout
    private lateinit var passBox:com.google.android.material.textfield.TextInputLayout
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
        nameBox=findViewById(R.id.nameBox)
        mailField=findViewById(R.id.emailField)
        mailBox=findViewById(R.id.mailBox)
        passField=findViewById(R.id.passwordField)
        passBox=findViewById(R.id.passBox)
        jumpBt=findViewById(R.id.JumpSignInBt)
        signUpBt=findViewById(R.id.signUpButton)

        jumpBt.setOnClickListener {
            val intent= Intent(this,signIn::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            finish()
        }
        nameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(nameField.text))
                    nameBox.error = "THIS FIELD CANNOT BE EMPTY!"
                else
                    nameBox.error=null
            }
        })
        mailField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(mailField.text))
                    mailBox.error = "THIS FIELD CANNOT BE EMPTY!"
                else
                    mailBox.error=null
            }
        })
        passField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(passField.text))
                    passBox.error = "THIS FIELD CANNOT BE EMPTY!"
                else
                    if(checkPasswordMeetsRequirements(passField.text.toString(), passBox))
                        passBox.error=null
            }

        })
        signUpBt.setOnClickListener {
            val name=nameField.text.toString()
            val mail=mailField.text.toString()
            val pass=passField.text.toString()
            if(TextUtils.isEmpty(nameField.text))
            {
                Toast.makeText(this,"Name Cannot Be Empty!", Toast.LENGTH_SHORT).show()
                nameBox.error=" This Field Cannot be Empty"
            }
            else
               if(TextUtils.isEmpty(mailField.text))
               {
                Toast.makeText(this,"Email Cannot Be Empty!", Toast.LENGTH_SHORT).show()
                   mailBox.error=" This Field Cannot be Empty"
               }
               else
                if(TextUtils.isEmpty(passField.text))
                {
                    Toast.makeText(this,"Password Cannot Be Empty!", Toast.LENGTH_SHORT).show()
                    passBox.error=" This Field Cannot be Empty"
                }
                else
                   {if(checkPasswordMeetsRequirements(pass,passBox))
                        SignUserUp(name,mail,pass)
                    }
        }
    }
    private fun checkPasswordMeetsRequirements(pass:String,passBox:com.google.android.material.textfield.TextInputLayout):Boolean
    {
        if(pass.length<8) {
            passBox.error="Must have more than 8 characters"
            return false
        }
        if(!(pass.count(Char::isDigit)>0))
        {
            passBox.error="Must have a number"
            return false
        }
        if(!(pass.any(Char::isLowerCase)&&pass.any(Char::isUpperCase)))
        {
            passBox.error="Must have both LowerCase and UpperCase Characters"
            return false
        }
        if(!(pass.any { it in "!~@#$%^&*()_-+.,:;/" }))
        {
            passBox.error="Must include a special character"
            return false
        }
        return true
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
        dbRef.child("User").child(uid).child("Storage").setValue(StorageData("0","0","524288000"))
    }
}