package com.sukanta.fooddama

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_signup)

        val uName = findViewById<TextInputEditText>(R.id.userName)
        val email = findViewById<TextInputEditText>(R.id.emailinput)
        val pass = findViewById<TextInputEditText>(R.id.passwordinput)
//        val cPass = findViewById<TextInputEditText>(R.id.cPasswordinput)
        val btn = findViewById<Button>(R.id.signupBtn)
        val signIn = findViewById<TextView>(R.id.redirect_to_signin)

        btn?.setOnClickListener {
            createAccount(uName.text.toString(), email.text.toString(), pass.text.toString())
        }

        // Redirect to signup page
        signIn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            startActivity(intent)
            this.finish()
        }
    }

    private fun md5(md5: String): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            val array = md.digest(md5.toByteArray(charset("UTF-8")))
            val sb = StringBuffer()
            for (i in array.indices) {
                sb.append(Integer.toHexString(array[i].toInt() and 0xFF or 0x100).substring(1, 3))
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            println("$e <<-- Error")
        } catch (ex: UnsupportedEncodingException) {
            println("$ex <<-- Error")
        }
        return null
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun createAccount(userName: String, email: String, password: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://localhost:9001"

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
                val db = Firebase.firestore
                val userData = hashMapOf(
                    "id" to getRandomString(8),
                    "username" to userName,
                    "email" to email,
                    "password" to md5(password)
                )

                println("This is user data $userData")

                db.collection("users").add(userData).addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
                println(user)
                signIn(email, password)
            } else {
                // If sign in fails, display a message to the user.
//                signIn(email, password)
                Log.d("ERROR", "Something wrong happened")
            }
        }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
                updateUI(null)
            }
        }
        // [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "SIGNUP"
    }
}