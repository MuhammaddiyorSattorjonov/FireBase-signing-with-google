package com.example.firebasesignwithgoogle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (auth.currentUser!=null){
//            val btn = findViewById<Button>(R.id.btn_registr)
//            btn.text = auth.currentUser?.displayName
//            btn.isEnabled = false
//        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
        findViewById<Button>(R.id.btn_registr)
            .setOnClickListener {
                val sigInIntent = googleSignInClient.signInIntent
                startActivityForResult(sigInIntent, 1)
            }
        findViewById<Button>(R.id.btn_registr)
            .setOnLongClickListener {
                auth.signOut()
                Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show()
                true
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "onActivityResult: ${account.displayName}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d(TAG, "onActivityResult: failure ${e.message}")
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in succes,update UI with the signed-in user's information
                    Log.d(TAG, "firebaseAuthWithGoogle: succes")
                    val user = auth.currentUser
                    //updateUI (user)
                    Toast.makeText(this, "${user?.email}", Toast.LENGTH_SHORT).show()
                } else {
                    // if sign in fails,display a message to the user
                    Log.d(TAG, "firebaseAuthWithGoogle: failure", task.exception)
                    //updateUI (null)
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}