package com.androiddevelopers.villabuluyorum.view.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.databinding.FragmentCreateUserNameBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.view.BottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.login.EntryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateUserNameFragment : Fragment() {

    private var _binding: FragmentCreateUserNameBinding? = null
    private val binding get() = _binding!!

    private var errorDialog: AlertDialog? = null

    private lateinit var viewModel: EntryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateUserNameBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[EntryViewModel::class.java]
        val view = binding.root
        return view
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(requireContext()).create()
        setupDialog()
        binding.btnCreateUserName.setOnClickListener {
            it.isEnabled = false
            val userName = binding.etUserName.text.toString()
            if (userName.isNotEmpty()){
                viewModel.createUserName(userName)
            }

        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        observeLiveData()
    }


    private fun goToHome(){
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }
    private fun setupDialog(){
        errorDialog?.setTitle("Kullanıcı adı")
        errorDialog?.setMessage("Farklı bir Kullanıcı adı girin")
        errorDialog?.setCancelable(true)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->

        }
    }
    private fun observeLiveData(){
        viewModel.authState.observe(viewLifecycleOwner,Observer{
            when(it.status){
                Status.SUCCESS->{
                    goToHome()
                }
                Status.ERROR->{
                    errorDialog?.show()
                }
                Status.LOADING->{
                    binding.pbUserName.visibility = View.VISIBLE
                }
            }
        })
    }
}