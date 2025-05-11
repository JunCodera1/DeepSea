package com.example.deepsea.data.repository

import com.example.deepsea.data.api.UnitGuideService
import com.example.deepsea.data.dto.UnitGuideDto
import com.example.deepsea.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UnitGuideRepository @Inject constructor(
    private val unitGuideService: UnitGuideService
) {
    suspend fun getUnitGuide(unitId: Long): Flow<NetworkResult<UnitGuideDto>> = flow {
        emit(NetworkResult.Loading())

        try {
            val response = unitGuideService.getUnitGuide(unitId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                } ?: emit(NetworkResult.Error("Empty response body"))
            } else {
                emit(NetworkResult.Error("Failed to fetch guide data: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.message}"))
        }
    }
}
