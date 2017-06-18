package com.philolog.hc;

/**
 * Created by jeremy on 8/7/15.
 */

public class GreekVerb
{
    public int person;
    public int number;
    public int tense;
    public int voice;
    public int mood;
    public int score = 0;
    public String elapsed;
    public Verb verb; //or int verbId?

    public native String addAccent(int accent, String str); //included here as a hack
    public native String getForm(int mf, int decompose);
    public native void generateForm();
    public native void changeFormByDegrees(int degrees);
    public native String getAbbrevDescription();

    public native boolean compareFormsCheckMFRecordResult(String actual, String expected, String elapsed, boolean MFPressed);

    static {
        //http://stackoverflow.com/questions/1007861/how-do-i-get-a-list-of-jni-libraries-which-are-loaded/1008631#1008631
        //try {
            System.loadLibrary("libGreek");
        //} catch (Exception e) { }
    }
}


