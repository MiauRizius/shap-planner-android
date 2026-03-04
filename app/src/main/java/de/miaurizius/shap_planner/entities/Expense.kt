package de.miaurizius.shap_planner.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Entity(tableName = "expenses")
data class Expense (
    @PrimaryKey val id: UUID,
    val payer_id: UUID,
    val amount: Int,
    val title: String,
    val description: String,
    val attachments: List<String>?,
    val created_at: Int,
    val last_updated_at: Int
)

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}