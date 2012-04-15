package my.activity.demo.settings;

import java.util.Arrays;

import my.activity.demo.Helper;
import my.activity.demo.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // get selected item
        int selected = -1;
        String[] providerNames = getResources().getStringArray(R.array.provider_values);
        String providerName = Settings.getInstance().getProviderName();
        for (int i = 0; i < providerNames.length; i++) {
            if (providerNames[i].equals(providerName)) {
                selected = i;
                break;
            }
        }
        if (selected == -1) {
            throw new IllegalStateException("Provider name " + providerName + " is absent in " + Arrays.toString(providerNames));
        }

        // set settings
        ((TextView) findViewById(R.id.stable_become_timeout)).setText(String.valueOf(Settings.getInstance().getStableBecomeTimeout() / 1000));
        ((TextView) findViewById(R.id.stable_period_timeout)).setText(String.valueOf(Settings.getInstance().getStablePeriodTimeout() / 1000));
        ((Spinner) findViewById(R.id.provider_name)).setSelection(selected);
    }

    @SuppressWarnings("unused")
    public void saveClick(View v) {
        try {
            int stableBecomeSeconds = Integer.parseInt(((TextView) findViewById(R.id.stable_become_timeout)).getText().toString());
            Settings.getInstance().setStableBecomeTimeout(stableBecomeSeconds * 1000);

            int stablePeriodSeconds = Integer.parseInt(((TextView) findViewById(R.id.stable_period_timeout)).getText().toString());
            Settings.getInstance().setStablePeriodTimeout(stablePeriodSeconds * 1000);

            Settings.getInstance().setProviderName((String) ((Spinner) findViewById(R.id.provider_name)).getSelectedItem());

            Helper.log("settings", "All settings saved.");
        } catch (NumberFormatException e) {
            Helper.log("error", "Failed to set settings: " + e.getMessage());
        }
    }
}
