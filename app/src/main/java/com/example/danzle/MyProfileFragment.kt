package com.example.danzle

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.MyProfileResponse
import com.example.danzle.databinding.FragmentMyProfileBinding
import com.example.danzle.myprofile.editProfile.EditProfile
import com.example.danzle.myprofile.myScore.MyScore
import com.example.danzle.myprofile.myVideo.MyVideo
import com.example.danzle.startPage.FirstStart
import com.example.danzle.startPage.token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!


    var username: String = ""
    var email: String = ""
    var image: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // converting screen when clicking
        // editProfile
        binding.editProfile.setOnClickListener { startActivity(Intent(requireContext(), EditProfile::class.java)) }

        // myVideo
        binding.myVideo.setOnClickListener { startActivity(Intent(requireContext(), MyVideo::class.java)) }

        // myScore
        binding.myScore.setOnClickListener { startActivity(Intent(requireContext(), MyScore::class.java)) }

        // logout
        // click button -> logout dialog -> (ok) start first
        binding.logout.setOnClickListener {
            showLogoutDialog()
        }


    }

    private fun showLogoutDialog() {
        val view = layoutInflater.inflate(R.layout.logout_dialog, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        alertDialog.setCancelable(false)
        alertDialog.window!!.attributes.windowAnimations = R.style.dialogAniamtion
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)

        alertDialog.window!!.setGravity(Gravity.BOTTOM)

        // click cancelButton
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        // click logoutButton
        logoutButton.setOnClickListener {
            val intent = Intent(requireContext(), FirstStart::class.java)

            // clear activity log
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    // getting some information from server
    private fun retrofitMyProfileMain(){
       val retrofit = RetrofitApi.getMyProfileServiceInstance()
        retrofit.getMyProfile(token)
            .enqueue(object : Callback<MyProfileResponse>{
                override fun onResponse(call: Call<MyProfileResponse>, response: Response<MyProfileResponse>) {
                    if (response.isSuccessful){
                        val myProfileResponse = response.body()

                        username = myProfileResponse!!.username
                        email = myProfileResponse!!.email

                        // about account information
                        binding.usernameTextview.text = username
                        binding.emailTextview.text = email

                    }
                }

                override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {
                    Log.d("Debug", "MyProfile / Error: ${t.message}")
                    Toast.makeText(requireContext(), "Error to retrieve data", Toast.LENGTH_SHORT).show()

                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}