package com.laka.androidlib.net;


import com.laka.androidlib.net.header.IHeaderManager;
import com.laka.androidlib.net.http.HttpParameterBuilder;
import com.laka.androidlib.net.http.IHttpManager;
import com.laka.androidlib.net.http.okHttp.OkHttpManager;

/**
 * @Author Lyf
 * @CreateTime 2018/2/8
 * @Description A HttpManager class is in charge of Managing Http Framework.
 * Such as, sets some settings of http or decides to use which http library.
 *
 * Single responsibility principle：
 * This class is simply for initializing Http's setting and getting an instance of class which enclosed the http library.
 **/
public final class HttpManager {

    // A tag for logging.
    private final static String TAG = HttpManager.class.getSimpleName();

    // mHttpManager is an IHttpManager instance.
    private static IHttpManager mHttpManager;

    // Init HttpManager.
    public static void initHttpManager(HttpParameterBuilder httpParameterBuilder, IHeaderManager headerManager) {

        mHttpManager = new OkHttpManager(httpParameterBuilder,headerManager);

        /*
         * Here is the code Shows you how to use Retrofit instead of OkHttp.
         * As you can see, What you only have to do is creating another Manager which implements the IHttpManager.
         */
        // mHttpManager = new RetrofitManager(httpBuilder);
    }

    /**
     * Returns An IHttpManager instance.
     * If the project never initialized the HttpManager, A RuntimeException will be thrown.
     */
    static IHttpManager getHttpManager() {

        if (mHttpManager == null) {
            throw new RuntimeException("You have to invoke initHttpManager() to init HttpManager before you use it.");
        }

        return mHttpManager;
    }

    private HttpManager() {
        throw new UnsupportedOperationException("You can't instantiate this class but invoke getHttpManager() after called initHttpManager().");
    }

}
