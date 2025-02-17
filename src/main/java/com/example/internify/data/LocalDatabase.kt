package com.example.internify.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.internify.model.Internship
import kotlinx.coroutines.flow.Flow

@Dao
interface InternifyDao {


    @Insert
    suspend fun insertInternship(internship: Internship)

    @Update
    suspend fun updateInternship(internship: Internship)

    @Query("DELETE FROM internships WHERE id = :id")
    suspend fun deleteInternship(id:Int)

    @Query("SELECT * FROM internships ORDER BY companyName ASC")
    fun getAllInternships(): Flow<List<Internship>>

    @Query("SELECT * FROM internships WHERE location = :location ORDER BY companyName ASC")
   fun getInternshipsByLocation(location: String): Flow<List<Internship>>

    @Query("SELECT * FROM internships WHERE status = :status ORDER BY companyName ASC")
    fun getInternshipsByStatus(status: String): Flow<List<Internship>>


}

@Database(entities = [Internship::class], version = 1)
abstract class InternshipDatabase : RoomDatabase() {
    abstract fun internifyDao(): InternifyDao

    companion object {
        @Volatile
        private var INSTANCE: InternshipDatabase? = null

        fun getDatabase(context: Context): InternshipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InternshipDatabase::class.java,
                    "internship_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}