package com.philolog.hc;

/**
 * Created by jeremy on 3/10/16.
 */
public class VerbSequence
{

    public int seq;

    public native int nextVerbSeq(GreekVerb vf1, GreekVerb vf2);
    public native void resetVerbSeq();
    public native int setupUnits(boolean[] unitArray, boolean isHCGame);
    public native boolean VerbSeqInit(String path);

    static {
        //http://stackoverflow.com/questions/1007861/how-do-i-get-a-list-of-jni-libraries-which-are-loaded/1008631#1008631
        //try {
        System.loadLibrary("libGreek");
        //} catch (Exception e) { }
    }
}

