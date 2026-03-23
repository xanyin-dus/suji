package com.suji.accountbook.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.app.NotificationCompat
import com.suji.accountbook.MainActivity
import com.suji.accountbook.R
import com.suji.accountbook.data.local.entity.PendingRecordEntity
import com.suji.accountbook.data.repository.PendingRecordRepository
import com.suji.accountbook.util.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class AutoRecordService : AccessibilityService() {

    @Inject
    lateinit var pendingRecordRepository: PendingRecordRepository

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    private var lastDetectedTime = 0L
    private var lastDetectedAmount = 0.0
    private var lastDetectedSource = ""

    companion object {
        const val WECHAT_PACKAGE = "com.tencent.mm"
        const val ALIPAY_PACKAGE = "com.eg.android.AlipayGphone"
        const val CHANNEL_ID = "auto_record_channel"

        private val amountPattern = Pattern.compile("[¥￥]?\\s*([\\d,]+\\.?\\d*)\\s*元?")
        
        private val paymentSuccessKeywords = listOf(
            "支付成功", "付款成功", "交易成功", "转账成功", "收款成功",
            "已支付", "已付款", "已转账", "已收款", "完成支付",
            "支付完成", "付款完成", "转账完成", "收款完成",
            "收款到账", "转账到账", "付款金额", "支付金额"
        )
        
        private val excludeKeywords = listOf(
            "余额", "剩余", "可用余额", "账户余额", "零钱余额", 
            "银行卡余额", "信用卡额度", "花呗额度", "借呗额度"
        )
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        createNotificationChannel()
        showForegroundNotification()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        if (!preferencesManager.isAutoRecordEnabled) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        when (packageName) {
            WECHAT_PACKAGE -> handleWeChatEvent(event)
            ALIPAY_PACKAGE -> handleAlipayEvent(event)
        }
    }

    private fun handleWeChatEvent(event: AccessibilityEvent) {
        try {
            val text = extractAllText(event)
            if (text.isEmpty()) return
            
            if (!containsPaymentSuccess(text)) return
            
            val amount = extractAmount(text)
            if (amount != null && amount > 0) {
                savePendingRecord(amount, "微信支付", text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleAlipayEvent(event: AccessibilityEvent) {
        try {
            val text = extractAllText(event)
            if (text.isEmpty()) return
            
            if (!containsPaymentSuccess(text)) return
            
            val amount = extractAmount(text)
            if (amount != null && amount > 0) {
                savePendingRecord(amount, "支付宝", text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun extractAllText(event: AccessibilityEvent): String {
        val sb = StringBuilder()
        
        event.text?.forEach { 
            sb.append(it).append(" ")
        }
        
        event.contentDescription?.let {
            sb.append(it).append(" ")
        }
        
        try {
            val rootNode = rootInActiveWindow
            if (rootNode != null) {
                extractTextFromNode(rootNode, sb)
                rootNode.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return sb.toString()
    }

    private fun extractTextFromNode(node: AccessibilityNodeInfo, sb: StringBuilder) {
        node.text?.let {
            sb.append(it).append(" ")
        }
        
        node.contentDescription?.let {
            sb.append(it).append(" ")
        }
        
        node.hintText?.let {
            sb.append(it).append(" ")
        }
        
        for (i in 0 until node.childCount) {
            try {
                val child = node.getChild(i)
                if (child != null) {
                    extractTextFromNode(child, sb)
                    child.recycle()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun containsPaymentSuccess(text: String): Boolean {
        val lowerText = text.lowercase()
        
        for (keyword in excludeKeywords) {
            if (lowerText.contains(keyword.lowercase())) {
                return false
            }
        }
        
        return paymentSuccessKeywords.any { keyword -> 
            lowerText.contains(keyword.lowercase()) 
        }
    }

    private fun extractAmount(text: String): Double? {
        val matcher = amountPattern.matcher(text)
        
        val amounts = mutableListOf<Double>()
        
        while (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "") ?: continue
            val amount = amountStr.toDoubleOrNull()
            if (amount != null && amount > 0 && amount < 1000000) {
                amounts.add(amount)
            }
        }
        
        if (amounts.isEmpty()) return null
        
        return amounts.maxOrNull()
    }

    private fun savePendingRecord(
        amount: Double,
        source: String,
        detectedText: String
    ) {
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastDetectedTime < 30000 &&
            lastDetectedAmount == amount &&
            lastDetectedSource == source) {
            return
        }
        
        lastDetectedTime = currentTime
        lastDetectedAmount = amount
        lastDetectedSource = source

        serviceScope.launch {
            try {
                val existingRecords = pendingRecordRepository.getUnprocessedPendingRecords().first()

                val recentDuplicate = existingRecords.find {
                    it.amount == amount &&
                    it.source == source &&
                    currentTime - it.detectedTime < 60000
                }

                if (recentDuplicate == null) {
                    pendingRecordRepository.insertPendingRecord(
                        PendingRecordEntity(
                            amount = amount,
                            source = source,
                            detectedText = detectedText.take(500),
                            detectedTime = currentTime
                        )
                    )

                    showDetectionNotification(amount, source)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "自动记账服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "自动记账服务运行状态通知"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showForegroundNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("自动记账服务运行中")
            .setContentText("正在监听微信、支付宝支付信息")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        startForeground(1, notification)
    }

    private fun showDetectionNotification(amount: Double, source: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStr = timeFormat.format(Date())

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("检测到${source}记录")
            .setContentText("¥${String.format("%.2f", amount)} · $timeStr")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onInterrupt() {
    }
}
