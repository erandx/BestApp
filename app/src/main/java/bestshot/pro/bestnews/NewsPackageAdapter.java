package bestshot.pro.bestnews;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsPackageAdapter extends ArrayAdapter<NewsPackage> {

    /**
     * Constructs a new {@link NewsPackageAdapter}.
     *
     * @param context      of the app
     * @param newsPackages is the list of newsPackage, which is the data source of the adapter
     */
    public NewsPackageAdapter(@NonNull Context context, @NonNull List<NewsPackage> newsPackages) {
        super(context, 0, newsPackages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_item, parent, false);

        }
        // Find the NewsPackage at the given position in the list of News

        NewsPackage currentNewsPackage = getItem(position);

        // Get and display the article's Title
        String newsTitle = currentNewsPackage.getWebTitle();
        TextView titleView = listItemView.findViewById(R.id.headline_text_view);
        titleView.setText(newsTitle);

        // Get and display the article's Title
        String newsSection = currentNewsPackage.getWebTrailText();
        TextView sectionNameView = listItemView.findViewById(R.id.article_text);
        sectionNameView.setText(newsSection);

        // Get and display the article's Section
        String textTitle = currentNewsPackage.getWebSection();
        TextView textView = listItemView.findViewById(R.id.article_section);
        textView.setText(textTitle);

        // Get and display the article's Author
        String newsAuthor = currentNewsPackage.getByLine() + " ";
        TextView authorView = listItemView.findViewById(R.id.author_text_view);
        authorView.setText(newsAuthor);

        // Format the date string (i.e. "Mar 28, 1981")
        String formattedDate = formatDate(currentNewsPackage.getWebPublicationDate());
        //find the textview in list_item with id miwok_text_view
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // Display the date of the current earthquake in that TextView
        dateView.setText(formattedDate);

        // Find the TextView with view ID time
        // Format the time string (i.e. "4:30 PM")
        String formattedTime = formatTime(currentNewsPackage.getWebPublicationDate());
        // Find and display the article's Time
        TextView timeView = listItemView.findViewById(R.id.time);
        timeView.setText(formattedTime);

        // Find and display the article's Thumbnail
        Bitmap newsPhoto = currentNewsPackage.getThumbnail();
        ImageView photoView = listItemView.findViewById(R.id.article_image);
        photoView.setImageBitmap(newsPhoto);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return a formatted time string (i.e. "Mar 3, '18") from a Date object.
     */
    private String formatDate(String dateObject) {
        final SimpleDateFormat inputParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        Date date_out = null;
        try {
            date_out = inputParser.parse(dateObject);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        final SimpleDateFormat outputFormatter = new SimpleDateFormat("MMM dd ''yy", Locale.US);
        return outputFormatter.format(date_out);
    }

    /**
     * Return a formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(String dateObject) {
        final SimpleDateFormat inputParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        Date date_out = null;
        try {
            date_out = inputParser.parse(dateObject);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        final SimpleDateFormat outputFormatter = new SimpleDateFormat("h:mm a", Locale.US);
        return outputFormatter.format(date_out);
    }
}
