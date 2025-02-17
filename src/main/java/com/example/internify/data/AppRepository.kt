package com.example.internify.data

import com.example.internify.model.Internship
import kotlinx.coroutines.flow.Flow


interface AppRepository {
    suspend fun insertInternship(internship: Internship)

    suspend fun updateInternship(internship: Internship)

    suspend fun deleteInternship(id:Int)

    suspend fun getAllInternships(): Flow<List<Internship>>

    suspend fun getInternshipsByLocation(location: String) : Flow<List<Internship>>

    suspend fun getInternshipsByStatus(status: String): Flow<List<Internship>>
}

class InternshipRepositoryImpl(
    private val internshipDao: InternifyDao
): AppRepository {
    override suspend fun insertInternship(internship: Internship) = internshipDao.insertInternship(internship)

    override suspend fun updateInternship(internship: Internship) = internshipDao.updateInternship(internship)

    override suspend fun deleteInternship(id:Int) = internshipDao.deleteInternship(id)

    override suspend fun getAllInternships() = internshipDao.getAllInternships()

    override suspend fun getInternshipsByLocation(location: String) = internshipDao.getInternshipsByLocation(location)

    override suspend fun getInternshipsByStatus(status: String) = internshipDao.getInternshipsByStatus(status)
}