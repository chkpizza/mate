package com.antique.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.antique.home.OnReportClickListener
import com.antique.home.R
import com.antique.home.data.PostDetailsUiState
import com.antique.home.databinding.FragmentReportPostBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ReportPostFragment(
    private val onReportClickListener: OnReportClickListener
) : BottomSheetDialogFragment() {
    private var _binding: FragmentReportPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_post, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupViewListener()
    }

    private fun setupViewListener() {
        binding.reportPostView.setOnClickListener {
            onReportClickListener.onClick(1)
            dismiss()
        }
        binding.blockUserView.setOnClickListener {
            onReportClickListener.onClick(2)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}