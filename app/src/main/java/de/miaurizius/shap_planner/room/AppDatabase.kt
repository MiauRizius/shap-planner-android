package de.miaurizius.shap_planner.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.entities.AccountDao
import de.miaurizius.shap_planner.entities.Expense
import de.miaurizius.shap_planner.entities.ExpenseDao
import de.miaurizius.shap_planner.entities.ExpenseShare
import de.miaurizius.shap_planner.entities.ExpenseShareDao
import de.miaurizius.shap_planner.entities.User
import de.miaurizius.shap_planner.entities.UserDao

class Converters {
    @TypeConverter
    fun fromList(list: List<String>?): String? = list?.joinToString(",")
    @TypeConverter
    fun toList(data: String?): List<String>? = data?.split(",")?.map { it.trim() }
}

@Database(entities = [Account::class, Expense::class, ExpenseShare::class, User::class], version = 6)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseShareDao(): ExpenseShareDao
    abstract fun userDao(): UserDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shap_planner_database"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}