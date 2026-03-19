package com.suji.accountbook.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.local.entity.RecordType
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PDFExporter @Inject constructor() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    suspend fun exportRecordsToPDF(
        context: Context,
        records: List<RecordEntity>,
        title: String = "账单记录"
    ): Result<File> = try {
        val fileName = "suji_records_${fileDateFormat.format(Date())}.pdf"
        val file = File(context.cacheDir, fileName)

        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)

        document.add(
            Paragraph(title)
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
        )

        val exportDate = "导出时间：${dateFormat.format(Date())}"
        document.add(
            Paragraph(exportDate)
                .setFontSize(10f)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20f)
        )

        val totalIncome = records.filter { it.type == RecordType.INCOME }.sumOf { it.amount }
        val totalExpense = records.filter { it.type == RecordType.EXPENSE }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        document.add(
            Paragraph("收入：¥${String.format("%.2f", totalIncome)}")
                .setFontSize(12f)
                .setFontColor(DeviceRgb(76, 175, 80))
        )
        document.add(
            Paragraph("支出：¥${String.format("%.2f", totalExpense)}")
                .setFontSize(12f)
                .setFontColor(DeviceRgb(233, 30, 99))
        )
        document.add(
            Paragraph("结余：¥${String.format("%.2f", balance)}")
                .setFontSize(12f)
                .setBold()
                .setMarginBottom(20f)
        )

        val table = Table(UnitValue.createPercentArray(floatArrayOf(20f, 15f, 20f, 25f, 20f)))
            .useAllAvailableWidth()

        table.addHeaderCell(
            Cell().add(Paragraph("日期").setBold())
        )
        table.addHeaderCell(
            Cell().add(Paragraph("类型").setBold())
        )
        table.addHeaderCell(
            Cell().add(Paragraph("金额").setBold())
        )
        table.addHeaderCell(
            Cell().add(Paragraph("备注").setBold())
        )
        table.addHeaderCell(
            Cell().add(Paragraph("分类ID").setBold())
        )

        records.forEach { record ->
            table.addCell(
                Cell().add(Paragraph(dateFormat.format(Date(record.date))))
            )
            table.addCell(
                Cell().add(
                    Paragraph(if (record.type == RecordType.INCOME) "收入" else "支出")
                )
            )
            table.addCell(
                Cell().add(
                    Paragraph(
                        String.format("¥%.2f", record.amount)
                    ).setFontColor(
                        if (record.type == RecordType.INCOME)
                            DeviceRgb(76, 175, 80)
                        else
                            DeviceRgb(233, 30, 99)
                    )
                )
            )
            table.addCell(
                Cell().add(Paragraph(record.remark.ifEmpty { "-" }))
            )
            table.addCell(
                Cell().add(Paragraph(record.categoryId?.toString() ?: "-"))
            )
        }

        document.add(table)
        document.close()

        Result.success(file)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun exportAnalysisToPDF(
        context: Context,
        title: String,
        analysisContent: String,
        chartBitmap: Bitmap? = null
    ): Result<File> = try {
        val fileName = "suji_analysis_${fileDateFormat.format(Date())}.pdf"
        val file = File(context.cacheDir, fileName)

        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)

        document.add(
            Paragraph(title)
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
        )

        val exportDate = "导出时间：${dateFormat.format(Date())}"
        document.add(
            Paragraph(exportDate)
                .setFontSize(10f)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20f)
        )

        analysisContent.split("\n").forEach { line ->
            document.add(
                Paragraph(line)
                    .setFontSize(11f)
                    .setMarginBottom(5f)
            )
        }

        document.close()

        Result.success(file)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun sharePDF(context: Context, file: File) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            android.content.Intent.createChooser(shareIntent, "分享PDF文件")
        )
    }
}
