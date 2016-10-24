package core.sontruong.com.filedownloader.base;


import java.io.File;
import java.util.Comparator;
import java.util.List;

/**
 * Created by truongthanhson on 10/25/16.
 */
public interface DownloadRequest extends Comparator<DownloadRequest> {
    String getId();

    String getUrl();

    File getDestination();

    File getTempStore();

    List<DownloadContext> getListeners();
}
