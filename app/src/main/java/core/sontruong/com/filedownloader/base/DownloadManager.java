package core.sontruong.com.filedownloader.base;

/**
 * Created by truongthanhson on 10/25/16.
 */

public interface DownloadManager {

    void startDownload(DownloadRequest request);

    void cancelDownload(DownloadRequest request);

    void cancelAll();

}
