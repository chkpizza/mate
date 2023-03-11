package com.antique.common

import androidx.annotation.Keep

@Keep
data class User(
    val uid: String,
    val nickName: String,
    val profileImage: String,
    val date: String
) {
    constructor() : this("", "", "", "")
}