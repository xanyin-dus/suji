package com.suji.accountbook.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.suji.accountbook.data.local.entity.PendingRecordEntity
import com.suji.accountbook.data.repository.PendingRecordRepository
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

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val WECHAT_PACKAGE = "com.tencent.mm"
        const val ALIPAY_PACKAGE = "com.eg.android.AlipayGphone"

        private val wechatPayPattern = Pattern.compile("支付[金额]?[：:]?\\s*¥?([\\d,]+\\.?\\d*)")
        private val alipayPayPattern = Pattern.compile("付款[金额]?[：:]?\\s*¥?([\\d,]+\\.?\\d*)")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

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
        val text = event.text?.joinToString(" ") ?: return

        val matcher = wechatPayPattern.matcher(text)
        if (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "") ?: return
            val amount = amountStr.toDoubleOrNull() ?: return

            savePendingRecord(
                amount = amount,
                source = "微信支付",
                detectedText = text
            )
        }
    }

    private fun handleAlipayEvent(event: AccessibilityEvent) {
        val text = event.text?.joinToString(" ") ?: return

        val matcher = alipayPayPattern.matcher(text)
        if (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "") ?: return
            val amount = amountStr.toDoubleOrNull() ?: return

            savePendingRecord(
                amount = amount,
                source = "支付宝",
                detectedText = text
            )
        }
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onInterrupt() {
    }
}
