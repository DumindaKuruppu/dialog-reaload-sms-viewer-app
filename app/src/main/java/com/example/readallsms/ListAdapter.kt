package com.example.readallsms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.readallsms.databinding.RowLayoutBinding
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
        view.sms_sender.text = list[potition].senderName
        view.sms_message.text = list[potition].message
        view.date.text = list[potition].date
        return view
    }
}