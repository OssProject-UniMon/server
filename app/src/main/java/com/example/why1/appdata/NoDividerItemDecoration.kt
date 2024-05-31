package com.example.why1.appdata

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NoDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val divider = ColorDrawable(context.getColor(R.color.transparent))

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }

    override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(0, 0, 0, 0)
    }
}