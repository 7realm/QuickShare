/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.ImageCenter;
import gov.nasa.pds.lessons.LessonRepository;
import android.app.Application;

/**
 * Current android application.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class NasaApplication extends Application {

    /**
     * Initializes application.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // load data from context
        ImageCenter.init(getApplicationContext());
        LessonRepository.load(getApplicationContext());

        // set url
        DataCenter.setUrl(getResources().getString(R.string.url));
    }

}
