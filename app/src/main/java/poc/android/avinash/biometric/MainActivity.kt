package poc.android.avinash.biometric

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import android.util.Log
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*

/**
 * A screen that offers BiometricPrompt auth dialog for android P devices.
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnShowDialog.setOnClickListener { showBiometricAuthDialog() }
    }

    private fun showBiometricAuthDialog() {
        val cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            showMessage("CancellationListener setOnCancelListener triggered")
        }

        /**
         * Currently BiometricPrompt API is supported only in Android P(version 28) devices.
         * For devices below Android version 28 Google is planning to provide Biometric Prompt Support Compat Library.
         * This will be released in subsequent versions.
         */
        BiometricPrompt.Builder(this@MainActivity)
                .setDescription("Test Biometric Dialog")
                .setTitle("Biometric dialog")
                .setNegativeButton("Cancel", mainExecutor, DialogInterface.OnClickListener { dialogInterface, i ->
                    showMessage("Cancel button clicked")
                })
                .build()
                .authenticate(cancellationSignal, mainExecutor, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        showMessage("Authentication successful")
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        /**
                         * Authentication failure takes place if we provide incorrect or unknown biometric credentials
                         */
                        showMessage("Authentication Failed")
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        /**
                         * Authentication errors can be of different types like lockout, hardware problems, issues in captured biometrics, timeouts,
                         * vendor error, etc.
                         */
                        showMessage("Authentication Error $errorCode $errString")
                        when (errorCode) {
                            BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT_PERMANENT,
                            BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT,
                            BiometricPrompt.BIOMETRIC_ERROR_CANCELED,
                            BiometricPrompt.BIOMETRIC_ERROR_HW_NOT_PRESENT,
                            BiometricPrompt.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                                showMessage("Biometric hardware and lockout issues")
                            }
                            BiometricPrompt.BIOMETRIC_ERROR_NO_SPACE,
                            BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS -> {
                                showMessage("Biometric settings changes i.e either biometric is removed or error while adding biometric")
                            }
                            BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED -> {
                                showMessage("User presses back button when biometric dialog is displayed")
                            }
                            BiometricPrompt.BIOMETRIC_ERROR_TIMEOUT,
                            BiometricPrompt.BIOMETRIC_ERROR_VENDOR,
                            BiometricPrompt.BIOMETRIC_ERROR_UNABLE_TO_PROCESS -> {
                                showMessage("Biometric timeout, vendor or processing errors")
                            }
                            else -> {
                                showMessage("Generic biometric error")
                            }

                        }
                    }

                    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                        super.onAuthenticationHelp(helpCode, helpString)
                        /**
                         * Help provided by biometric dialog during authentication
                         */
                        showMessage("Authentication Error $helpCode $helpString")
                        when (helpCode) {
                            BiometricPrompt.BIOMETRIC_ACQUIRED_IMAGER_DIRTY,
                            BiometricPrompt.BIOMETRIC_ACQUIRED_INSUFFICIENT,
                            BiometricPrompt.BIOMETRIC_ACQUIRED_PARTIAL,
                            BiometricPrompt.BIOMETRIC_ACQUIRED_TOO_FAST,
                            BiometricPrompt.BIOMETRIC_ACQUIRED_TOO_SLOW -> {
                                showMessage("Quality of image while IRIS and Face scan is blurry OR biometric moved fast or slow")
                            }
                            else -> {
                                showMessage("Generic biometric help")
                            }
                        }
                    }
                })
    }

    fun showMessage(message: String) {
        Log.d(TAG, message)
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

}
