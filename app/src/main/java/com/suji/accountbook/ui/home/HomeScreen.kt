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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.suji.accountbook.data.local.entity.RecordType
import com.suji.accountbook.ui.theme.ExpenseColor
import com.suji.accountbook.ui.theme.IncomeColor
import com.suji.accountbook.ui.theme.PrimaryLight
import java.text.SimpleDateFormat
import java.util.Calendar
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
    val recordsWithCategory by viewModel.recordsWithCategory.collectAsState()
    val accountBooks by viewModel.accountBooks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        HomeHeader(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            currentYear = uiState.currentYear,
            currentMonth = uiState.currentMonth,
            onPreviousMonth = { viewModel.previousMonth() },
            onNextMonth = { viewModel.nextMonth() },
            onNavigateToAnalysis = onNavigateToAnalysis,
            onNavigateToSettings = onNavigateToSettings
        )

        Spacer(modifier = Modifier.height(16.dp))

        RecordList(
            recordsWithCategory = recordsWithCategory,
            onRecordClick = { },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HomeHeader(
    totalIncome: Double,
    totalExpense: Double,
    currentYear: Int,
    currentMonth: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val balance = totalIncome - totalExpense
    val monthNames = listOf("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月")
    val calendar = Calendar.getInstance()
    val isCurrentMonth = currentYear == calendar.get(Calendar.YEAR) && currentMonth == calendar.get(Calendar.MONTH)

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPreviousMonth,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "上个月",
                            tint = Color.White
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "${currentYear}年",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = monthNames[currentMonth],
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onNextMonth,
                        modifier = Modifier.size(36.dp),
                        enabled = !isCurrentMonth
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "下个月",
                            tint = if (isCurrentMonth) Color.White.copy(alpha = 0.3f) else Color.White
                        )
                    }
                }

                if (!isCurrentMonth) {
                    Text(
                        text = "本月",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (balance >= 0) "结余" else "超支",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.8f)
            )

            Text(
                text = String.format("¥ %.2f", kotlin.math.abs(balance)),
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
    recordsWithCategory: List<RecordWithCategory>,
    onRecordClick: (RecordWithCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayOfWeekNames = listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    val groupedRecords = recordsWithCategory.groupBy { item ->
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(item.record.date))
    }.toSortedMap(reverseOrder())

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "记账记录",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (recordsWithCategory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "暂无记录",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "点击下方按钮开始记账",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                groupedRecords.forEach { (date, dateRecords) ->
                    item {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val parsedDate = dateFormat.parse(date)
                        val calendar = Calendar.getInstance()
                        if (parsedDate != null) {
                            calendar.time = parsedDate
                        }
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                        val month = calendar.get(Calendar.MONTH) + 1
                        
                        val today = Calendar.getInstance()
                        val isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isToday) PrimaryLight.copy(alpha = 0.1f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$dayOfMonth",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isToday) PrimaryLight else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "${month}月${dayOfMonth}日 ${dayOfWeekNames[dayOfWeek - 1]}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                if (isToday) {
                                    Text(
                                        text = "今天",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrimaryLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            val dayIncome = dateRecords.filter { it.record.type == RecordType.INCOME }.sumOf { it.record.amount }
                            val dayExpense = dateRecords.filter { it.record.type == RecordType.EXPENSE }.sumOf { it.record.amount }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                if (dayIncome > 0) {
                                    Text(
                                        text = String.format("+¥%.0f", dayIncome),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IncomeColor
                                    )
                                }
                                if (dayExpense > 0) {
                                    Text(
                                        text = String.format("-¥%.0f", dayExpense),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ExpenseColor
                                    )
                                }
                            }
                        }
                    }

                    items(dateRecords) { item ->
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
                                recordWithCategory = item,
                                onClick = { onRecordClick(item) }
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
    recordWithCategory: RecordWithCategory,
    onClick: () -> Unit
) {
    val record = recordWithCategory.record
    val category = recordWithCategory.category
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val isExpense = record.type == RecordType.EXPENSE

    val iconText = category?.icon ?: if (isExpense) "支" else "收"
    val categoryName = category?.name ?: if (isExpense) "支出" else "收入"
    val categoryColor = try {
        category?.color?.let { Color(android.graphics.Color.parseColor(it)) }
            ?: if (isExpense) ExpenseColor else IncomeColor
    } catch (e: Exception) {
        if (isExpense) ExpenseColor else IncomeColor
    }

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
                    .background(categoryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    style = MaterialTheme.typography.titleMedium,
                    color = categoryColor
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = record.remark.ifEmpty { categoryName },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.bodySmall,
                        color = categoryColor.copy(alpha = 0.7f)
                    )
                    Text(
                        text = " · ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Text(
                        text = timeFormat.format(Date(record.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
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
