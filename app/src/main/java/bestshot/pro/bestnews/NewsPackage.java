package bestshot.pro.bestnews;

import android.graphics.Bitmap;

public class NewsPackage {

    private String mWebPublicationDate;
    private String mWebTitle;
    private String mUrl;
    private String mWebTrailText;
    private String mWebSection;
    private String mByLine;
    private Bitmap mThumbnail;

    /**
     * Constructs a new {@link NewsPackage} object
     *
     * @param webPublicationDate Publication date for the article
     * @param webTitle           Title of the article
     * @param url                Url of the article
     * @param webSection         Section of the article
     * @param byLine             Author of the article
     * @param thumbnail          Url to the thumbnail of the article
     */
    public NewsPackage(String webSection, String webTitle, String byLine, String webPublicationDate, String url, String webTrailText, Bitmap thumbnail) {

        mWebSection = webSection;
        mWebTitle = webTitle;
        mByLine = byLine;
        mWebPublicationDate = webPublicationDate;
        mUrl = url;
        mWebTrailText = webTrailText;
        mThumbnail = thumbnail;
    }

    public String getWebPublicationDate() {
        return mWebPublicationDate;
    }

    public String getWebTitle() {
        return mWebTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getWebTrailText() {
        return mWebTrailText;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public String getByLine() {
        return mByLine;
    }

    public String getWebSection() {
        return mWebSection;
    }
}
