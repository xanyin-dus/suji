package com.suji.accountbook.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object Home : Screen

    @Serializable
    object Record : Screen

    @Serializable
    object Analysis : Screen

    @Serializable
    object Settings : Screen

    @Serializable
    object AddRecord : Screen

    @Serializable
    data class EditRecord(val recordId: Long) : Screen

    @Serializable
    object AccountBookManagement : Screen

    @Serializable
    object CategoryManagement : Screen

    @Serializable
    object PendingRecords : Screen

    @Serializable
    object AIAnalysis : Screen

    @Serializable
    object AISettings : Screen

    @Serializable
    object About : Screen
}

val Screen.routeKey: String
    get() = when (this) {
        is Screen.EditRecord -> "EditRecord/${recordId}"
        else -> this::class.simpleName ?: "unknown"
    }