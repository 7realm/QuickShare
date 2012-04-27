package gov.nasa.pds.android;

import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.RestrictionType;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        // get attribute value and convert it to enum
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AddRestrictionView);
        int attributeValue = attributes.getInt(R.styleable.AddRestrictionView_restrictionType, 0);
        restrictionType = RestrictionType.values()[attributeValue];
        attributes.recycle();

        // inflate content view
        LayoutInflater.from(context).inflate(R.layout.add_restriction, this, true);
        restrictionText = (TextView) findViewById(R.id.restrictionText);
        restrictionText.setText(getRestrictionText());

        // tune select button
        selectButton = (Button) findViewById(R.id.restrictionSelectButton);
        selectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start select activity for result
                Intent intent = new Intent(getContext(), PageViewActivity.class);
                intent.putExtra(PageViewActivity.EXTRA_QUERY_TYPE, convert(restrictionType).name());
                ((Activity) getContext()).startActivityForResult(intent, PageViewActivity.REQUEST_SELECT_RESTRICTION);
            }
        });
    }

    private static QueryType convert(RestrictionType restrictionType) {
        switch (restrictionType) {
        case TARGET_TYPE:
            return QueryType.GET_TYPES_INFO;
        case TARGET:
            return QueryType.GET_TARGETS_INFO;
        case MISSION:
            return QueryType.GET_MISSIONS_INFO;
        case INSTRUMENT:
            return QueryType.GET_INSTRUMENTS_INFO;
        default:
            return null;
        }
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
}
