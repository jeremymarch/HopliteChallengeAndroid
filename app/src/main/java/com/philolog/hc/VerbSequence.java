package com.philolog.hc;

/**
 * Created by jeremy on 3/10/16.
 */
public class VerbSequence
{

    public int seq;
    public int state;
    public native int vsNext(GreekVerb vf1, GreekVerb vf2);
    public native void vsReset();
    public native int setupUnits(boolean[] unitArray, boolean isHCGame);
    public native int vsInit(String path);

    public static final int STATE_ERROR    = 0;
    public static final int STATE_NEW      = 1;
    public static final int STATE_REP      = 2;
    public static final int STATE_GAME0VER = 3;

    static {
        //http://stackoverflow.com/questions/1007861/how-do-i-get-a-list-of-jni-libraries-which-are-loaded/1008631#1008631
        //try {
        System.loadLibrary("libGreek");
        //} catch (Exception e) { }
    }
}

