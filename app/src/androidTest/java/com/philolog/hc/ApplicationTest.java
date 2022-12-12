package com.philolog.hc;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends Application {

    public ApplicationTest() {
        //super(Application.class);
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (InputStream fin) throws Exception {
        //File fl = new File(filePath);
        //FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    @Test
    public void test_pempw() {
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

    @Test
    public void test_verbs() {
        String[] persons = {"first", "second", "third"};
        String[] numbers = {"singular", "plural"};
        String[] tenses = {"Present", "Imperfect", "Future", "Aorist", "Perfect", "Pluperfect"};
        String[] voices = {"Active", "Middle", "Passive"};
        String[] moods = {"Indicative", "Subjunctive", "Optative", "Imperative"};

        String[] personsabbrev = {"1st", "2nd", "3rd"};
        String[] numbersabbrev = {"sing.", "pl."};
        String[] tensesabbrev = {"pres.", "imp.", "fut.", "aor.", "perf.", "plup."};
        String[] voicesabbrev = {"act.", "mid.", "pass."};
        String[] moodsabbrev = {"ind.", "subj.", "opt.", "imper."};

        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        boolean print_lines = true;
        //ClassLoader classLoader = this.getClass().getClassLoader();
        //URL resource = classLoader.getResource("new.txt");
        String file_contents = "";
        try {
            InputStream f = appContext.getAssets().open("new.txt");
            file_contents = getStringFromFile(f);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue("Verb file not found", false);
            return;
        }
        String[] rows = file_contents.split("\n");
        //Assert.assertEquals(34852, rows.length);
        Verb v = new Verb();

        int line = 1;
        for (int verb_num = 0; verb_num < 127; verb_num++) {

            //let verb = Verb2.init(verbid: verb_num)
            //let vf = VerbForm(.unset, .unset, .unset, .unset, .unset, verb: verb_num);
            GreekVerb vf = new GreekVerb();

            boolean isOida = false;

            if (verb_num == 118 || verb_num == 119) {
                print_lines = true;
                isOida = true;
            }
            else {
                print_lines = false;
                isOida = false;
            }
            v.getVerb(verb_num);
            Assert.assertEquals("", rows[line],"Verb " + verb_num + ". " + v.present);
            line += 2;
            //if verb.present == "οἶδα" || verb.present == "σύνοιδα"
            //{
            //    isOida = true
            //}
            for (int tense = 0; tense < tenses.length; tense++)
            {
                vf.tense = tense;
                for (int voice = 0; voice < voices.length; voice++)
                {
                    vf.voice = voice;
                    for (int mood = 0; mood < moods.length; mood++)
                    {
                        vf.mood = mood;
                        if (mood != 0/*indicative*/ && (tense == 4/*.perfect*/ || tense == 5/*.pluperfect*/ || tense == 1/*.imperfect*/ || (tense == 2/*.future*/ && mood != 2/*.optative*/)))
                        {
                            if (isOida && tense == 4/*.perfect*/ && voice == 0/*.active*/) {
                                Log.i("abc", "abc");
                            }
                            else {
                                continue;
                            }
                        }
                        //else if isOida && mood != .indicative && (tense == .pluperfect || tense == .imperfect || (tense == .future && mood != .optative))
                        //{
                        //    continue
                        //}

                        var voi = "";
                        if (voice == 1/*.middle*/ && mood == 3/*.imperative*/) {
                            voi = "Middle";
                        }
                        else if (voice == 2/*.passive*/ && mood == 3/*.imperative*/) {
                            voi = "Passive";
                        }
                        else if (vf.getVoiceDescription() == "Middle/Passive" && voice == 1/*.middle*/) {
                            voi = "Middle (" + vf.getVoiceDescription() + ")";
                        }
                        else if (vf.getVoiceDescription() == "Middle/Passive" && voice == 2/*.passive*/) {
                            voi = "Passive (" + vf.getVoiceDescription() + ")";
                        }
                        else {
                            voi = vf.getVoiceDescription();
                        }

                        String sec = tenses[tense] + " " + voi + " " + moods[mood];
                        if (print_lines) {
                            Log.i("abc",line + " - " + sec);
                        }
                        Assert.assertEquals("line: " + line + ". verb: " + verb_num + " " + tense + " " + voice + " " + mood + " " + isOida, rows[line], sec);

                        if (rows[line] != sec) {
                            Assert.assertTrue("wrong", false);
                            return;
                        }
                        line += 1;
                        for (int number = 0; number < numbers.length; number++)
                        {
                            vf.number = number;
                            for (int person = 0; person < persons.length; person++)
                            {
                                vf.person = person;
                                String form = vf.getForm(1, 0).replaceAll(",\n", ", ");
                                String form_d = vf.getForm(1,1).replaceAll(",\n", ", ");

                                if (mood == 3/*.imperative*/ && person == 0/*.first*/) {
                                    form = "NF";
                                    form_d = "NDF";
                                }

                                if (form == "")
                                {
                                    form = "NF";
                                }
                                if (form_d == "")
                                {
                                    form_d = "NDF";
                                }
                                String label = (person + 1) + ((number == 0/*.singular*/) ? "s" : "p");

                                String x = label + ": " + form + " ; " + form_d;
                                if (print_lines) {
                                    Log.i("abc","\t" + line + " - " + x);
                                }
                                Assert.assertEquals("", rows[line], x);
/*
                                let is_equal_insensitive = x.compare(rows[line], options: NSString.CompareOptions.diacriticInsensitive, range: nil, locale: nil)
                                XCTAssertEqual(is_equal_insensitive, ComparisonResult.orderedSame)

                                let is_equal_literal = x.compare(rows[line], options: NSString.CompareOptions.literal, range: nil, locale: nil)
                                XCTAssertEqual(is_equal_literal, ComparisonResult.orderedSame)

                                XCTAssertEqual(rows[line], x, "line: \(line). verb: \(vf.verbid) \(vf.person) \(vf.number) \(vf.tense) \(vf.voice) \(vf.mood)")
    */
                                if (rows[line] != x) {
                                    return;
                                }
                                line += 1;
                            }
                        }
                    }
                }
            }
            //next verb
            line += 1;
        }
    }
}

