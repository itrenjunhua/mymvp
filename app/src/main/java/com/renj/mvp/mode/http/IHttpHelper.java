package com.renj.mvp.mode.http;

import com.renj.mvp.mode.bean.response.BannerAndNoticeRPB;
import com.renj.mvp.mode.bean.response.ClassificationRPB;
import com.renj.mvp.mode.bean.response.FoundRPB;
import com.renj.mvp.mode.bean.response.GeneralListRPB;
import com.renj.mvpbase.mode.IMvpHttpHelper;

import io.reactivex.Observable;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * 邮箱：renjunhua@anlovek.com
 * <p>
 * 创建时间：2018-08-17   10:43
 * <p>
 * 描述：APP 操作网络的帮助类接口，将所有的网络相关操作方法定义在此接口中
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public interface IHttpHelper extends IMvpHttpHelper {
    /**
     * 我的CSDN banner和公告数据
     */
    Observable<BannerAndNoticeRPB> myCSDNBannerRequest();

    /**
     * 我的CSDN 列表数据
     */
    Observable<GeneralListRPB> myCSDNListRequest(int pageNo, int pageSize);

    /**
     * 我的GitHub banner和公告数据
     */
    Observable<BannerAndNoticeRPB> myGitHubBannerRequest();

    /**
     * 我的GitHub 列表数据
     */
    Observable<GeneralListRPB> myGitHubListRequest(int pageNo, int pageSize);

    /**
     * 发现页数据
     */
    Observable<FoundRPB> foundDataRequest();

    /**
     * 分类目录
     */
    Observable<ClassificationRPB> classificationDataRequest();

    /**
     * 分类列表
     */
    Observable<GeneralListRPB> classificationListRequest(int pid, int pageNo, int pageSize);
}
