package com.lib;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class ZipUtility {
    public static final void zipFolder(File inputFolder, File zipFile) {
        ZipOutputStream zipStream = null;
        try {
            zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
            zip(inputFolder, inputFolder, zipFile, zipStream);
            zipStream.finish();
        } catch (IOException e) {
            Log.e("zip", "Failed to compress folder " + inputFolder + " to file " + zipFile);
        } finally {
            Streams.close(zipStream);
        }
    }

    private static final void zip(File inputFolder, File zipBaseFolder, File zipFile, ZipOutputStream zipStream) throws IOException {
        File[] files = inputFolder.listFiles();
        for (File file : files) {
            if (file.equals(zipFile)) {
                continue;
            }
            if (file.isDirectory()) {
                zip(file, zipBaseFolder, zipFile, zipStream);
            } else {
                ZipEntry entry = new ZipEntry(file.getPath().substring(zipBaseFolder.getPath().length() + 1));
                zipStream.putNextEntry(entry);

                Streams.copy(new FileInputStream(file), zipStream, false);
            }
        }
    }

    public static final void unzip(File zipFile, File extractTo) {
        try {
            ZipFile archive = new ZipFile(zipFile);
            Enumeration<? extends ZipEntry> e = archive.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                File file = new File(extractTo, entry.getName());
                if (entry.isDirectory() && !file.exists()) {
                    file.mkdirs();
                } else {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }

                    Streams.copy(archive.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)), true);
                }
            }
        } catch (IOException e) {
            Log.e("zip", "Failed to uncompress " + zipFile + " to " + extractTo, e);
        }
    }
}
