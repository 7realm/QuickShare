/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data;

import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.data.queries.PagedQuery;
import gov.nasa.pds.soap.entities.PagedResults;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import soap.EnvelopeProcess;
import android.util.Log;

/**
 * Data provider class, that will execute all queries.
 *
 * @author 7realm
 * @version 1.0
 */
public class DataCenter {
    /** How many items are displayed per one page. */
    public static final int ITEMS_PER_PAGE = 20;

    private static final int DAYS_PER_YEAR = 365;
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;
    private static DateFormat DATE_LONG = new SimpleDateFormat("MMMMM dd yyyy 'at' HH:mm");

    private static final Pattern INDENTS = Pattern.compile("^(\\s*)\\S(?:.*\\S)?(\\s*)$", Pattern.MULTILINE);
    private static final Pattern MAX_LENGTH = Pattern.compile("^.*$", Pattern.MULTILINE);

    private static final String URL_PATTERN = "http://%s:8080/nasa_pds_ws/services/PlanetaryDataSystemPort";

    private static String url = "50.19.174.233";

    /**
     * Format date to long format.
     *
     * @param date the date to format
     * @return the formatted date
     */
    public static String formatLong(Date date) {
        return date == null ? "" : DATE_LONG.format(date);
    }

    /**
     * Formats period to correct string.
     *
     * @param startDate the period start date
     * @param endDate the period end date
     * @return the formatted period string
     */
    public static String formatPeriod(Date startDate, Date endDate) {
        // handle null values
        if (endDate == null) {
            endDate = new Date();
        } else if (startDate == null) {
            startDate = endDate;
        }

        // calculate duration
        long duration = endDate.getTime() - startDate.getTime();

        // get time periods
        long minutes = duration / MILLISECONDS_PER_MINUTE;
        long hours = minutes / MINUTES_PER_HOUR;
        long days = hours / HOURS_PER_DAY;
        long years = days / DAYS_PER_YEAR;

        // correct days because of Feb 29
        days -= endDate.getYear() / 4 - startDate.getYear() / 4;

        return String.format("%d years %d days %02d hours", years, days % DAYS_PER_YEAR, hours % HOURS_PER_DAY);
    }

    /**
     * Process description by removing common indentation from start and end.
     *
     * @param text the description text
     * @return the processed text
     */
    public static String processDescription(String text) {
        // find max line length
        int maxLength = 0;
        Matcher maxLengthMatcher = MAX_LENGTH.matcher(text);
        while (maxLengthMatcher.find()) {
            maxLength = Math.max(maxLength, maxLengthMatcher.end() - maxLengthMatcher.start());
        }

        // find minimum start and end indents
        int startIndent = Integer.MAX_VALUE;
        int endIndent = Integer.MAX_VALUE;
        Matcher matcher = INDENTS.matcher(text);
        while (matcher.find()) {
            int length = matcher.end() - matcher.start();
            int curStartIndent = matcher.group(1).length();
            int curEndIndent = matcher.group(2).length();
            if (curStartIndent == 0 && startIndent + length < maxLength) {
                curStartIndent = startIndent;
            }

            if (curEndIndent == 0 && endIndent + length < maxLength) {
                curEndIndent = endIndent;
            }

            startIndent = Math.min(startIndent, curStartIndent);
            endIndent = Math.min(endIndent, curEndIndent);
        }

        // remove start and end indents
        if (startIndent > 0 && startIndent < maxLength) {
            text = text.replaceAll("(?m)^\\s{" + startIndent + "}", "");
        }
        if (endIndent > 0 && endIndent < maxLength) {
            text = text.replaceAll("(?m)\\s{" + endIndent + "}$", "");
        }
        return text;
    }

    /**
     * Executes paged query.
     *
     * @param query the query to execute
     * @return the page results
     */
    public static PagedResults executePagedQuery(PagedQuery query) {
        Log.i("soap", "Executing paged query: " + query.getQueryType());

        PagedResults result = (PagedResults) executeMethod(query.getEnvelope());

        if (result == null) {
            result = new PagedResults();
            result.setTotal(0);
        }
        return result;
    }

    /**
     * Executes object query.
     *
     * @param query the object query
     * @return the result from query
     */
    @SuppressWarnings("unchecked")
    public static <T> T executeObjectQuery(ObjectQuery<T> query) {
        Log.i("soap", "Executing object query: " + query.getQueryType());

        return (T) executeMethod(query.getEnvelope());
    }

    private static Object executeMethod(SoapSerializationEnvelope envelope) {
        try {
            // execute soap call
            EnvelopeProcess.parseEnvelope(envelope);
            
            return envelope.getResponse();
        } catch (SoapFault soapFault) {
            Log.e("soap", "Soap fault : " + soapFault.faultstring);
        } catch (IOException e) {
            Log.e("soap", "I/O error: " + e.getMessage(), e);
        } catch (XmlPullParserException e) {
            Log.e("soap", "Xml parser error: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("soap", "Unexpected exception when calling soap method: " + e.getMessage(), e);
        }
        return null;

    }

    /**
     * Gets server url.
     *
     * @return the server url or host
     */
    public static String getUrl() {
        return url;
    }

    /**
     * Sets server url.
     *
     * @param url the server url
     */
    public static void setUrl(String url) {
        DataCenter.url = url;
    }

    /**
     * Check if connection is correct.
     *
     * @return true if connection is possible
     */
    public static boolean testConnection() {
        try {
            // execute simple request
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(String.format(URL_PATTERN, url));
            httpclient.execute(httpget);
            return true;
        } catch (IOException e) {
            Log.e("soap", "Failed to test connection.", e);
            return false;
        }
    }
}
