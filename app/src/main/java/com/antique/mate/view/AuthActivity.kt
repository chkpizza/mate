package com.antique.mate.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.antique.mate.R
import com.antique.mate.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInIntentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)

        initialize()
    }

    private fun initialize() {
        setupEdgeStyle()
        setupLauncher()
        setupViewListener()
        setupViewState()
        setupObservers()
    }

    private fun setupEdgeStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        } else {
            binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    private fun setupLauncher() {
        signInIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Snackbar.make(binding.root, "로그인에 실패했습니다 인터넷 연결을 확인해주세요", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupViewListener() {
        binding.googleLoginButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            signIn()
        }
    }

    private fun setupViewState() {

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInIntentLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    /*
                    val date = System.currentTimeMillis().toString()
                    val nickName = "수험생_${date.substring(date.length - 4)}"
                    //authViewModel.registration(User(uid = Helper.getUid(), nickName = nickName, date = date))

                     */
                    startActivity(Intent(this, MainActivity::class.java))
                } else {

                }
            }
    }


    private fun setupObservers() {
        /*
        authViewModel.image.observe(this) {
            Glide.with(binding.onBoardingImageView.context)
                .load(it)
                .into(binding.onBoardingImageView)
        }

        authViewModel.registration.observe(this) {
            Helper.isSignOut.value = false
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

         */
    }


    override fun onStart() {
        super.onStart()

        Firebase.auth.currentUser?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}