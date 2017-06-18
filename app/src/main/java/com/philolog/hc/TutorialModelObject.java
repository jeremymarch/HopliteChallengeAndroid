package com.philolog.hc;



/**
 * Created by anupamchugh on 26/12/15.
 */
public enum TutorialModelObject {

    RED(R.string.TutInfo, R.layout.tutorialhtml),
    BLUE(R.string.TutAcknowledgements, R.layout.tutorialhtml);

    private int mTitleResId;
    private int mLayoutResId;

    TutorialModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
