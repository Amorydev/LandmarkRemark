package com.amory.landmarkremark.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amory.landmarkremark.R
import com.amory.landmarkremark.databinding.ActivityRegisterBinding
import com.amory.landmarkremark.viewModel.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth:FirebaseAuth
    private val viewModel : RegisterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        onClickListener()
        observerData()
    }

    private fun observerData() {
        viewModel.resultSignUp.observe(this){result ->
            if (result!!){
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun onClickListener() {
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this,
                    "Vui lòng nhập đúng định dạng email"
                    , Toast.LENGTH_SHORT
                ).show()
            }
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    viewModel.createAccount(email, pass)
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }
}