package com.example.readallsms

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.readallsms.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val requestReadSms: Int = 2
    private val eZReload: String = "eZ Reload"
    private val eZReloadTransfers: String = "eZReload"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS),
                requestReadSms
            )
        } else {
            setSmsMessages("", null)
        }

        setSmsMessages("", null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestReadSms) setSmsMessages("", null)
    }

    private fun setSmsMessages(uriString: String, selection: String?) {

        val smsList = ArrayList<SmsData>()

        val cursor = contentResolver.query(
            Uri.parse("content://sms/$uriString"),
            null,
            selection,
            null,
            null,
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val nameID = cursor.getColumnIndex("address")
                val messageID = cursor.getColumnIndex("body")
                val dateID = cursor.getColumnIndex("date")

                do {
                    val dateString = cursor.getString(dateID)
//                    val messageString = cursor.getString(messageID)

                    if ((cursor.getString(nameID).equals(eZReload) || (cursor.getString(nameID)
                            .equals(eZReloadTransfers))) && (cursor.getString(messageID)
                            .startsWith("RELOADED")) || (cursor.getString(messageID)
                            .startsWith("RELOAD")) || (cursor.getString(messageID)
                            .startsWith("YOU HAVE")) || (cursor.getString(messageID)).contains("has transferred")
                    ) {
                        println(cursor.getString(messageID))
                        smsList.add(
                            SmsData(
                                cursor.getString(nameID),
                                convertDateTimeToSinhala(Date(dateString.toLong()).toString()),
                                formatReloadSms(cursor.getString(messageID))
                            )
                        )
                    }

                } while (cursor.moveToNext())
            }
            cursor.close()
            val adapter = ListAdapter(this, smsList)
            sms_list_view.adapter = adapter

        }

    }


    private fun convertDateTimeToSinhala(dateTimeString: String): String {
        // Parse the input date-time string
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US)
        val dateTime: Date
        try {
            dateTime = inputFormat.parse(dateTimeString)!!
        } catch (e: ParseException) {
            return "Invalid date-time format"
        }

        // Convert the parsed date-time to Sinhala
        val sinhalaSymbols = DateFormatSymbols.getInstance(Locale("si", "LK"))
        val outputFormat = SimpleDateFormat("dd MMMM yyyy hh:mm a", sinhalaSymbols)
        return outputFormat.format(dateTime)
    }


    private fun formatReloadSms(messageID: String): Array<Any> {

        if (messageID.startsWith("RELOADED")) {

            // Extract phone number
            val phoneRegex = "TO (\\d+)"
            val phoneNumber =
                Pattern.compile(phoneRegex).matcher(messageID).apply { find() }.group(1)

            // Extract date
            val dateRegex = "ON (\\w+ \\d+, \\d+)"
            val date = Pattern.compile(dateRegex).matcher(messageID).apply { find() }.group(1)

            // Extract reference number
            val refRegex = "REFERENCE NO: (\\d+)"
            val referenceNumber =
                Pattern.compile(refRegex).matcher(messageID).apply { find() }.group(1)

            // Extract amount
            val amountRegex = "RS (\\d+\\.\\d+)"
            val amount = Pattern.compile(amountRegex).matcher(messageID).apply { find() }.group(1)

            // Extract new balance
            val balanceRegex = "NEW BALANCE: RS (\\d+\\.\\d+)"
            val newBalance =
                Pattern.compile(balanceRegex).matcher(messageID).apply { find() }.group(1)

            return arrayOf(
                "$phoneNumber අංකයට\nරු. $amount/= ක්\n$date දිනයේදී රීලෝඩ් කරන ලදි.\nඉතිරි මුදල රු. $newBalance/=\nයොමු අංකය: $referenceNumber, ",
                1
            )

        } else if (messageID.startsWith("RELOAD NOT SUCCESSFUL TO")) {

            // Extract phone number
            val phoneRegex = "TO (\\d+)"
            val phoneNumber =
                Pattern.compile(phoneRegex).matcher(messageID).apply { find() }.group(1)

            return arrayOf("මුදල් මදි නිසා $phoneNumber යන අංකයට දැමූ රීලෝඩ් එක සාර්ථක නැත.", 3)

        } else if (messageID.startsWith("YOU HAVE")) {
            return arrayOf("ඔබ යෙදූ PIN අංකය වැරදියි නැවත උත්සාහ කරන්න", 3)

        } else if (messageID.contains("has transferred")) {

            val firstPhoneNumberRegex = "(\\d{9})"
            val firstPhoneNumber =
                Pattern.compile(firstPhoneNumberRegex).matcher(messageID).apply { find() }.group(1)

            val transferDateRegex = "(\\d{2}/\\d{2}/\\d{4})"
            val transferDate =
                Pattern.compile(transferDateRegex).matcher(messageID).apply { find() }.group(1)

            val transferredAmountRegex = "(\\d+\\.\\d{1,2})"
            val transferredAmount =
                Pattern.compile(transferredAmountRegex).matcher(messageID).apply { find() }.group(1)

            val newBalanceRegex = "Your new balance is Rs (\\d+\\.\\d+)"
            val newBalance =
                Pattern.compile(newBalanceRegex).matcher(messageID).apply { find() }.group(1)

            val referenceNumberRegex = "Reference no\\. (\\d+)"
            val referenceNumber =
                Pattern.compile(referenceNumberRegex).matcher(messageID).apply { find() }.group(1)

            return arrayOf(
                "0$firstPhoneNumber විසින් $transferDate දින ඔබේ ගිණුමට රු. $transferredAmount ක් රීලෝඩ් කර ඇත.\nඔබගේ නව ශේෂය රු. $newBalance කි.\nයොමු අංකය: $referenceNumber",
                2
            )

        } else {
            return arrayOf(messageID, 2)
        }
    }
}

//RELOAD NOT SUCCESSFUL. PLEASE CONTACT YOUR RETAILER FOR ASSISTANCE. REFERENCE NO: 10762617237

