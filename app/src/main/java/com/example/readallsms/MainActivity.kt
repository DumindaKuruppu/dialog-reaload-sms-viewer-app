package com.example.readallsms

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.readallsms.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Date
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val requestReadSms: Int = 2
    private val eZreload: String = "eZ Reload"

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
                    val messageString = cursor.getString(messageID)

                    if (cursor.getString(nameID).equals(eZreload)) {
                        println(cursor.getString(messageID))
                        smsList.add(
                            SmsData(
                                cursor.getString(nameID),
                                Date(dateString.toLong()).toString(),
                                formatReloadSms(cursor.getString(messageID)),
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


    private fun formatReloadSms(messageID: String): String {

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

            val extractedData =
                "Phone number: $phoneNumber, Date: $date, Reference number: $referenceNumber, Amount: $amount"


            return extractedData
        } else {
            return messageID
        }
    }
}

