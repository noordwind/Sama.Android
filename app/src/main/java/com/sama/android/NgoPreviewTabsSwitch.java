package com.sama.android;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NgoPreviewTabsSwitch
        extends LinearLayout
        implements View.OnClickListener {

    private View mSwitch;
    private TextView childrenTab;
    private TextView donationsTab;
    private OnSwitched mListener;

    private View mContentView;
    private int inactiveColor;
    private int normalColor;

    public NgoPreviewTabsSwitch(Context context) {
        super(context);
        init();
    }

    public NgoPreviewTabsSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NgoPreviewTabsSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.switcher, this, true);

        childrenTab = mContentView.findViewById(R.id.children);
        donationsTab = mContentView.findViewById(R.id.donations);

        mSwitch = mContentView.findViewById(R.id.switcher);

        normalColor = ContextCompat.getColor(getContext(), R.color.white);
        inactiveColor = ContextCompat.getColor(getContext(), R.color.white);
    }

    public OnSwitched getListener() {
        return mListener;
    }

    public void setListener(OnSwitched listener) {
        mListener = listener;
    }

    public void setChildrenTabName(String name) {
        childrenTab.setText(name);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.donations:
                showDonations();
                break;

            case R.id.children:
                showChildren();
                break;

            default:
                //do nothing
                break;
        }
    }

    public void showDonations() {
        childrenTab.setOnClickListener(this);
        donationsTab.setOnClickListener(null);

        childrenTab.setTextColor(inactiveColor);
        donationsTab.setTextColor(normalColor);

        mSwitch.animate().translationX(mSwitch.getWidth());
        if (getListener() != null) {
            getListener().donationsShown();
        }
    }

    public void showChildren() {
        childrenTab.setOnClickListener(null);
        donationsTab.setOnClickListener(this);

        childrenTab.setTextColor(normalColor);
        donationsTab.setTextColor(inactiveColor);

        mSwitch.animate().translationX(0);

        if (getListener() != null) {
            getListener().childrenShown();
        }
    }

    public interface OnSwitched {
        /**
         * Indicates that switcher now set to free float.
         */
        void childrenShown();

        /**
         * Indicates that switcher now set to stationary.
         */
        void donationsShown();

    }

}
