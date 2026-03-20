package com.suji.accountbook.ui.analysis

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.suji.accountbook.data.local.dao.CategoryStatistic
import com.suji.accountbook.data.local.dao.DailyStatistic
import com.suji.accountbook.ui.theme.ChartColor1
import com.suji.accountbook.ui.theme.ChartColor2
import com.suji.accountbook.ui.theme.ChartColor3
import com.suji.accountbook.ui.theme.ChartColor4
import com.suji.accountbook.ui.theme.ChartColor5
import com.suji.accountbook.ui.theme.ExpenseColor
import com.suji.accountbook.ui.theme.IncomeColor
import com.suji.accountbook.ui.theme.PrimaryLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val accountBooks by viewModel.accountBooks.collectAsState()
    val expenseCategoryStats by viewModel.expenseCategoryStats.collectAsState()
    val incomeCategoryStats by viewModel.incomeCategoryStats.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val aiAnalysisState by viewModel.aiAnalysisState.collectAsState()

    val chartColors = listOf(
        ChartColor1, ChartColor2, ChartColor3, ChartColor4, ChartColor5
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("数据分析") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TimeRange.entries) { timeRange ->
                    FilterChip(
                        selected = uiState.selectedTimeRange == timeRange,
                        onClick = { viewModel.selectTimeRange(timeRange) },
                        label = {
                            Text(
                                when (timeRange) {
                                    TimeRange.WEEK -> "本周"
                                    TimeRange.MONTH -> "本月"
                                    TimeRange.YEAR -> "本年"
                                    TimeRange.CUSTOM -> "自定义"
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.runAIAnalysis() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryLight
                ),
                enabled = !aiAnalysisState.isLoading
            ) {
                if (aiAnalysisState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI智能分析")
                }
            }

            if (aiAnalysisState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = aiAnalysisState.error!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (aiAnalysisState.result != null) {
                Spacer(modifier = Modifier.height(16.dp))
                AIAnalysisResultCard(result = aiAnalysisState.result!!)
            }

            Spacer(modifier = Modifier.height(16.dp))

            var selectedTabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf("支出分析", "收入分析", "收支趋势")

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryLight
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> ExpenseAnalysisSection(
                    stats = expenseCategoryStats,
                    dailyStats = dailyStats,
                    colors = chartColors
                )
                1 -> IncomeAnalysisSection(
                    stats = incomeCategoryStats,
                    dailyStats = dailyStats,
                    colors = chartColors
                )
                2 -> TrendAnalysisSection(
                    dailyStats = dailyStats
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ExpenseAnalysisSection(
    stats: List<CategoryStatistic>,
    dailyStats: List<DailyStatistic>,
    colors: List<Color>
) {
    Column {
        PieChartCard(
            title = "支出分类占比",
            stats = stats,
            colors = colors
        )

        Spacer(modifier = Modifier.height(16.dp))

        BarChartCard(
            title = "每日支出",
            dailyStats = dailyStats,
            showIncome = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        CategoryListCard(
            title = "支出明细",
            stats = stats,
            colors = colors
        )
    }
}

@Composable
private fun IncomeAnalysisSection(
    stats: List<CategoryStatistic>,
    dailyStats: List<DailyStatistic>,
    colors: List<Color>
) {
    Column {
        PieChartCard(
            title = "收入分类占比",
            stats = stats,
            colors = colors
        )

        Spacer(modifier = Modifier.height(16.dp))

        BarChartCard(
            title = "每日收入",
            dailyStats = dailyStats,
            showIncome = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        CategoryListCard(
            title = "收入明细",
            stats = stats,
            colors = colors
        )
    }
}

@Composable
private fun TrendAnalysisSection(
    dailyStats: List<DailyStatistic>
) {
    Column {
        LineChartCard(
            title = "收支趋势",
            dailyStats = dailyStats
        )

        Spacer(modifier = Modifier.height(16.dp))

        BarChartCard(
            title = "收支对比",
            dailyStats = dailyStats,
            showIncome = true,
            showExpense = true
        )
    }
}

@Composable
private fun AIAnalysisResultCard(result: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = PrimaryLight
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI分析报告",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = result,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun PieChartCard(
    title: String,
    stats: List<CategoryStatistic>,
    colors: List<Color>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (stats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                val total = stats.sumOf { it.totalAmount }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PieChart(
                            data = stats.map { it.totalAmount },
                            colors = colors.take(stats.size),
                            modifier = Modifier.size(160.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "总计",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = String.format("¥%.0f", total),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stats.take(5).forEachIndexed { index, stat ->
                            val percentage = if (total > 0) stat.totalAmount / total * 100 else 0.0
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(colors[index % colors.size])
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stat.categoryName ?: "未分类",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = String.format("%.1f%%", percentage),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PieChart(
    data: List<Double>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = data.sum()
    if (total == 0.0) return

    var startAngle = -90f

    Box(
        modifier = modifier
    ) {
        data.forEachIndexed { index, value ->
            if (value > 0) {
                val sweepAngle = (value / total * 360f).toFloat()
                val color = colors[index % colors.size]

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true
                        )
                )
                startAngle += sweepAngle
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
private fun BarChartCard(
    title: String,
    dailyStats: List<DailyStatistic>,
    showIncome: Boolean = false,
    showExpense: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (dailyStats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                val displayStats = dailyStats.takeLast(7)
                val maxAmount = displayStats.maxOf {
                    when {
                        showIncome && showExpense -> maxOf(it.income, it.expense)
                        showIncome -> it.income
                        else -> it.expense
                    }
                }.takeIf { it > 0 } ?: 1.0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    displayStats.forEach { stat ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (showExpense) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(((stat.expense / maxAmount) * 120).dp.coerceAtLeast(4.dp))
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(ExpenseColor)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            if (showIncome) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(((stat.income / maxAmount) * 120).dp.coerceAtLeast(4.dp))
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(IncomeColor)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Text(
                                text = stat.day.takeLast(5),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (showExpense) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(ExpenseColor, RoundedCornerShape(2.dp))
                            )
                            Text(
                                text = "支出",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    if (showIncome) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(IncomeColor, RoundedCornerShape(2.dp))
                            )
                            Text(
                                text = "收入",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LineChartCard(
    title: String,
    dailyStats: List<DailyStatistic>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (dailyStats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                val displayStats = dailyStats.takeLast(7)
                val maxAmount = displayStats.maxOf {
                    maxOf(it.income, it.expense)
                }.takeIf { it > 0 } ?: 1.0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val width = size.width
                        val height = size.height
                        val padding = 40f
                        val chartWidth = width - padding * 2
                        val chartHeight = height - padding * 2

                        drawLine(
                            color = Color.LightGray,
                            start = Offset(padding, height - padding),
                            end = Offset(width - padding, height - padding),
                            strokeWidth = 1f
                        )

                        val expensePoints = displayStats.mapIndexed { index, stat ->
                            Offset(
                                padding + (chartWidth / (displayStats.size - 1)) * index,
                                height - padding - (stat.expense / maxAmount * chartHeight).toFloat()
                            )
                        }

                        val incomePoints = displayStats.mapIndexed { index, stat ->
                            Offset(
                                padding + (chartWidth / (displayStats.size - 1)) * index,
                                height - padding - (stat.income / maxAmount * chartHeight).toFloat()
                            )
                        }

                        for (i in 0 until expensePoints.size - 1) {
                            drawLine(
                                color = ExpenseColor,
                                start = expensePoints[i],
                                end = expensePoints[i + 1],
                                strokeWidth = 3f
                            )
                        }

                        for (i in 0 until incomePoints.size - 1) {
                            drawLine(
                                color = IncomeColor,
                                start = incomePoints[i],
                                end = incomePoints[i + 1],
                                strokeWidth = 3f
                            )
                        }

                        expensePoints.forEach { point ->
                            drawCircle(
                                color = ExpenseColor,
                                radius = 6f,
                                center = point
                            )
                        }

                        incomePoints.forEach { point ->
                            drawCircle(
                                color = IncomeColor,
                                radius = 6f,
                                center = point
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(ExpenseColor, RoundedCornerShape(2.dp))
                        )
                        Text(
                            text = "支出",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(IncomeColor, RoundedCornerShape(2.dp))
                        )
                        Text(
                            text = "收入",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryListCard(
    title: String,
    stats: List<CategoryStatistic>,
    colors: List<Color>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (stats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                val total = stats.sumOf { it.totalAmount }

                stats.forEachIndexed { index, stat ->
                    val percentage = if (total > 0) stat.totalAmount / total else 0.0
                    val animatedProgress by animateFloatAsState(
                        targetValue = percentage.toFloat(),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "progress_$index"
                    )

                    CategoryProgressBar(
                        name = stat.categoryName ?: "未分类",
                        amount = stat.totalAmount,
                        percentage = percentage,
                        color = colors[index % colors.size],
                        progress = animatedProgress
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CategoryProgressBar(
    name: String,
    amount: Double,
    percentage: Double,
    color: Color,
    progress: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = String.format("¥%.2f (%.1f%%)", amount, percentage * 100),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            )
        }
    }
}

private fun Modifier.drawArc(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    useCenter: Boolean
): Modifier = this.then(
    Modifier.drawBehind {
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = useCenter
        )
    }
)
