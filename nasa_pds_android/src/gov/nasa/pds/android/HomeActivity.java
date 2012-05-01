/*
 * Created by 7realm at 01.05.2012 21:06:56. All rights reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.data.EntityType;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 *
 *
 * @author TCSDESIGNER, TCSDEVELOPER
 * @version 1.0
 */
public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @SuppressWarnings("unused")
    public void onTouchScreenClick(View v) {
        startBrowsingFor(EntityType.TARGET_TYPE);
    }

    @SuppressWarnings("unused")
    public void onTargetsButtonClick(View v) {
        startBrowsingFor(EntityType.TARGET);
    }

    @SuppressWarnings("unused")
    public void onMissionsButtonClick(View v) {
        startBrowsingFor(EntityType.MISSION);
    }

    @SuppressWarnings("unused")
    public void onInstrumentsButtonClick(View v) {
        startBrowsingFor(EntityType.INSTRUMENT);
    }

    private void startBrowsingFor(EntityType entityType) {
        Intent intent = new Intent(this, PageViewActivity.class);
        intent.putExtra(PageViewActivity.EXTRA_ENTITY_TYPE, entityType);
        startActivity(intent);
    }
}
