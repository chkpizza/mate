package com.antique.mate.data

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
