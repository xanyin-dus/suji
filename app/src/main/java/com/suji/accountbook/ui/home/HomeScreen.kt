package com.suji.accountbook.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.local.entity.RecordType
import com.suji.accountbook.ui.theme.ExpenseColor
import com.suji.accountbook.ui.theme.IncomeColor
import com.suji.accountbook.ui.theme.PrimaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToRecord: () -> Unit,
    onNavigateToAddRecord: () -> Unit,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val records by viewModel.records.collectAsState()
    val accountBooks by viewModel.accountBooks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            HomeHeader(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                onNavigateToAnalysis = onNavigateToAnalysis,
                onNavigateToSettings = onNavigateToSettings
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecordList(
                records = records,
                onRecordClick = { },
                modifier = Modifier.weight(1f)
            )
        }

        FloatingActionButton(
            onClick = onNavigateToAddRecord,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PrimaryLight
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加记录",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun HomeHeader(
    totalIncome: Double,
    totalExpense: Double,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val balance = totalIncome - totalExpense

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryLight,
                        PrimaryLight.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "本月概览",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = String.format("¥ %.2f", balance),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "收入",
                    amount = totalIncome,
                    icon = Icons.Default.KeyboardArrowUp,
                    color = IncomeColor
                )

                StatItem(
                    title = "支出",
                    amount = totalExpense,
                    icon = Icons.Default.KeyboardArrowDown,
                    color = ExpenseColor
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = String.format("¥ %.2f", amount),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecordList(
    records: List<RecordEntity>,
    onRecordClick: (RecordEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val groupedRecords = records.groupBy { record ->
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(record.date))
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "本月记录",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无记录",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                groupedRecords.forEach { (date, dateRecords) ->
                    item {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(dateRecords) { record ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ),
                            exit = fadeOut() + scaleOut()
                        ) {
                            RecordItem(
                                record = record,
                                onClick = { onRecordClick(record) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordItem(
    record: RecordEntity,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val isExpense = record.type == RecordType.EXPENSE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isExpense) ExpenseColor.copy(alpha = 0.1f)
                        else IncomeColor.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = record.remark.take(1).ifEmpty { if (isExpense) "支" else "收" },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isExpense) ExpenseColor else IncomeColor
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = record.remark.ifEmpty { if (isExpense) "支出" else "收入" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = timeFormat.format(Date(record.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Text(
                text = String.format(
                    "%s¥ %.2f",
                    if (isExpense) "-" else "+",
                    record.amount
                ),
                style = MaterialTheme.typography.titleMedium,
                color = if (isExpense) ExpenseColor else IncomeColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
