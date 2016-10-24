package core.sontruong.com.filedownloader.base;

/**
 * Created by truongthanhson on 10/25/16.
 */
public interface DownloadContext {

    void onDownloadStart();
    void onDownloadProgressChanged(float percent);
    void onDownloadCancelled();
    void onDownloadFinish();

    void onDownloadFailed();

}
