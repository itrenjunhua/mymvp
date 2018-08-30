package com.renj.mvp.base.view;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.renj.mvp.mode.bean.BaseResponseBean;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2017-09-29   16:39
 * <p>
 * 描述：MVP模式中View顶层接口
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public interface IBaseView {
    /**
     * 获取布局文件资源id
     *
     * @return
     */
    @LayoutRes
    int getLayoutId();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 显示内容页面<br/> <b>特别注意：这个方法的自动回调时间是在获取服务器数据，调用 View(MVP模式中的V) 类中的设置数据方法之后，
     * 所以当设置数据方法中的代码会影响界面显示时需要特别注意，否则可能界面显示的状态和预期的不对<b/>
     *
     * @param e   表示返回空数据的是具体的哪个响应实体类，主要作用在一个页面多个请求时进行区分
     * @param <E>
     */
    <E> void showContentPage(@NonNull E e);

    /**
     * 显示正在加载中页面
     */
    void showLoadingPage();

    /**
     * 显示空数据页面
     *
     * @param e   表示返回空数据的是具体的哪个响应实体类，主要作用在一个页面多个请求时进行区分
     * @param <E>
     */
    <E extends BaseResponseBean> void showEmptyDataPage(@NonNull E e);

    /**
     * 显示网路连接异常页面
     */
    void showNetWorkErrorPage();

    /**
     * 显示错误页面<br/>
     * </b>
     *
     * @param e 错误信息
     */
    void showErrorPage(Throwable e);
}