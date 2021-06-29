package com.example.iqrabedlibrary.database

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.viewModel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExportToExcel(private val context: Context, private val userViewModel: UserViewModel,
                    private val teachersList: List<User>, private val studentsList: List<User>) {

    private val TAG = "ExportToExcel"

    private lateinit var wb: XSSFWorkbook

    private val CHANNEL_ID = "MY CHANNEL ID"

    private val PROGRESS_MAX = teachersList.size + studentsList.size - 1

    private var PROGRESS_CURRENT = 0

    private val notificationId = 9

    private lateinit var notificationManager: NotificationManagerCompat

    private lateinit var builder: NotificationCompat.Builder

    private val scope = CoroutineScope(Dispatchers.Default)

      fun export() {
          scope.launch {
              //start Notification Progress
              createNotification()

              wb = XSSFWorkbook()
              val teachersSheet = wb.createSheet("Teachers")
              val studentsSheet = wb.createSheet("Students")
              var row = teachersSheet.createRow(0)


              //Generating Teachers Data
              createMetaData(wb, row)

              var rowNum = 1

              for (i in teachersList.indices) {

                  val histories = userViewModel.getAllHistoryInList(teachersList[i].id)
                  withContext(Dispatchers.Default) {

                      histories.forEach { history ->

                          row = teachersSheet.createRow(rowNum)

                          row.createCell(0).setCellValue(rowNum.toString())

                          row.createCell(1).setCellValue(teachersList[i].name)

                          row.createCell(2).setCellValue(history.issueDate)

                          row.createCell(3).setCellValue(history.bookTitle)

                          row.createCell(4).setCellValue(history.bookNo.toString())

                          row.createCell(5)
                              .setCellValue(history.returnDate ?: Constants.NOT_RETURNED)
                          rowNum++
                      }
                  }
                  PROGRESS_CURRENT++
                  updateNotification(PROGRESS_MAX, PROGRESS_CURRENT)
              }

                  //Generating Students Data
                  row = studentsSheet.createRow(0)

                  createMetaData(wb, row)

                  rowNum = 1

                  for (j in studentsList.indices) {

                      val histories1 = userViewModel.getAllHistoryInList(studentsList[j].id)
                      withContext(Dispatchers.Default) {

                          histories1.forEach { history ->

                              row = studentsSheet.createRow(rowNum)

                              row.createCell(0).setCellValue(rowNum.toString())

                              row.createCell(1).setCellValue(studentsList[j].name)

                              row.createCell(2).setCellValue(history.issueDate)

                              row.createCell(3).setCellValue(history.bookTitle)

                              row.createCell(4).setCellValue(history.bookNo.toString())

                              row.createCell(5)
                                  .setCellValue(history.returnDate ?: Constants.NOT_RETURNED)

                              rowNum++
                          }
                      }
                      PROGRESS_CURRENT++
                      updateNotification(PROGRESS_MAX, PROGRESS_CURRENT)
                  }
                      wb.export()
                      userViewModel.isExporting = false
                      updateNotification(0, 0)
                      Log.d(TAG, "---------End----------")
              }
          }

    private fun createMetaData(wb: XSSFWorkbook, row: XSSFRow) {
        val style = wb.createCellStyle()
        style.apply {
            borderBottom = CellStyle.BORDER_THIN
            borderLeft = CellStyle.BORDER_THIN
            borderRight = CellStyle.BORDER_THIN
            borderTop = CellStyle.BORDER_THIN
            wrapText = true
        }


        row.createCell(0).apply {
            cellStyle = style
            setCellValue("Sr.No")
        }

        row.createCell(1).apply {
            cellStyle = style
            setCellValue("Name")
        }

        row.createCell(2).apply {
            cellStyle = style
            setCellValue("Issue Date")
        }

        row.createCell(3).apply {
            cellStyle = style
            setCellValue("Book Name")
        }

        row.createCell(4).apply {
            cellStyle = style
            setCellValue("Book Number")
        }

        row.createCell(5).apply {
            cellStyle = style
            setCellValue("Return Date")
        }
    }



    fun cancel() {
        scope.cancel()
    }


    fun updateNotification(max: Int, current: Int) {
       if (max == 0 && current == 0) {
           builder.setContentTitle("Exporting Completed")
           builder.setContentText("")
           builder.setOngoing(false)
       }

        builder.setProgress(max, current, false)
        notificationManager.notify(notificationId, builder.build())
    }


    /**
     * Exports the Excel file to Internal Storage of
     * Targeted Platform
     */
    private fun XSSFWorkbook.export() {

        val file = File("${Environment.getExternalStorageDirectory()}/Iqra library history.xlsx")

        try {
            if (!file.exists()) {
                Log.d(TAG, "Creating File")
                file.createNewFile()
            }

            Log.d(TAG, "getting OutPut Stream")
            val outputStream = file.outputStream()

            Log.d(TAG, "Writing excel file to outPutStream")
            this.write(outputStream)

            Log.d(TAG, "Flushing OutPut Stream")
            outputStream.flush()
            Log.d(TAG, "Closing OutPut Stream")
            outputStream.close()

        } catch (e: Exception) {
            showErrorDialog(e.message)
        }
    }

    private fun showErrorDialog(error: String?) {
        MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme).apply {

            setTitle("Unable to Export")

            val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.dialog_bg, null)
            background = drawable

            setMessage("While Exporting in Excel getting this Error: $error Please Contact Zaki")

            setNegativeButton("OK", null)

            show()
        }
    }

    private fun createNotification() {
        builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {

            setContentTitle("Exporting to Excel")

            setContentText("In Progress")

            setSmallIcon(R.drawable.ic_library)

            setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)

            setOngoing(true)

            setNotificationSilent()

            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        lateinit var channel: NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = context.resources.getString(R.string.channel_name)

            val descriptionText = context.resources.getString(R.string.channel_description)

            val importance = NotificationManager.IMPORTANCE_DEFAULT

            //Create Notification Channel
            channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
        }


        notificationManager = NotificationManagerCompat.from(context)

        notificationManager.apply {
            // Issue the initial notification with zero progress
            createNotificationChannel(channel)
            notify(notificationId, builder.build())
            // Do the job here that tracks the progress.
            // Usually, this should be in a
            // worker thread
            // To show progress, update PROGRESS_CURRENT and update the notification with:

            // builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
            // notificationManager.notify(notificationId, builder.build())

            // When done, update the notification one more time to remove the progress bar

            // builder.setContentText("Download complete")
            // .setProgress(0, 0, false)
            // notify(notificationId, builder.build())
        }
    }

}