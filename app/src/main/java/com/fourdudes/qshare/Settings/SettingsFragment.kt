package com.fourdudes.qshare.Settings

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fourdudes.qshare.MainActivity
import com.fourdudes.qshare.R
import com.fourdudes.qshare.data.ItemRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.api.services.drive.DriveScopes

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var repository: ItemRepository
    override fun onAttach(context: Context) {
        super.onAttach(context)
        repository = ItemRepository.getInstance(context)!!
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext)
        val googleAccountEmail = googleAccount?.email ?: "Not signed in"

        //set sign out behavior
        findPreference<Preference>(resources.getString(R.string.key_sign_out))?.apply {
            setOnPreferenceClickListener {
                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it )
                }
                builder?.apply {
                    setTitle("Sign Out?")
                    setMessage("You will need to sign in again to use QshaRe.")
                    setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .build()
                        val client = GoogleSignIn.getClient(requireActivity().applicationContext, signInOptions)
                        client.signOut()
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        })
//                        client.signOut().addOnCompleteListener {object: OnCompleteListener<Void> {
//                            override fun onComplete(p0: Task<Void>) {
//                                isEnabled = false
//                                summary = resources.getString(R.string.sign_out_summary_fmt, "Signed out")
//                            }
//                        }
//                        })
                    setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                        //do nothing
                    })
                }
                val dialog = builder?.create()
                dialog?.show()
                false
            }
            isEnabled = googleAccount != null
            summary = resources.getString(R.string.sign_out_summary_fmt, googleAccountEmail)
        }

        //set clear database behavior
        findPreference<Preference>(resources.getString(R.string.key_clear_history))?.setOnPreferenceClickListener {
            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it )
            }
            builder?.apply {
                setMessage("Are you sure you would like to clear the database? This action cannot be undone! (and it makes us sad :( )")
                setTitle("Confirm Deletion")
                setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                    repository.deleteItems()
                })
                setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                    //do nothing
                })
            }
            val dialog = builder?.create()
            dialog?.show()
            false
        }
    }
}
