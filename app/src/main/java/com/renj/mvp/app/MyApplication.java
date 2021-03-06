package com.renj.mvp.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.renj.cachelibrary.CacheManageUtils;
import com.renj.httplibrary.RetrofitUtil;
import com.renj.mvp.R;
import com.renj.mvp.mode.db.DBHelper;
import com.renj.mvp.mode.db.bean.DaoMaster;
import com.renj.mvp.mode.db.bean.DaoSession;
import com.renj.mvp.mode.file.FileHelper;
import com.renj.mvp.mode.http.ApiServer;
import com.renj.mvp.mode.http.HttpHelper;
import com.renj.mvp.utils.ImageLoaderUtils;
import com.renj.mvpbase.mode.ModelManager;
import com.renj.pagestatuscontroller.RPageStatusManager;
import com.renj.pagestatuscontroller.annotation.RPageStatus;
import com.renj.utils.AndroidUtils;
import com.renj.utils.common.UIUtils;

import okhttp3.Request;

/**
 * ======================================================================
 * 作者：Renj
 * <p>
 * 创建时间：2017-05-11   16:53
 * <p>
 * 描述：继承至Application的类，设置默认全局的异常处理，初始化全局的相关变量等操作
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
    }

    // 初始化Application
    private void initApplication() {
        AndroidUtils.init(this);
        // 初始化全局的异常处理机制
        // MyExceptionHandler.newInstance().initMyExceptionHandler(this);
        // ANR监测
//        if (AndroidUtils.isDebug()) {
//            ANRWatchDog.init();
//        }

        // 初始化数据库框架
        initGreenDao();

        // 初始化 Retrofit
        RetrofitUtil.newInstance()
                .addApiServerClass(ApiServer.class)
                .addInterceptor((chain) -> {
                    Request originalRequest = chain.request();
                    // 拼接 APP_TOKEN 头
                    Request sessionIdRequest = originalRequest.newBuilder()
                            .addHeader(AppConfig.APP_TOKEN_KEY, AppConfig.APP_TOKEN_VALUE)
                            .build();
                    return chain.proceed(sessionIdRequest);
                })
                .initRetrofit(ApiServer.BASE_URL);

        // 初始化 ModelManager，注意 需要先 初始化 Retrofit
        ModelManager.newInstance()
                .addDBHelper(new DBHelper())
                .addFileHelper(new FileHelper())
                .addHttpHelper(new HttpHelper());

        // 在子线程中初始化相关库
        initOnNewThread();
    }

    public static DaoSession daoSession;

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, AppConfig.DATABASE_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    private void initOnNewThread() {
        UIUtils.runOnNewThread(() -> {
            // 初始化图片加载库
            ImageLoaderUtils.init(MyApplication.this);
            // 初始化缓存类
            CacheManageUtils.initCacheUtil(MyApplication.this);
            // 配置全局页面状态控制框架
            RPageStatusManager.getInstance()
                    .addPageStatusView(RPageStatus.LOADING, R.layout.status_view_loading)
                    .addPageStatusView(RPageStatus.EMPTY, R.layout.status_view_empty)
                    .addPageStatusView(RPageStatus.NET_WORK, R.layout.status_view_network)
                    .addPageStatusView(RPageStatus.ERROR, R.layout.status_view_error);
        });
    }
}
