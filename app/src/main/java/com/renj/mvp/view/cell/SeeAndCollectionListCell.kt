package com.renj.mvp.view.cell

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.renj.mvp.R
import com.renj.mvp.mode.db.bean.ListSeeAndCollectionDB
import com.renj.mvp.view.activity.WebViewActivity
import com.renj.view.recyclerview.adapter.*

/**
 * ======================================================================
 *
 *
 * 作者：Renj
 * 邮箱：itrenjunhua@163.com
 *
 *
 * 创建时间：2019-06-14   14:27
 *
 *
 * 描述：收藏和查看List条目样式
 *
 *
 * 修订历史：
 *
 *
 * ======================================================================
 */
class SeeAndCollectionListCell(isSeeList: Boolean) :
        BaseRecyclerCell<ListSeeAndCollectionDB>(R.layout.cell_see_and_collection_list) {

    private var isSeeList = false

    init {
        this.isSeeList = isSeeList
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder<out BaseRecyclerCell<*>>, position: Int, itemData: ListSeeAndCollectionDB?) {
        holder.setText(R.id.tv_see_and_collection_title, itemData!!.title)

        var textView = holder.getView<TextView>(R.id.tv_see_and_collection_count)
        if (isSeeList) {
            textView.visibility = View.VISIBLE
            textView.text = "查看次数：" + itemData.seeCount
        } else {
            textView.visibility = View.GONE
        }
    }

    override fun onItemClick(context: Context, recyclerAdapter: RecyclerAdapter<*>,
                             holder: RecyclerViewHolder<out BaseRecyclerCell<*>>,
                             itemView: View, position: Int, itemData: ListSeeAndCollectionDB?) {
        val intent = Intent(context, WebViewActivity::class.java)
        val bundleData = WebViewActivity.BundleData(itemData!!.pid, itemData.dataId, itemData.title,
                itemData.content, itemData.url, itemData.images.split(","), WebViewActivity.TYPE_LIST)
        intent.putExtra("data", bundleData)
        context.startActivity(intent)
    }
}
