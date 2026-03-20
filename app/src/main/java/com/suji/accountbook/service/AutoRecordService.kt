package com.suji.accountbook.service

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
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
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class AutoRecordService : AccessibilityService() {

    @Inject
    lateinit var pendingRecordRepository: PendingRecordRepository

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val WECHAT_PACKAGE = "com.tencent.mm"
        const val ALIPAY_PACKAGE = "com.eg.android.AlipayGphone"
        const val CHANNEL_ID = "auto_record_channel"

        private val amountPatterns = listOf(
            Pattern.compile("¥\\s*([\\d,]+\\.?\\d*)"),
            Pattern.compile("([\\d,]+\\.?\\d*)\\s*元"),
            Pattern.compile("支付[金额]?[：:]?\\s*¥?\\s*([\\d,]+\\.?\\d*)"),
            Pattern.compile("付款[金额]?[：:]?\\s*¥?\\s*([\\d,]+\\.?\\d*)"),
            Pattern.compile("消费[金额]?[：:]?\\s*¥?\\s*([\\d,]+\\.?\\d*)"),
            Pattern.compile("转账[金额]?[：:]?\\s*¥?\\s*([\\d,]+\\.?\\d*)"),
            Pattern.compile("收款[金额]?[：:]?\\s*¥?\\s*([\\d,]+\\.?\\d*)")
        )

        private val excludeKeywords = listOf(
            "余额", "剩余", "可用", "账户余额", "零钱", "银行卡", "信用卡",
            "花呗额度", "借呗额度", "理财", "基金", "股票"
        )

        private val paymentKeywords = listOf(
            "支付成功", "付款成功", "交易成功", "转账成功", "收款成功",
            "已支付", "已付款", "已转账", "已收款", "完成支付",
            "支付完成", "付款完成", "转账完成", "收款完成"
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

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        when (packageName) {
            WECHAT_PACKAGE -> handleWeChatEvent(event)
            ALIPAY_PACKAGE -> handleAlipayEvent(event)
        }
    }

    private fun handleWeChatEvent(event: AccessibilityEvent) {
        val text = extractTextFromEvent(event) ?: return

        if (!containsPaymentKeyword(text)) {
            return
        }

        val amount = extractAmount(text)
        if (amount != null && amount > 0) {
            savePendingRecord(
                amount = amount,
                source = "微信支付",
                detectedText = text
            )
        }
    }

    private fun handleAlipayEvent(event: AccessibilityEvent) {
        val text = extractTextFromEvent(event) ?: return

        if (!containsPaymentKeyword(text)) {
            return
        }

        val amount = extractAmount(text)
        if (amount != null && amount > 0) {
            savePendingRecord(
                amount = amount,
                source = "支付宝",
                detectedText = text
            )
        }
    }

    private fun extractTextFromEvent(event: AccessibilityEvent): String? {
        val textList = event.text ?: return null
        val text = textList.joinToString(" ")

        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val allText = extractAllTextFromNode(rootNode)
            rootNode.recycle()
            return if (allText.isNotEmpty()) allText else text
        }

        return text.ifEmpty { null }
    }

    private fun extractAllTextFromNode(node: AccessibilityNodeInfo): String {
        val textBuilder = StringBuilder()

        node.text?.let {
            textBuilder.append(it).append(" ")
        }

        node.contentDescription?.let {
            textBuilder.append(it).append(" ")
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                textBuilder.append(extractAllTextFromNode(child)).append(" ")
                child.recycle()
            }
        }

        return textBuilder.toString().trim()
    }

    private fun containsPaymentKeyword(text: String): Boolean {
        val lowerText = text.lowercase()
        return paymentKeywords.any { keyword -> lowerText.contains(keyword.lowercase()) }
    }

    private fun extractAmount(text: String): Double? {
        val lowerText = text.lowercase()

        for (keyword in excludeKeywords) {
            if (lowerText.contains(keyword.lowercase())) {
                val keywordIndex = lowerText.indexOf(keyword.lowercase())
                val amountIndex = lowerText.indexOfAny(listOf("¥", "元", "支付", "付款", "消费", "转账", "收款"))
                if (amountIndex != -1 && keywordIndex < amountIndex + 20 && keywordIndex > amountIndex - 20) {
                    return null
                }
            }
        }

        for (pattern in amountPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace(",", "") ?: continue
                val amount = amountStr.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    return amount
                }
            }
        }

        return null
    }

    private fun savePendingRecord(
        amount: Double,
        source: String,
        detectedText: String
    ) {
        serviceScope.launch {
            try {
                val existingRecords = pendingRecordRepository.getUnprocessedPendingRecords().first()

                val recentDuplicate = existingRecords.find {
                    it.amount == amount &&
                    it.source == source &&
                    System.currentTimeMillis() - it.detectedTime < 60000
                }

                if (recentDuplicate == null) {
                    pendingRecordRepository.insertPendingRecord(
                        PendingRecordEntity(
                            amount = amount,
                            source = source,
                            detectedText = detectedText,
                            detectedTime = System.currentTimeMillis()
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
            .build()

        startForeground(1, notification)
    }

    private fun showDetectionNotification(amount: Double, source: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("检测到${source}记录")
            .setContentText("金额：¥${String.format("%.2f", amount)}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onInterrupt() {
    }
}
