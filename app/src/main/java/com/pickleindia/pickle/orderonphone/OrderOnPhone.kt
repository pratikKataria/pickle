package com.pickleindia.pickle.orderonphone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.transition.MaterialSharedAxis
import com.pickleindia.pickle.R
import com.pickleindia.pickle.databinding.FragmentOrderOnPhoneBinding

class OrderOnPhone : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentOrderOnPhoneBinding>(inflater, R.layout.fragment_order_on_phone, container, false)

        enterTransition = MaterialSharedAxis.create(MaterialSharedAxis.Z, true)

        binding.closeImageButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }

}