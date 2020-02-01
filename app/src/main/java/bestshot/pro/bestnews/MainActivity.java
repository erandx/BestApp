package bestshot.pro.bestnews;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<NewsPackage>> {

    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?";

    private static final String apiKey = BuildConfig.THE_GUARDIAN_API_KEY;
    private static final String REQUEST_URL = "&api-key=" + apiKey;

    // Extras at the end of the URL string
    private static final String URL_EXTRAS = "&show-fields=headline,trailText,shortUrl,thumbnail,byline";

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * ListView that holds the articles
     **/
    private ListView packageListView;

    /**
     * TextView when the list is empty
     */
    private TextView mEmptyStateTextView;
    /**
     * Adapter for the list of news
     */
    private NewsPackageAdapter mAdapter;

    /**
     * Returns a Guardian API URL string from all the components
     *
     * @param section section or tag in Guardian
     * @return URL string
     */
    public static String constructUrl(@Nullable String section, @Nullable String orderBy) {

        // Start the StringBuilder and add the URL Base to the beginning
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(GUARDIAN_REQUEST_URL);

        // If the section isn't null then add that
        if (section != null) {
            stringBuilder.append(section);
        } else {
            stringBuilder.append(R.string.category__default_value);
        }

        // If the orderBy isn't null then add that
        if (orderBy != null) {
            stringBuilder.append("&order-by="
                    + orderBy);
        } else {
            // order by newest articles by default if no preference set
            stringBuilder.append("&order-by="
                    + (R.string.pref_order_by_default));
        }

        // Add the extras to the query
        stringBuilder.append(URL_EXTRAS);

        // Add the API Key to the end of the query
        stringBuilder.append(REQUEST_URL);

        // LOG the API URL
        Log.i(LOG_TAG, "API GUARDIAN_REQUEST_URL: " + stringBuilder.toString());

        return stringBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the menu toolbar for app compat
        Toolbar mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // Hide the default title to use ours
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Find a reference to the {@link ListView} in the layout
        packageListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new NewsPackageAdapter(this, new ArrayList<NewsPackage>());

        // Find the feedback_view that is only visible when the list has no items
        mEmptyStateTextView = findViewById(R.id.empty_view);
        packageListView.setEmptyView(mEmptyStateTextView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        packageListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected article.
        packageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                NewsPackage currentNewsPackage = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsPackageUri = Uri.parse(currentNewsPackage.getUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsPackageUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<NewsPackage>> onCreateLoader(int i, Bundle bundle) {

        Log.i(LOG_TAG, "Test: onCreateLoader() called...");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sectionChoice = sharedPrefs.getString(
                getString(R.string.pref_topic_key),
                getString(R.string.category_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.pref_order_by_key),
                getString(R.string.pref_order_by_default)
        );

        // Construct the API URL to query the Guardian Dataset
        String GUARDIAN_SECTION = constructUrl(sectionChoice, orderBy);

        return new NewsPackageLoader(this, GUARDIAN_SECTION);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsPackage>> loader, List<NewsPackage> newsPackages) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsPackages != null && !newsPackages.isEmpty()) {
            mAdapter.addAll(newsPackages);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsPackage>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}