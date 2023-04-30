package com.jarvis.instarepost.classes;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import java.io.File;

public class SingleMediaScanner implements MediaScannerConnectionClient {
    private File file;
    private MediaScannerConnection mediaScannerConnection;

    public SingleMediaScanner(Context context, File file2) {
        this.file = file2;
        this.mediaScannerConnection = new MediaScannerConnection(context, this);
        this.mediaScannerConnection.connect();
    }

    public void onMediaScannerConnected() {
        this.mediaScannerConnection.scanFile(this.file.getAbsolutePath(), null);
    }

    public void onScanCompleted(String str, Uri uri) {
        this.mediaScannerConnection.disconnect();
    }
}
