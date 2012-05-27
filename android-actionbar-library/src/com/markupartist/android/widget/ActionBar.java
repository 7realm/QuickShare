/*
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.markupartist.android.widget;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.markupartist.android.widget.actionbar.R;

public class ActionBar extends RelativeLayout implements OnClickListener {

    private LayoutInflater mInflater;
    private RelativeLayout mBarView;
    private ImageView mLogoView;
    private View mBackIndicator;
    private EditText mTitleView;
    private LinearLayout mActionsView;
    private ImageButton mHomeBtn;
    private RelativeLayout mHomeLayout;
    private ProgressBar mProgress;
    private TitleChangeListener titleChangeListener;

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(mBarView);

        mLogoView = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
        mHomeLayout = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
        mHomeBtn = (ImageButton) mBarView.findViewById(R.id.actionbar_home_btn);
        mBackIndicator = mBarView.findViewById(R.id.actionbar_home_is_back);

        mTitleView = (EditText) mBarView.findViewById(R.id.actionbar_title);
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

        mProgress = (ProgressBar) mBarView.findViewById(R.id.actionbar_progress);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar);
        CharSequence title = a.getString(R.styleable.ActionBar_title);
        if (title != null) {
            setTitle(title);
        }
        a.recycle();

        mTitleView.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (titleChangeListener != null) {
                        titleChangeListener.onTitleChanged(v.getText());
                    }
                }

                return false;
            }
        });
    }

    public void setHomeAction(Action action) {
        mHomeBtn.setOnClickListener(this);
        mHomeBtn.setTag(action);
        mHomeBtn.setImageResource(action.getDrawable());
        mHomeLayout.setVisibility(View.VISIBLE);
    }

    public void clearHomeAction() {
        mHomeLayout.setVisibility(View.GONE);
    }

    /**
     * Shows the provided logo to the left in the action bar.
     *
     * This is ment to be used instead of the setHomeAction and does not draw a divider to the left of the provided logo.
     *
     * @param resId The drawable resource id
     */
    public void setHomeLogo(int resId) {
        // TODO: Add possibility to add an IntentAction as well.
        mLogoView.setImageResource(resId);
        mLogoView.setVisibility(View.VISIBLE);
        mHomeLayout.setVisibility(View.GONE);
    }

    /**
     * Emulating Honeycomb, setdisplayHomeAsUpEnabled takes a boolean and toggles whether the "home" view should have a little triangle
     * indicating "up".
     *
     * @param show if "up" triangle will be shown
     */
    public void setDisplayHomeAsUpEnabled(boolean show) {
        mBackIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setTitleEditable(boolean isTitleEditable) {
        mTitleView.setFocusable(isTitleEditable);
        mTitleView.setFocusableInTouchMode(true);
        mTitleView.setBackgroundResource(isTitleEditable ? R.drawable.shape_edit_light : android.R.color.transparent);
    }

    public void setTitleChangeListener(TitleChangeListener titleChangeListener) {
        this.titleChangeListener = titleChangeListener;
    }

    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }

    /**
     * Set the enabled state of the progress bar.
     *
     * @param One of {@link View#VISIBLE}, {@link View#INVISIBLE}, or {@link View#GONE}.
     */
    public void setProgressBarVisibility(int visibility) {
        mProgress.setVisibility(visibility);
    }

    /**
     * Returns the visibility status for the progress bar.
     *
     * @param One of {@link View#VISIBLE}, {@link View#INVISIBLE}, or {@link View#GONE}.
     */
    public int getProgressBarVisibility() {
        return mProgress.getVisibility();
    }

    /**
     * Function to set a click listener for Title TextView
     *
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link Action}s.
     *
     * @param actionList the actions to add
     */
    public void addActions(List<Action> actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     *
     * @param action the action to add
     */
    public void addAction(Action action) {
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     *
     * @param action the action to add
     * @param index the position at which to add the action
     */
    public void addAction(Action action, int index) {
        mActionsView.addView(inflateAction(action), index);
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mActionsView.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     *
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mActionsView.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     *
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mActionsView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mActionsView.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    mActionsView.removeView(view);
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
        return mActionsView.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     *
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.actionbar_item, mActionsView, false);

        // set action image
        ImageView imageView = (ImageView) view.findViewById(R.id.actionBarItemImage);
        imageView.setImageResource(action.getDrawable());

        // set action text
        TextView textView = (TextView) view.findViewById(R.id.actionBarItemText);
        textView.setText(action.getText());

        view.setTag(action);
        view.setOnClickListener(this);
        return view;
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
        private int drawable;
        private String text;

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
        void onTitleChanged(CharSequence newTitle);
    }
}
