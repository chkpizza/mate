package com.antique.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.antique.home.OnReportClickListener
import com.antique.home.R
import com.antique.home.databinding.FragmentReportCommentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReportCommentFragment(
    private val onReportClickListener: OnReportClickListener
) : BottomSheetDialogFragment() {
    private var _binding: FragmentReportCommentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_comment, container, false)
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
        binding.reportCommentView.setOnClickListener {
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