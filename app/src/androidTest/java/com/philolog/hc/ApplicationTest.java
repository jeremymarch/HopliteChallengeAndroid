package com.philolog.hc;

import android.app.Application;
import org.junit.Test;
import org.junit.Assert;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

public class ApplicationTest extends Application {

    public ApplicationTest() {
        //super(Application.class);
    }

    @Test
    public void test_pempv() {
        GreekVerb a = new GreekVerb();
        a.verbid = 1;
        a.person = 0;
        a.number = 0;
        a.tense = 0;
        a.mood = 0;
        a.voice = 0;
        String f = a.getForm(1,0);
        Assert.assertEquals("πέμπω", f);
    }
}

