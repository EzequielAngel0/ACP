package com.example.viajesapp

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

object ExcelExporter {

    suspend fun exportarTickets(context: Context, tickets: List<TicketEntity>, idViaje: String) {
        withContext(Dispatchers.IO) {
            try {
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Tickets")

                // Estilo de encabezado
                val headerStyle: CellStyle = workbook.createCellStyle().apply {
                    alignment = HorizontalAlignment.CENTER
                }

                val header = sheet.createRow(0)
                val columnas = listOf("ID", "Origen", "Destino", "Precio", "Recibido", "Cambio", "Fecha", "Hora")

                columnas.forEachIndexed { i, titulo ->
                    val cell = header.createCell(i)
                    cell.setCellValue(titulo)
                    cell.setCellStyle(headerStyle)
                }

                var totalPrecio = 0.0
                var totalRecibido = 0.0
                var totalCambio = 0.0

                tickets.forEachIndexed { index, ticket ->
                    val row = sheet.createRow(index + 1)
                    row.createCell(0).setCellValue(ticket.id.toDouble())
                    row.createCell(1).setCellValue(ticket.origen)
                    row.createCell(2).setCellValue(ticket.destino)
                    row.createCell(3).setCellValue(ticket.precio)
                    row.createCell(4).setCellValue(ticket.recibido ?: 0.0)
                    row.createCell(5).setCellValue(ticket.cambio ?: 0.0)
                    row.createCell(6).setCellValue(ticket.fecha)
                    row.createCell(7).setCellValue(ticket.hora)

                    totalPrecio += ticket.precio
                    totalRecibido += ticket.recibido ?: 0.0
                    totalCambio += ticket.cambio ?: 0.0
                }

                // Fila vacía
                sheet.createRow(tickets.size + 1)

                // Fila "Resumen"
                val resumenTitleRow = sheet.createRow(tickets.size + 2)
                resumenTitleRow.createCell(0).setCellValue("Resumen")

                // Fila de resumen de totales y pasajeros
                val resumenDataRow = sheet.createRow(tickets.size + 3)
                resumenDataRow.createCell(0).setCellValue("Total pasajeros:")
                resumenDataRow.createCell(1).setCellValue(tickets.size.toDouble())
                resumenDataRow.createCell(2).setCellValue("Total precio:")
                resumenDataRow.createCell(3).setCellValue(totalPrecio)
                resumenDataRow.createCell(4).setCellValue("Total recibido:")
                resumenDataRow.createCell(5).setCellValue(totalRecibido)
                resumenDataRow.createCell(6).setCellValue("Total cambio:")
                resumenDataRow.createCell(7).setCellValue(totalCambio)

                // Guardar archivo en carpeta de Descargas
                val fileName = "viaje_${idViaje}.xlsx"
                val mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    val outputStream: OutputStream? = resolver.openOutputStream(it)
                    workbook.write(outputStream)
                    outputStream?.close()
                    workbook.close()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Excel guardado en Descargas", Toast.LENGTH_LONG).show()
                    }
                } ?: throw Exception("No se pudo crear el archivo en Descargas")
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
