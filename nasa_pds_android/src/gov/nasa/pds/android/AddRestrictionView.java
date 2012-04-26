package gov.nasa.pds.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddRestrictionView extends RelativeLayout {
    private Button selectButton;
    private TextView restrictionText;
    private RestrictionType restrictionType;

    public AddRestrictionView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public AddRestrictionView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AddRestrictionView);
        int str = a.getInt(R.styleable.AddRestrictionView_restrictionType, 0);
        restrictionType = RestrictionType.values()[str];
        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.add_restriction, this, true);
        restrictionText = (TextView) findViewById(R.id.restrictionText);
        restrictionText.setText(getRestrictionText());

        selectButton = (Button) findViewById(R.id.restrictionSelectButton);
        selectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Select " + restrictionType + "!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CharSequence getRestrictionText() {
        StringBuilder builder = new StringBuilder();
        builder.append("<select ");
        switch (restrictionType) {
        case TARGET_TYPE:
            builder.append("target type");
            break;
        case TARGET:
            builder.append("target");
            break;
        case MISSION:
            builder.append("mission");
            break;
        case INSTRUMENT:
            builder.append("instrument");
            break;
        }
        builder.append(">");
        return builder;
    }

    public static enum RestrictionType {
        TARGET_TYPE,
        TARGET,
        MISSION,
        INSTRUMENT
    }

}
