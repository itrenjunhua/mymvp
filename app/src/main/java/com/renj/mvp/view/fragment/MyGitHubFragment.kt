package com.renj.mvp.view.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.renj.mvp.R
import com.renj.mvp.controller.IMyGitHubController
import com.renj.mvp.mode.bean.response.BannerAndNoticeRPB
import com.renj.mvp.mode.bean.response.GeneralListRPB
import com.renj.mvp.presenter.MyGitHubPresenter
import com.renj.mvp.utils.MyCommonUtils
import com.renj.mvp.view.cell.*
import com.renj.mvpbase.view.LoadingStyle
import com.renj.pagestatuscontroller.IRPageStatusController
import com.renj.pagestatuscontroller.annotation.RPageStatus
import com.renj.rxsupport.rxview.RxBasePresenterFragment
import com.renj.view.recyclerview.adapter.*
import com.renj.view.recyclerview.draw.LinearItemDecoration
import kotlinx.android.synthetic.main.my_csdn_github_fragment.*

/**
 * ======================================================================
 *
 *
 * 作者：Renj
 * 邮箱：itrenjunhua@163.com
 *
 *
 * 创建时间：2019-07-07   16:45
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
class MyGitHubFragment : RxBasePresenterFragment<MyGitHubPresenter>(), IMyGitHubController.IMyGithubView {

    private var pageNo = 1
    private var pageSize = 20

    private var cells = ArrayList<MultiItemEntity>()
    private var recyclerAdapter: MultiItemAdapter<MultiItemEntity>? = null

    companion object {
        fun newInstance(): MyGitHubFragment {
            val args = Bundle()
            val fragment = MyGitHubFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.my_csdn_github_fragment
    }

    override fun initView(contentView: View?) {
    }

    override fun initData() {
        swipe_toLoad_layout.setOnRefreshListener {
            pageNo = 1
            cells.clear()
            requestBannerData(LoadingStyle.LOADING_REFRESH)
            requestListData(LoadingStyle.LOADING_REFRESH)
        }
        swipe_toLoad_layout.setOnLoadMoreListener {
            requestListData(LoadingStyle.LOADING_LOAD_MORE)
        }

        recyclerAdapter = object : MultiItemAdapter<MultiItemEntity>() {
            override fun <C : BaseRecyclerCell<MultiItemEntity?>?> getRecyclerCell(itemTypeValue: Int): C {
                return when (itemTypeValue) {
                    RecyclerItemType.BANNER_CELL_TYPE -> {
                        BannerCell() as C
                    }
                    RecyclerItemType.NOTICE_CELL_TYPE -> {
                        NoticeCell() as C
                    }
                    RecyclerItemType.NO_MORE_CELL_TYPE -> {
                        NoMoreCell() as C
                    }
                    else ->
                        GeneralListCell() as C
                }
            }
        }
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        swipe_target.layoutManager = linearLayoutManager
        swipe_target.adapter = recyclerAdapter
        swipe_target.addItemDecoration(LinearItemDecoration(LinearLayoutManager.VERTICAL))

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
            cells.add(0, SimpleMultiItemEntity(RecyclerItemType.BANNER_CELL_TYPE, bannerAndNoticeRPB.data.banners))
            cells.add(1, SimpleMultiItemEntity(RecyclerItemType.NOTICE_CELL_TYPE, bannerAndNoticeRPB.data.notices))
        } else {
            cells.add(SimpleMultiItemEntity(RecyclerItemType.BANNER_CELL_TYPE, bannerAndNoticeRPB.data.banners))
            cells.add(SimpleMultiItemEntity(RecyclerItemType.NOTICE_CELL_TYPE, bannerAndNoticeRPB.data.notices))
        }
        recyclerAdapter?.setData(cells)
    }

    override fun listRequestSuccess(requestCode: Int, generalListRPB: GeneralListRPB) {
        cells.addAll(generalListRPB.data.list)
        recyclerAdapter?.setData(cells)

        if (pageNo >= generalListRPB.data.page) {
            swipe_toLoad_layout.isLoadingMore = false
            swipe_toLoad_layout.isLoadMoreEnabled = false
            recyclerAdapter?.addAndNotifyAll(SimpleMultiItemEntity(RecyclerItemType.NO_MORE_CELL_TYPE, null))
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
            swipe_toLoad_layout.isRefreshing = false
        if (loadingStyle == LoadingStyle.LOADING_LOAD_MORE)
            swipe_toLoad_layout.isLoadingMore = false
    }

}
