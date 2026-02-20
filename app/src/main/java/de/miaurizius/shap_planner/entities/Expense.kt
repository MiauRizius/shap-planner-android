package de.miaurizius.shap_planner.entities

import java.util.UUID

data class Expense (
    val id: UUID,
    val amt: Double,
    val desc: String,

    val payerId: UUID,
    val debtors: List<User>
)