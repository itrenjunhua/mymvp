package com.renj.mvp.test.network;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.renj.mvp.base.rxpresenter.RxPresenter;
import com.renj.mvp.mode.ModelManager;
import com.renj.mvp.mode.http.utils.CustomSubscriber;
import com.renj.mvp.utils.rxjava.RxUtils;

import java.util.Map;

import javax.inject.Inject;

/**
 * ======================================================================
 * 作者：Renj
 * <p>
 * 创建时间：2017-05-15   18:05
 * <p>
 * 描述：
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class WeatherPresenter extends RxPresenter<WeatherControl.WeatherView> implements WeatherControl.WeatherPresenter {

    @Inject
    public WeatherPresenter(ModelManager modelManager) {
        super(modelManager);
    }

    @Override
    public void getData(@IntRange final int requestCode, String path, Map<String, String> queryMap) {

        addDisposable(mModelManager.getWeather(path, queryMap)
                .compose(RxUtils.newInstance().<String>threadTransformer())
                .subscribeWith(new CustomSubscriber<String>(requestCode, mView) {
                    @Override
                    public void onResult(@NonNull String s) {
                        mView.setData(requestCode, s);
                    }
                }));
    }
}
