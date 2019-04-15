package com.renj.mvp.mode.http;

import com.renj.mvpbase.mode.IMvpHttpHelper;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

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
    Flowable<String> getWeather(@Path("path") String text, @QueryMap Map<String,String> queryMap);
}
