package com.example.myapplication

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TabsAdapter(private val pages: List<View>) : RecyclerView.Adapter<TabsAdapter.PageHolder>() {
    class PageHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = pages.size

    override fun getItemViewType(position: Int) = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val page = pages[viewType]
        page.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        return PageHolder(page)
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) = Unit
}
