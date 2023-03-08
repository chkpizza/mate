package com.antique.mate.usecase

import android.util.Log
import com.antique.mate.data.User
import com.antique.mate.repo.AuthRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class RegisterUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Boolean {
        val uid = Firebase.auth.currentUser?.uid ?: return false
        val nickName = System.currentTimeMillis().toString().run {
            "익명의수험생_${substring(length - 4)}"
        }
        Log.d("NickName", nickName)
        val profileImage = "https://img.etoday.co.kr/pto_db/2019/12/600/20191225184317_1405975_664_866.jpg"
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        return authRepository.registerUser(User(uid, nickName, profileImage, date))
    }
}