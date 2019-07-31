package com.renj.mvp.view.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.BindView
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout
import com.renj.daggersupport.DaggerSupportPresenterFragment
import com.renj.mvp.R
import com.renj.mvp.controller.IMyCSDNController
import com.renj.mvp.mode.bean.response.BannerAndNoticeRPB
import com.renj.mvp.mode.bean.response.GeneralListRPB
import com.renj.mvp.presenter.MyCSDNPresenter
import com.renj.mvp.utils.MyCommonUtils
import com.renj.mvp.view.cell.CellFactory
import com.renj.mvpbase.view.LoadingStyle
import com.renj.pagestatuscontroller.IRPageStatusController
import com.renj.pagestatuscontroller.annotation.RPageStatus
import com.renj.view.recyclerview.adapter.IRecyclerCell
import com.renj.view.recyclerview.adapter.RecyclerAdapter
import com.renj.view.recyclerview.draw.LinearItemDecoration

/**
 * ======================================================================
 *
 *
 * 作者：Renj
 * 邮箱：renjunhua@anlovek.com
 *
 *
 * 创建时间：2019-04-15   16:45
 *
 *
 * 描述：
 *
 *
 * 修订历史：
 *
 *
 * ======================================================================
 */
class MyCSDNFragment : DaggerSupportPresenterFragment<MyCSDNPresenter>(), IMyCSDNController.IMyCSDNView {

    private var pageNo = 1
    private var pageSize = 20

    @BindView(R.id.swipe_toLoad_layout)
    lateinit var swipeToLoadLayout: SwipeToLoadLayout
    @BindView(R.id.swipe_target)
    lateinit var recyclerView: RecyclerView
    private var recyclerAdapter: RecyclerAdapter<IRecyclerCell<*>>? = null

    private var cells = ArrayList<IRecyclerCell<*>>()

    companion object {

        fun newInstance(): MyCSDNFragment {
            val args = Bundle()
            val fragment = MyCSDNFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.my_csdn_github_fragment
    }

    override fun initData() {
        swipeToLoadLayout.setOnRefreshListener {
            pageNo = 1
            cells.clear()
            requestBannerData(LoadingStyle.LOADING_REFRESH)
            requestListData(LoadingStyle.LOADING_REFRESH)
        }
        swipeToLoadLayout.setOnLoadMoreListener {
            requestListData(LoadingStyle.LOADING_LOAD_MORE)
        }

        recyclerAdapter = RecyclerAdapter()
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = recyclerAdapter
        recyclerView.addItemDecoration(LinearItemDecoration(LinearLayoutManager.VERTICAL))

        pageNo = 1
        cells.clear()
        requestBannerData(LoadingStyle.LOADING_PAGE)
        requestListData(LoadingStyle.LOADING_REFRESH)
    }

    private fun requestBannerData(loadingStyle: Int) {
        mPresenter.bannerRequest(loadingStyle)
    }

    private fun requestListData(loadingStyle: Int) {
        mPresenter.listRequest(loadingStyle, pageNo, pageSize)
    }

    override fun bannerRequestSuccess(requestCode: Int, bannerAndNoticeRPB: BannerAndNoticeRPB) {
        if (cells.size > 0) {
            cells.add(0, CellFactory.createBannerCell(bannerAndNoticeRPB.data.banners) as IRecyclerCell<*>)
            cells.add(1, CellFactory.createNoticeCell(bannerAndNoticeRPB.data.notices) as IRecyclerCell<*>)
        } else {
            cells.add(CellFactory.createBannerCell(bannerAndNoticeRPB.data.banners) as IRecyclerCell<*>)
            cells.add(CellFactory.createNoticeCell(bannerAndNoticeRPB.data.notices) as IRecyclerCell<*>)
        }
        recyclerAdapter?.setData(cells)
    }

    override fun listRequestSuccess(requestCode: Int, generalListRPB: GeneralListRPB) {
        cells.addAll(CellFactory.createGeneralListCell(generalListRPB.data.list) as List<IRecyclerCell<*>>)
        recyclerAdapter?.setData(cells)

        if (pageNo >= generalListRPB.data.page) {
            swipeToLoadLayout.isLoadingMore = false
            swipeToLoadLayout.isLoadMoreEnabled = false
            recyclerAdapter?.addAndNotifyAll(CellFactory.createNoMoreCell() as IRecyclerCell<*>)
        } else {
            swipeToLoadLayout.isLoadMoreEnabled = true
        }
        pageNo += 1
    }

    /**
     * 处理状态页面的事件
     *
     * @param iRPageStatusController 控制器
     * @param pageStatus             点击事件产生的页面状态
     * @param object                 绑定对象
     * @param view                   点击事件产生的 View
     * @param viewId                 点击事件产生的 View 的 id
     */
    override fun handlerPageLoadException(iRPageStatusController: IRPageStatusController<*>, pageStatus: Int, `object`: Any, view: View, viewId: Int) {
        if (pageStatus == RPageStatus.ERROR && viewId == R.id.tv_error) {
            pageNo = 1
            requestBannerData(LoadingStyle.LOADING_PAGE)
            requestListData(LoadingStyle.LOADING_REFRESH)
        } else if (pageStatus == RPageStatus.NET_WORK && viewId == R.id.tv_reload) {
            pageNo = 1
            // 此处修改页面状态是因为在 MyApplication 中指定了当网络异常时点击不自动修改为 loading 状态
            rPageStatusController.changePageStatus(RPageStatus.LOADING)
            requestBannerData(LoadingStyle.LOADING_PAGE)
            requestListData(LoadingStyle.LOADING_REFRESH)
        } else if (pageStatus == RPageStatus.NET_WORK && viewId == R.id.tv_net_work) {
            MyCommonUtils.openNetWorkActivity(activity)
        }

    }

    override fun showCustomResultPage(status: Int, loadingStyle: Int, `object`: Any?) {
        if (loadingStyle == LoadingStyle.LOADING_REFRESH)
            swipeToLoadLayout.isRefreshing = false
        if (loadingStyle == LoadingStyle.LOADING_LOAD_MORE)
            swipeToLoadLayout.isLoadingMore = false
    }
}
