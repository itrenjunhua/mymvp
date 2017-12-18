package com.renj.mvp.utils.cache;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2017-12-18   13:44
 * <p>
 * 描述：缓存管理线程辅助工具类。主要作用：1.用于切换线程；2.在子线程获取到结果后提供回调
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class CacheThreadResult<T> {
    private T execute;

    private CacheThreadResult() {
    }

    /**
     * 创建新的 {@link CacheThreadResult} 对象
     *
     * @param <T> 泛型
     * @return {@link CacheThreadResult} 对象
     */
    static <T> CacheThreadResult<T> create() {
        return new CacheThreadResult<>();
    }

    /**
     * 需要运行在新的线程的代码
     *
     * @param cacheCallBack {@link CacheCallBack<T>} 接口回调方法中的代码就是运行在新的线程的方法
     * @return {@link CacheThreadResult} 对象
     */
    CacheThreadResult<T> runOnNewThread(final CacheCallBack<T> cacheCallBack) {
        RCacheConfig.EXECUTORSERVICE.execute(new Runnable() {
            @Override
            public void run() {
                execute = cacheCallBack.execute();
            }
        });
        return this;
    }

    /**
     * 切换回主线程运行
     */
    private void returnMainThread(final CacheResultCallBack<T> cacheResultCallBack) {
        RCacheConfig.MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cacheResultCallBack.onResult(execute);
            }
        });
    }

    /**
     * 得到在子线程中运行代码得到的结果
     *
     * @param cacheResultCallBack 回调，具体的内容作为回调方法的参数
     */
    public void onResult(CacheResultCallBack<T> cacheResultCallBack) {
        if (cacheResultCallBack != null)
            returnMainThread(cacheResultCallBack);
    }

    /**
     * 将需要在新线程中执行的代码编入回调方法中
     *
     * @param <T>
     */
    interface CacheCallBack<T> {
        T execute();
    }

    /**
     * 结果回调
     *
     * @param <T>
     */
    public interface CacheResultCallBack<T> {
        void onResult(T result);
    }
}