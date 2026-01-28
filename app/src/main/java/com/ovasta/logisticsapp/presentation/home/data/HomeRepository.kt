package com.ovasta.logisticsapp.presentation.home.data

class HomeRepository(
    private val homeRemoteDataSource: IHomeRemoteDataSource,
) : IHomeRepository {
    override suspend fun getAssignedTasks(
        userId: Int,
        branchId: Int,
        userType: String
    ) = homeRemoteDataSource.getAssignedTasks(userId, branchId, userType)
}