/*
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */

package com.markupartist.android.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.markupartist.android.widget.actionbar.R;

public class ActionBar extends RelativeLayout implements OnClickListener, TextWatcher {
    private final ViewGroup layoutView;
    private TextView titleTextView;
    private final ViewGroup actionListView;
    private final ProgressBar progressBar;
    private TitleChangeListener titleChangeListener;
    private TitleType titleType = TitleType.LABEL;
    private final LayoutInflater layoutInflater;
    private String[] dropDownItems;

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        // inflate layout
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutView = (ViewGroup) layoutInflater.inflate(R.layout.actionbar, null);
        addView(layoutView);

        // store reusable elements
        actionListView = (ViewGroup) layoutView.findViewById(R.id.actionbarActionList);
        progressBar = (ProgressBar) layoutView.findViewById(R.id.actionbarProgressBar);

        // adjust title settings
        setTitleType(titleType);
    }

    public void setUpAction(Action action) {
        // make group visible
        ViewGroup upButtonLayout = (ViewGroup) layoutView.findViewById(R.id.actionbarUpLayout);
        upButtonLayout.setVisibility(View.VISIBLE);

        // set action to button
        View upActionView = inflateAction(action);
        upButtonLayout.addView(upActionView);
    }

    public void setTitleType(TitleType titleType) {
        this.titleType = titleType;

        switch (titleType) {
        case LABEL:
            titleTextView = (TextView) findViewById(R.id.actionbarTitleLabel);
            titleTextView.removeTextChangedListener(this);

            findViewById(R.id.actionbarTitleLabel).setVisibility(VISIBLE);
            findViewById(R.id.actionbarTitleEdit).setVisibility(GONE);
            findViewById(R.id.actionbarTitleDrowDown).setVisibility(GONE);
            break;

        case EDIT:
            titleTextView = (TextView) findViewById(R.id.actionbarTitleEdit);
            titleTextView.addTextChangedListener(this);

            findViewById(R.id.actionbarTitleLabel).setVisibility(GONE);
            findViewById(R.id.actionbarTitleEdit).setVisibility(VISIBLE);
            findViewById(R.id.actionbarTitleDrowDown).setVisibility(GONE);
            break;

        case DROP_DOWN:
            titleTextView = (TextView) findViewById(R.id.actionbarTitleDrowDown);
            titleTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (dropDownItems == null) {
                        Log.w("action_bar", "Drop down items are not set.");
                        return;
                    }

                    // show alert dialog that will emulate drop down
                    new AlertDialog.Builder(getContext())
                        .setTitle("Select browse item type:")
                        .setItems(dropDownItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // check if we did not selected the current item
                                if (!dropDownItems[which].equals(titleTextView.getText())) {
                                    titleTextView.setText(dropDownItems[which]);

                                    // notify title change listener
                                    if (titleChangeListener != null) {
                                        titleChangeListener.onTitleChanged(dropDownItems[which], which);
                                    }
                                }
                            }
                        })
                        .create().show();
                }
            });

            findViewById(R.id.actionbarTitleLabel).setVisibility(GONE);
            findViewById(R.id.actionbarTitleEdit).setVisibility(GONE);
            findViewById(R.id.actionbarTitleDrowDown).setVisibility(VISIBLE);
            break;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (titleChangeListener != null) {
            titleChangeListener.onTitleChanged(s.toString(), -1);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    public void setTitleChangeListener(TitleChangeListener titleChangeListener) {
        this.titleChangeListener = titleChangeListener;
    }

    public void setTitle(CharSequence title) {
        if (titleType == TitleType.LABEL || titleType == TitleType.EDIT) {
            titleTextView.setText(title);
        } else {
            throw new UnsupportedOperationException("Setting text title to incorrect title type: " + titleType);
        }
    }

    public void setTitle(int titleIndex, String[] dropDownItems) {
        this.dropDownItems = dropDownItems;
        if (titleType == TitleType.DROP_DOWN) {
            titleTextView.setText(dropDownItems[titleIndex]);
        } else {
            throw new UnsupportedOperationException("Setting spinner title to incorrect title type: " + titleType);
        }
    }

    /**
     * Set the visible state of the progress bar.
     *
     * @param isVisible if progress bar should be visible
     */
    public void setProgressBarVisibile(boolean isVisible) {
        progressBar.setVisibility(isVisible ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);

            // update view after action
            updateView(view, action);
        }
    }

    public void updateActions() {
        for (int i = 0; i < actionListView.getChildCount(); i++) {
            View childView = actionListView.getChildAt(i);
            if (childView.getTag() instanceof Action) {
                updateView(childView, (Action) childView.getTag());
            }
        }
    }

    /**
     * Adds a new {@link Action}.
     *
     * @param action the action to add
     */
    public void addAction(Action action) {
        int index = actionListView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     *
     * @param action the action to add
     * @param index the position at which to add the action
     */
    public void addAction(Action action, int index) {
        actionListView.addView(inflateAction(action), index);
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        actionListView.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     *
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        actionListView.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     *
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = actionListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = actionListView.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    actionListView.removeView(view);
                }
            }
        }
    }

    /**
     * Returns the number of actions currently registered with the action bar.
     *
     * @return action count
     */
    public int getActionCount() {
        return actionListView.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     *
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = layoutInflater.inflate(R.layout.actionbar_item, actionListView, false);

        updateView(view, action);

        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }

    private static void updateView(View view, Action action) {
        // set action image
        ImageView imageView = (ImageView) view.findViewById(R.id.actionBarItemImage);
        imageView.setImageResource(action.getDrawable());

        // set action text
        TextView textView = (TextView) view.findViewById(R.id.actionBarItemText);
        if (action.getText() == null || action.getText().trim().length() == 0) {
            textView.setVisibility(GONE);
        } else {
            textView.setVisibility(VISIBLE);
            textView.setText(action.getText());
        }
    }

    /**
     * Definition of an action that could be performed, along with a icon to show.
     */
    public static interface Action {
        int getDrawable();

        String getText();

        void performAction(View view);
    }

    public static abstract class AbstractAction implements Action {
        private final int drawable;
        private final String text;

        public AbstractAction(int drawable, String text) {
            this.drawable = drawable;
            this.text = text;
        }

        @Override
        public int getDrawable() {
            return drawable;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    public static interface TitleChangeListener {
        void onTitleChanged(CharSequence newTitle, int newTitlePosition);
    }

    /**
     * Represents type of action bar title.
     *
     * @author TCSASSEMBLER
     */
    public static enum TitleType {
        LABEL,
        EDIT,
        DROP_DOWN
    }
}
