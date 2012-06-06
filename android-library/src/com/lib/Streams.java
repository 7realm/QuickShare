/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package com.lib;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for working with streams.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public final class Streams {
    /**
     * Default buffer size for use in {@link #copy(InputStream, OutputStream, boolean)}.
     */
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * Private constructor, to prevent instantiation. This class has only static methods.
     */
    private Streams() {
        // Does nothing
    }

    /**
     * Copies the contents of the given {@link InputStream} to the given {@link OutputStream}. Shortcut for
     *
     * <pre>
     * copy(pInputStream, pOutputStream, new byte[8192]);
     * </pre>
     *
     * @param pInputStream The input stream, which is being read. It is guaranteed, that {@link InputStream#close()} is
     *        called on the stream.
     * @param pOutputStream The output stream, to which data should be written. May be null, in which case the input
     *        streams contents are simply discarded.
     * @param pClose True guarantees, that {@link OutputStream#close()} is called on the stream. False indicates, that
     *        only {@link OutputStream#flush()} should be called finally.
     *
     * @return Number of bytes, which have been copied.
     * @throws IOException An I/O error occurred.
     */
    public static long copy(InputStream pInputStream,
        OutputStream pOutputStream, boolean pClose)
        throws IOException {
        return copy(pInputStream, pOutputStream, pClose,
            new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copies the contents of the given {@link InputStream} to the given {@link OutputStream}.
     *
     * @param pIn The input stream, which is being read. It is guaranteed, that {@link InputStream#close()} is called on
     *        the stream.
     * @param pOut The output stream, to which data should be written. May be null, in which case the input streams
     *        contents are simply discarded.
     * @param pClose True guarantees, that {@link OutputStream#close()} is called on the stream. False indicates, that
     *        only {@link OutputStream#flush()} should be called finally.
     * @param pBuffer Temporary buffer, which is to be used for copying data.
     * @return Number of bytes, which have been copied.
     * @throws IOException An I/O error occurred.
     */
    public static long copy(InputStream pIn,
        OutputStream pOut, boolean pClose,
        byte[] pBuffer)
        throws IOException {
        OutputStream out = pOut;
        InputStream in = pIn;
        try {
            long total = 0;
            for (;;) {
                int res = in.read(pBuffer);
                if (res == -1) {
                    break;
                }
                if (res > 0) {
                    total += res;
                    if (out != null) {
                        out.write(pBuffer, 0, res);
                    }
                }
            }
            if (out != null) {
                if (pClose) {
                    out.close();
                } else {
                    out.flush();
                }
                out = null;
            }
            in.close();
            in = null;
            return total;
        } finally {
            close(in);

            if (pClose) {
                close(out);
            }
        }
    }

    /**
     * This convenience method allows to read a {@link InputStream} content into a string. The platform's default
     * character encoding is used for converting bytes into characters.
     *
     * @param pStream The input stream to read.
     * @see #asString(InputStream, String)
     * @return The streams contents, as a string.
     * @throws IOException An I/O error occurred.
     */
    public static String asString(InputStream pStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(pStream, baos, true);
        return baos.toString();
    }

    /**
     * This convenience method allows to read a {@link InputStream} content into a string, using the given character
     * encoding.
     *
     * @param pStream The input stream to read.
     * @param pEncoding The character encoding, typically "UTF-8".
     * @see #asString(InputStream)
     * @return The streams contents, as a string.
     * @throws IOException An I/O error occurred.
     */
    public static String asString(InputStream pStream, String pEncoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(pStream, baos, true);
        return baos.toString(pEncoding);
    }

    /**
     * Write string content to stream.
     *
     * @param out the output stream
     * @param content the content
     * @throws IOException if error occurs
     */
    public static void writeToStream(OutputStream out, String content) throws IOException {
        writeToStream(out, content.getBytes());
    }

    /**
     * Write byte content to stream.
     *
     * @param out the output stream
     * @param content the content
     * @throws IOException if error occurs
     */
    public static void writeToStream(OutputStream out, byte[] content) throws IOException {
        try {
            out.write(content);
        } finally {
            close(out);
        }
    }

    /**
     * Close stream.
     *
     * @param stream the stream
     */
    public static void close(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
