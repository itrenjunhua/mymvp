package com.renj.home.view.fragment

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.renj.arouter.ARouterPath
import com.renj.common.view.cell.CommonCellFactory
import com.renj.home.R
import com.renj.home.controller.IMyCSDNController
import com.renj.home.mode.bean.response.BannerAndNoticeRPB
import com.renj.home.mode.bean.response.GeneralListRPB
import com.renj.home.presenter.MyCSDNPresenter
import com.renj.home.view.cell.CellFactory
import com.renj.mvpbase.view.LoadingStyle
import com.renj.pagestatuscontroller.IRPageStatusController
import com.renj.pagestatuscontroller.annotation.RPageStatus
import com.renj.rxsupport.rxview.RxBasePresenterFragment
import com.renj.utils.net.NetWorkUtils
import com.renj.view.recyclerview.adapter.IRecyclerCell
import com.renj.view.recyclerview.adapter.RecyclerAdapter
import com.renj.view.recyclerview.draw.LinearItemDecoration
import kotlinx.android.synthetic.main.my_csdn_github_fragment.*

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
@Route(path = ARouterPath.PATH_HOME_FRAGMENT_MY_CSDN)
class MyCSDNFragment : RxBasePresenterFragment<MyCSDNPresenter>(), IMyCSDNController.IMyCSDNView {

    private var pageNo = 1
    private var pageSize = 20

    private var cells = ArrayList<IRecyclerCell<*>>()
    private var recyclerAdapter: RecyclerAdapter<IRecyclerCell<*>>? = null

    companion object {
        const val REQUEST_CODE_BANNER = 1
        const val REQUEST_CODE_LIST = 2
    }

    override fun getLayoutId(): Int {
        return R.layout.my_csdn_github_fragment
    }

    override fun initData() {
        swipe_toLoad_layout.setOnRefreshListener {
            pageNo = 1
            cells.clear()
            requestBannerData(LoadingStyle.LOADING_REFRESH, REQUEST_CODE_BANNER)
            requestListData(LoadingStyle.LOADING_REFRESH, REQUEST_CODE_LIST)
        }
        swipe_toLoad_layout.setOnLoadMoreListener {
            requestListData(LoadingStyle.LOADING_LOAD_MORE, REQUEST_CODE_LIST)
        }

        recyclerAdapter = RecyclerAdapter()
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        swipe_target.layoutManager = linearLayoutManager
        swipe_target.adapter = recyclerAdapter
        swipe_target.addItemDecoration(LinearItemDecoration(LinearLayoutManager.VERTICAL))

        pageNo = 1
        cells.clear()
        requestBannerData(LoadingStyle.LOADING_PAGE, REQUEST_CODE_BANNER)
        requestListData(LoadingStyle.LOADING_REFRESH, REQUEST_CODE_LIST)
    }

    private fun requestBannerData(loadingStyle: Int, requestCode: Int) {
        mPresenter.bannerRequest(loadingStyle, requestCode)
    }

    private fun requestListData(loadingStyle: Int, requestCode: Int) {
        mPresenter.listRequest(loadingStyle, requestCode, pageNo, pageSize)
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
            swipe_toLoad_layout.isLoadingMore = false
            swipe_toLoad_layout.isLoadMoreEnabled = false
            recyclerAdapter?.addAndNotifyAll(CommonCellFactory.createNoMoreCell() as IRecyclerCell<*>)
        } else {
            swipe_toLoad_layout.isLoadMoreEnabled = true
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
            requestBannerData(LoadingStyle.LOADING_PAGE, REQUEST_CODE_BANNER)
            requestListData(LoadingStyle.LOADING_REFRESH, REQUEST_CODE_LIST)
        } else if (pageStatus == RPageStatus.NET_WORK && viewId == R.id.tv_reload) {
            pageNo = 1
            // 此处修改页面状态是因为在 BaseApplication 中指定了当网络异常时点击不自动修改为 loading 状态
            rPageStatusController.changePageStatus(RPageStatus.LOADING)
            requestBannerData(LoadingStyle.LOADING_PAGE, REQUEST_CODE_BANNER)
            requestListData(LoadingStyle.LOADING_REFRESH, REQUEST_CODE_LIST)
        } else if (pageStatus == RPageStatus.NET_WORK && viewId == R.id.tv_net_work) {
            NetWorkUtils.openNetWorkActivity()
        }

    }

    override fun handlerResultOtherStyle(status: Int, loadingStyle: Int, requestCode: Int, `object`: Any?) {
        if (loadingStyle == LoadingStyle.LOADING_REFRESH)
            swipe_toLoad_layout.isRefreshing = false
        if (loadingStyle == LoadingStyle.LOADING_LOAD_MORE)
            swipe_toLoad_layout.isLoadingMore = false
    }
}