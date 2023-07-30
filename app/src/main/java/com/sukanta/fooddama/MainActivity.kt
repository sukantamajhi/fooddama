package com.sukanta.fooddama

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_main)

        val email = findViewById<TextInputEditText>(R.id.emailinput)
        val password = findViewById<TextInputEditText>(R.id.passwordinput)
        val btn = findViewById<Button>(R.id.loginBtn)
        val signUp = findViewById<TextView>(R.id.redirect_to_signup)

        btn.setOnClickListener {
            signIn(email.text.toString(), password.text.toString())
        }

        // Redirect to signup page
        signUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            startActivity(intent)
            this.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        Log.d("USER", currentUser?.email.toString())
        if (currentUser !== null) {
            reload()
        }
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
        Log.d(TAG, user.toString())
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "LOGIN"
    }
}