package com.example.why1.appdata

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.why1.R

class Accountadapter(val context: Context, val TestList: ArrayList<AccountData>) : BaseAdapter() {
    override fun getCount(): Int {
        return TestList.size
    }

    override fun getItem(position: Int): Any {
        return TestList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, converterView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.listview_account,null)

        val tag = view.findViewById<TextView>(R.id.tag)
        val price = view.findViewById<TextView>(R.id.price)
        val date = view.findViewById<TextView>(R.id.buydate)
        val howbuy = view.findViewById<TextView>(R.id.howbuy)
        val storename = view.findViewById<TextView>(R.id.storename)

        val data = TestList[position]
        tag.text = data.tag
        price.text = data.price
        storename.text = data.storename
        date.text = data.date
        howbuy.text = data.howbuy

        return view
    }
}