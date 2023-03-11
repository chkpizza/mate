package com.antique.home.usecase

import com.antique.home.repo.PostDetailsRepository

class BlockUserUseCase(private val postDetailsRepository: PostDetailsRepository) {
    suspend operator fun invoke(blockUid: String): Boolean = postDetailsRepository.blockUser(blockUid)
}