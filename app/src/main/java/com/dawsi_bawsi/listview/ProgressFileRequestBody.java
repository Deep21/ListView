package com.dawsi_bawsi.listview;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Spartiate on 05/03/2016.
 */
public class ProgressFileRequestBody extends RequestBody {

    private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE
    private static final String TAG = "ProgressFileRequestBody";
    private int position;
    private final File file;
    private final ProgressListener listener;
    private final String contentType;

    public ProgressFileRequestBody(File file, String contentType, ProgressListener listener) {
        Log.d(TAG, "ProgressFileRequestBody: ");
        this.file = file;
        this.contentType = contentType;
        this.listener = listener;
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[4096];
        FileInputStream in = new FileInputStream(file);
        long total = 0;
        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                total += read;
                listener.transferred(total * 100 / contentLength());
                sink.write(buffer, 0, read);
                //out.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }

/*        Source source = null;
        try {
            source = Okio.source(file);
            long total = 0;
            long read;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                Log.d(TAG, "writeTo: " + total);
                long t = (total * 100 / contentLength());
                    listener.transferred(t);
                sink.flush();


            }
        } finally {
            Util.closeQuietly(source);
        }*/
    }

    public interface ProgressListener {
        void transferred(long num);
    }

    public int getPostion(){
        return position;
    }

}