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

@Entity(tableName = "expense_shares")
data class ExpenseShare(
    @PrimaryKey val id: UUID,
    val expense_id: UUID,
    val user_id: UUID,
    val share_cents: Int
)

@Dao
interface ExpenseShareDao {
    @Query("SELECT * FROM expense_shares")
    fun getAllShares(): Flow<List<ExpenseShare>>

    @Query("SELECT * FROM expense_shares WHERE id = :shareId")
    fun getShareById(shareId: UUID): Flow<ExpenseShare?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShare(share: ExpenseShare)

    @Delete
    suspend fun deleteShare(share: ExpenseShare)
}