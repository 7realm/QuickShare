package gov.nasa.pds.data;

import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.soap.entities.WsDataFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.ksoap2.transport.Streams;

import android.content.Context;
import android.util.Log;

public class ImageCenter {
    private static File imagesDir;

    private static boolean isImage(String fileName) {
        return fileName != null && (
            fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"));
    }

    public static void init(Context context) {
        imagesDir = context.getDir("images", Context.MODE_PRIVATE);
    }

    public static boolean isImageFile(WsDataFile dataFile) {
        return dataFile.getContent() == null ? isImage(dataFile.getFilename()) : isImage(dataFile.getName());
    }

    public static File getImage(long id) {
        // try get image from cache
        File imageFile = new File(imagesDir, Long.toString(id));
        Log.i("soap", "Looking for image " + imageFile.getAbsolutePath());
        if (imageFile.exists()) {
            return imageFile;
        }

        // get image info
        WsDataFile dataFile = DataCenter.executeObjectQuery(new ObjectQuery<WsDataFile>(QueryType.GET_FILE, id));
        if (dataFile == null) {
            Log.w("soap", "Failed to get file for id " + id + ".");
            return null;
        }

        try {
            // file contains content
            if (dataFile.getContent() != null) {
                if (isImage(dataFile.getName())) {
                    Streams.writeToStream(new FileOutputStream(imageFile), dataFile.getContent());
                    Log.i("soap", "Image " + id + " was fetched from content.");
                    return imageFile;
                }
            } else {
                // file has attachment, check if it is image already
                if (isImage(dataFile.getFilename())) {
                    Streams.writeToStream(new FileOutputStream(imageFile), dataFile.getDataHandler().getContent());
                    Log.i("soap", "Image " + id + " was fetched from attachment.");
                    return imageFile;
                }

                // need one more query for URL
                String url = DataCenter.executePreviewQuery(id);
                if (url != null) {
                    // download file from url
                    URLConnection urlConnection = new URL(url).openConnection();
                    Streams.copy(urlConnection.getInputStream(), new FileOutputStream(imageFile), true);
                    Log.i("soap", "Image " + id + " was fetched from preview.");
                    return imageFile;
                }
            }
        } catch (IOException e) {
            Log.e("soap", "Failed to save image " + imageFile.getAbsolutePath(), e);
        }

        return null;
    }
}
