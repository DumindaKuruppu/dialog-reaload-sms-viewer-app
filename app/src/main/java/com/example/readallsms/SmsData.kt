package com.example.readallsms

import java.io.Serializable

data class SmsData(val senderName: String?, val date: String, val message: Array<Any>) {
}