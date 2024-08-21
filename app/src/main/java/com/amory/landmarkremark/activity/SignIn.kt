package com.amory.landmarkremark.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amory.landmarkremark.databinding.ActivitySignInBinding
import com.amory.landmarkremark.viewModel.SignInViewModel
import com.google.firebase.auth.FirebaseAuth

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var mAuth: FirebaseAuth
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        onClickListener()
        observerData()
    }
    private fun observerData() {
        viewModel.authResult.observe(this) { result ->
            if (result!!) {
                viewModel.pushUserIdToFirebase()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
    private fun onClickListener() {
        binding.button.setOnClickListener {
            val strEmail = binding.emailEt.text.toString()
            val strPassword = binding.passET.text.toString()
            viewModel.signInWithEmailAndPassword(strEmail, strPassword)
        }
        binding.tvNotAccount.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        if (viewModel.isSignUp()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}