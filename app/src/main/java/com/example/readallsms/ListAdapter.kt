package com.example.readallsms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.readallsms.databinding.RowLayoutBinding
import kotlinx.android.synthetic.main.activity_main.view.sms_list_view
import kotlinx.android.synthetic.main.row_layout.view.*

private lateinit var binding: RowLayoutBinding



class ListAdapter(val context: Context, val list: ArrayList<SmsData>) : BaseAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(potition: Int): Any {
        return list[potition]
    }

    override fun getItemId(potition: Int): Long {
        return potition.toLong()
    }

    override fun getView(potition: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.row_layout,parent,false)
        val smsMessageTextView = view.findViewById<TextView>(R.id.sms_message)
        view.sms_message.text = list[potition].message[0].toString()

        if (list[potition].message.size > 1) {
            if (list[potition].message[1] == 1) {
                smsMessageTextView.setTextColor(ContextCompat.getColor(context, R.color.reload_success_font_color))
            } else if (list[potition].message[1] == 2) {
                smsMessageTextView.setTextColor(ContextCompat.getColor(context, R.color.reload_other_font_color))
            } else if (list[potition].message[1] == 3) {
                smsMessageTextView.setTextColor(ContextCompat.getColor(context, R.color.reload_not_success_font_color))
            }
        }


        view.date.text = list[potition].date
        return view
    }
}