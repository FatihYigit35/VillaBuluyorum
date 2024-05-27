package com.androiddevelopers.villabuluyorum.view.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentRegisterBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.view.user.BottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.login.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var verificationDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null
    val RC_SIGN_IN = 20
    private lateinit var mGoogleSignInClient: GoogleSignInClient


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(requireContext()).create()
        verificationDialog = AlertDialog.Builder(requireContext()).create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        binding.btnGoogle.setOnClickListener {
            googleSignIn()
        }
        binding.tvGoToLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToSignInFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassworad.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            viewModel.signUp(email, password, confirmPassword)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnPhone.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToPhoneLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }
        setupDialogs()
        observeLiveData()
    }


    private fun observeLiveData() {
        viewModel.authState.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.pbRegister.visibility = View.INVISIBLE
                    binding.btnRegister.isEnabled = true
                }

                Status.LOADING -> {
                    binding.pbRegister.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }

                Status.SUCCESS -> {
                    binding.pbRegister.visibility = View.INVISIBLE
                    binding.btnRegister.isEnabled = true
                    if (it.data == true) {
                        enter()
                    }
                }
            }
        })

        viewModel.registrationError.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    if (it.data == true) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    } else {
                        binding.etConfirmPassword.error = it.message
                    }
                }

                else -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.isVerificationEmailSent.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    verificationDialog?.show()
                }

                Status.ERROR -> {
                    errorDialog?.show()
                }

                else -> {
                    errorDialog?.show()
                }
            }
        })

    }

    private fun enter() {
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        startActivity(intent)
    }

    private fun setupDialogs() {
        verificationDialog?.setTitle("Email Doğrulama")
        verificationDialog?.setMessage("Doğrulama e-postası gönderildi. Lütfen e-posta kutunuzu kontrol edin.")
        verificationDialog?.setCancelable(false)

        verificationDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->
            findNavController().popBackStack()
        }

        errorDialog?.setTitle("Hata")
        errorDialog?.setMessage("e-posta gönderilemedi")
        errorDialog?.setCancelable(false)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tekrar gönder") { _, _ ->

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun googleSignIn() {
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task.addOnSuccessListener {
                    val account = it.requestExtraScopes()
                    viewModel.signInWithGoogle(account.idToken)
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Giriş Yapılamadı",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Giriş Yapılamadı : " + e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}