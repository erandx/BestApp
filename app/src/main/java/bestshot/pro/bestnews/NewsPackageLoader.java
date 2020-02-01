package bestshot.pro.bestnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsPackageLoader extends AsyncTaskLoader<List<NewsPackage>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsPackageLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link NewsPackageLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsPackageLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /*
     * This is on a background thread.
     */
    @Override
    public List<NewsPackage> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of newspackages.
        List<NewsPackage> newsPackages = QueryUtils.fetchNewsPackageData(mUrl);
        return newsPackages;
    }
}
