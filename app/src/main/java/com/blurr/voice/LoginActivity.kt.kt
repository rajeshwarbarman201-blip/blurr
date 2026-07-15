package com.blurr.voice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}        progressBar.visibility = View.VISIBLE
        loadingText.visibility = View.VISIBLE
        signInButton.isEnabled = false

        Log.d("LoginActivity", "Starting Google Sign-In process")
        Log.d("LoginActivity", "Using web client ID: ${getString(R.string.default_web_client_id)}")

        // 4. Launch the sign-in flow
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    Log.d("LoginActivity", "One Tap UI started successfully")
                    // The BeginSignInResult contains a PendingIntent
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    // Launch the intent sender
                    googleSignInLauncher.launch(intentSenderRequest)
                } catch (e: Exception) {
                    Log.e("LoginActivity", "Couldn't start One Tap UI: ${e.localizedMessage}", e)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    FirebaseCrashlytics.getInstance()
                        .log("Failed to start One Tap UI: ${e.localizedMessage}")
                    Toast.makeText(
                        this,
                        "Sign-in UI failed to start: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar.visibility = View.GONE
                    loadingText.visibility = View.GONE
                    signInButton.isEnabled = true
                }
            }
            .addOnFailureListener(this) { e ->
                Log.e("LoginActivity", "Sign-in failed: ${e.localizedMessage}", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                FirebaseCrashlytics.getInstance()
                    .log("Google One Tap sign-in failed: ${e.localizedMessage}")
                Toast.makeText(this, "Sign-in failed: ${e.localizedMessage}", Toast.LENGTH_LONG)
                    .show()
                progressBar.visibility = View.GONE
                loadingText.visibility = View.GONE
                signInButton.isEnabled = true
            }
    }

    private fun startPostAuthFlow(isNewUser: Boolean) {
        val profileManager = UserProfileManager(this)
        val user = firebaseAuth.currentUser
        // Prefer Firebase displayName; if absent (new custom token user), use the lastEnteredName fallback
        val name = user?.displayName ?: lastEnteredName ?: "Unknown"
        val email = user?.email ?: "unknown"
        profileManager.saveProfile(name, email)

        lifecycleScope.launch {
            val onboardingManager = OnboardingManager(this@LoginActivity)
            if (isNewUser) {
                val freemiumManager = FreemiumManager()
                freemiumManager.provisionUserIfNeeded()
            }
            if (onboardingManager.isOnboardingCompleted()) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@LoginActivity, OnboardingPermissionsActivity::class.java))
            }
            finish()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                // Hide progress bar and loading text when Firebase authentication completes
                progressBar.visibility = View.GONE
                loadingText.visibility = View.GONE
                signInButton.isEnabled = true

                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false

                    Log.d("LoginActivity", "signInWithCredential:success")
                    // 1. Get the successfully signed-in user
                    val user = firebaseAuth.currentUser
                    val name = user?.displayName
                    val email = user?.email
                    val profileManager = UserProfileManager(this)

                    // 2. Check if name and email are not null
                    if (name != null && email != null) {
                        // 3. Create an instance of your UserProfileManager

                        // 4. Save the profile information
                        profileManager.saveProfile(name, email)
                        Log.d(
                            "LoginActivity",
                            "User profile saved: Name='$name', Email='$email'"
                        )
                    } else {
                        profileManager.saveProfile("Unknown", "unknown")
                        Log.w("LoginActivity", "User name or email was null, profile not saved.")
                    }

                    lifecycleScope.launch {
                        val onboardingManager = OnboardingManager(this@LoginActivity)

                        if (isNewUser) {
                            Log.d(
                                "LoginActivity",
                                "New user detected. Provisioning freemium account."
                            )
                            val freemiumManager = FreemiumManager()
                            freemiumManager.provisionUserIfNeeded()
                        }

                        // CHECK THE LOCAL FLAG INSTEAD OF isNewUser
                        if (onboardingManager.isOnboardingCompleted()) {
                            Log.d(
                                "LoginActivity",
                                "Onboarding already completed on this device. Launching main activity."
                            )
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        } else {
                            Log.d(
                                "LoginActivity",
                                "Onboarding not completed. Launching permissions stepper."
                            )
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    OnboardingPermissionsActivity::class.java
                                )
                            )
                        }
                        finish()
                    }
                } else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                    task.exception?.let { exception ->
                        FirebaseCrashlytics.getInstance().recordException(exception)
                        FirebaseCrashlytics.getInstance()
                            .log("Firebase authentication failed: ${exception.localizedMessage}")
                    }
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
