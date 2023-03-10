package com.antique.home.data

import androidx.annotation.Keep

@Keep
data class Block(
    val blockedUser: String,  //차단할 사용자 고유 ID
    val date: String          //차단한 날짜
) {
    constructor() : this("", "")
}
