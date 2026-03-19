package com.suji.accountbook.ui.record

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddRecord: () -> Unit,
    onNavigateToEditRecord: (Long) -> Unit
) {
    val records by viewModel.records.collectAsState()
    val accountBooks by viewModel.accountBooks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val filteredRecords = if (uiState.searchQuery.isEmpty()) {
        records
    } else {
        records.filter {
            it.remark.contains(uiState.searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("账单") },
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
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("搜索备注") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accountBooks) { book ->
                    FilterChip(
                        selected = book.id == uiState.selectedAccountBookId,
                        onClick = { viewModel.selectAccountBook(book.id) },
                        label = { Text(book.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredRecords,
                        key = { it.id }
                    ) { record ->
                        RecordItemWithSwipe(
                            record = record,
                            onClick = { onNavigateToEditRecord(record.id) },
                            onDelete = { viewModel.deleteRecord(record) }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onNavigateToAddRecord,
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
}

@Composable
private fun RecordItemWithSwipe(
    record: RecordEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var isDeleted by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !isDeleted,
        enter = fadeIn() + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut() + scaleOut()
    ) {
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
                val isExpense = record.type == RecordType.EXPENSE
                val timeFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

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

                IconButton(
                    onClick = {
                        isDeleted = true
                        onDelete()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
