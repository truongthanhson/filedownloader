package core.sontruong.com.filedownloader.base;

import android.net.Uri;
import android.support.annotation.VisibleForTesting;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * Created by truongthanhson on 10/25/16.
 */

public class DownloadTask implements Callable<DownloadResult> {
    private static final int MAX_REDIRECTS = 5;

    public static final int HTTP_TEMPORARY_REDIRECT = 307;
    public static final int HTTP_PERMANENT_REDIRECT = 308;
    private DownloadRequest request;

    public DownloadTask(DownloadRequest request) {
        this.request = request;
    }

    @Override
    public DownloadResult call() throws Exception {
        return fetchSync(request);
    }


    DownloadResult fetchSync(DownloadRequest downloadRequest) {
        HttpURLConnection connection = null;

        try {
            connection = downloadFrom(Uri.parse(downloadRequest.getUrl()), MAX_REDIRECTS);

            if (connection != null) {
//                request.getListeners().onResponse(connection.getInputStream(), -1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private HttpURLConnection downloadFrom(Uri uri, int maxRedirects) throws IOException {
        HttpURLConnection connection = openConnectionTo(uri);
        int responseCode = connection.getResponseCode();

        if (isHttpSuccess(responseCode)) {
            return connection;

        } else if (isHttpRedirect(responseCode)) {
            String nextUriString = connection.getHeaderField("Location");
            connection.disconnect();

            Uri nextUri = (nextUriString == null) ? null : Uri.parse(nextUriString);
            String originalScheme = uri.getScheme();

            if (maxRedirects > 0 && nextUri != null && !nextUri.getScheme().equals(originalScheme)) {
                return downloadFrom(nextUri, maxRedirects - 1);
            } else {
                String message = maxRedirects == 0
                        ? error("URL %s follows too many redirects", uri.toString())
                        : error("URL %s returned %d without a valid redirect", uri.toString(), responseCode);
                throw new IOException(message);
            }

        } else {
            connection.disconnect();
            throw new IOException(String
                    .format("Image URL %s returned HTTP code %d", uri.toString(), responseCode));
        }
    }

    @VisibleForTesting
    static HttpURLConnection openConnectionTo(Uri uri) throws IOException {
        URL url = new URL(uri.toString());
        return (HttpURLConnection) url.openConnection();
    }

    private static boolean isHttpSuccess(int responseCode) {
        return (responseCode >= HttpURLConnection.HTTP_OK &&
                responseCode < HttpURLConnection.HTTP_MULT_CHOICE);
    }

    private static boolean isHttpRedirect(int responseCode) {
        switch (responseCode) {
            case HttpURLConnection.HTTP_MULT_CHOICE:
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
            case HTTP_TEMPORARY_REDIRECT:
            case HTTP_PERMANENT_REDIRECT:
                return true;
            default:
                return false;
        }
    }

    private static String error(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

}
