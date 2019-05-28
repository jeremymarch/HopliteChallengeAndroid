package com.philolog.hc;

/**
 * Created by jeremy on 8/7/15.
 */
public class Verb {
    public int verbId;
    public String present;
    public String future;
    public String aorist;
    public String perfect;
    public String perfmid;
    public String aoristpass;
    public String def;

    //public native void getRandomVerb();
    public native void getVerb(int verbId);
    public native int deponentType();

    static {
        //http://stackoverflow.com/questions/1007861/how-do-i-get-a-list-of-jni-libraries-which-are-loaded/1008631#1008631
        //try {
            System.loadLibrary("libGreek");
        //} catch (Exception e) { }
    }

}
