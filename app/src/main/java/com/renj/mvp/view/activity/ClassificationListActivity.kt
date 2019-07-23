package com.renj.mvp.view.activity

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.renj.arouter.ARouterPath
import com.renj.daggersupport.DaggerSupportPresenterActivity
import com.renj.mvp.R
import com.renj.mvp.controller.IClassificationListController
import com.renj.mvp.mode.bean.response.GeneralListRPB
import com.renj.mvp.presenter.ClassificationListPresenter
import com.renj.mvp.utils.MyCommonUtils
import com.renj.mvp.view.cell.CellFactory
import com.renj.mvpbase.view.LoadingStyle
import com.renj.pagestatuscontroller.IRPageStatusController
import com.renj.pagestatuscontroller.annotation.RPageStatus
import com.renj.view.recyclerview.adapter.IRecyclerCell
import com.renj.view.recyclerview.adapter.RecyclerAdapter
import com.renj.view.recyclerview.draw.LinearItemDecoration
import kotlinx.android.synthetic.main.classification_list_activity.*

/**
 * ======================================================================
 *
 *
 * 作者：Renj
 * 邮箱：renjunhua@anlovek.com
 *
 *
 * 创建时间：2019-07-09   11:03
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
@Route(path = ARouterPath.PATH_ACTIVITY_CLASSIFICATION_LIST, group = ARouterPath.GROUP_CLASSIFICATION)
class ClassificationListActivity : DaggerSupportPresenterActivity<ClassificationListPresenter>(), IClassificationListController.IClassificationListView {
    private var pageNo = 1
    private var pageSize = 20

    private var pid: Int = 0
    private var recyclerAdapter: RecyclerAdapter<IRecyclerCell<*>>? = null

    companion object {
        const val REQUEST_CODE_REFRESH = 1
        const val REQUEST_CODE_LOAD_MORE = 2
    }

    override fun getLayoutId(): Int {
        return R.layout.classification_list_activity
    }

    override fun initData() {
        setPageBack(true, false, null)
        setPageTitle(intent.getStringExtra("title"))

        pid = intent.getIntExtra("pid", 0)

        swipe_toLoad_layout.setOnRefreshListener {
            pageNo = 1
            mPresenter.classificationListRequest(LoadingStyle.LOADING_REFRESH, REQUEST_CODE_REFRESH, pid, pageNo, pageSize)
        }
        swipe_toLoad_layout.setOnLoadMoreListener {
            mPresenter.classificationListRequest(LoadingStyle.LOADING_LOAD_MORE, REQUEST_CODE_LOAD_MORE, pid, pageNo, pageSize)
        }

        recyclerAdapter = RecyclerAdapter()
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        swipe_target.layoutManager = linearLayoutManager
        swipe_target.adapter = recyclerAdapter
        swipe_target.addItemDecoration(LinearItemDecoration(LinearLayoutManager.VERTICAL))

        pageNo = 1
        mPresenter.classificationListRequest(LoadingStyle.LOADING_PAGE, REQUEST_CODE_REFRESH, pid, pageNo, pageSize)
    }

    override fun classificationListRequestSuccess(requestCode: Int, generalListRPB: GeneralListRPB) {
        if (requestCode == REQUEST_CODE_REFRESH)
            recyclerAdapter?.setData(CellFactory.createGeneralListCell(generalListRPB.data.list) as List<IRecyclerCell<*>>)
        if (requestCode == REQUEST_CODE_LOAD_MORE)
            recyclerAdapter?.addAndNotifyAll(CellFactory.createGeneralListCell(generalListRPB.data.list) as List<IRecyclerCell<*>>)

        if (pageNo >= generalListRPB.data.page) {
            swipe_toLoad_layout.isLoadingMore = false
            swipe_toLoad_layout.isLoadMoreEnabled = false
            recyclerAdapter?.addAndNotifyAll(CellFactory.createNoMoreCell() as IRecyclerCell<*>)
        } else {
            swipe_toLoad_layout.isLoadMoreEnabled = true
        }
        pageNo += 1
    }

    override fun handlerPageLoadException(iRPageStatusController: IRPageStatusController<out IRPageStatusController<*>>?, pageStatus: Int, `object`: Any?, view: View?, viewId: Int) {
        if (pageStatus == RPageStatus.ERROR && viewId == R.id.tv_error) {
            pageNo = 1
            mPresenter.classificationListRequest(LoadingStyle.LOADING_PAGE, REQUEST_CODE_REFRESH, pid, pageNo, pageSize)
        } else if (pageStatus == RPageStatus.NET_WORK && viewId == R.id.tv_reload) {
            pageNo = 1
            // 此处修改页面状态是因为在 MyApplication 中指定了当网络异常时点击不自动修改为 loading 状态
            rPageStatusController.changePageStatus(RPageStatus.LOADING)
            mPresenter.classificationListRequest(LoadingStyle.LOADING_PAGE, REQUEST_CODE_REFRESH, pid, pageNo, pageSize)
        } else if (pageStatus == RPageStatus.NET_WORK && viewId == R.id.tv_net_work) {
            MyCommonUtils.openNetWorkActivity(this)
        }
    }

    override fun handlerResultOtherStyle(status: Int, loadingStyle: Int, requestCode: Int, `object`: Any?) {
        if (requestCode == REQUEST_CODE_REFRESH)
            swipe_toLoad_layout.isRefreshing = false
        if (requestCode == REQUEST_CODE_LOAD_MORE)
            swipe_toLoad_layout.isLoadingMore = false
    }
}
