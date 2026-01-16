package com.philolog.hc;

import android.content.Context;
import android.util.Log;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    public ApplicationTest() {
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
        String ret = convertStreamToString(fin);
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
    public void check_all_verbs() {
        String[] persons = {"first", "second", "third"};
        String[] numbers = {"singular", "plural"};
        String[] tenses = {"Present", "Imperfect", "Future", "Aorist", "Perfect", "Pluperfect"};
        String[] voices = {"Active", "Middle", "Passive"};
        String[] moods = {"Indicative", "Subjunctive", "Optative", "Imperative"};

        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        boolean print_lines = true;
        String file_contents = "";
        try {
            InputStream f = appContext.getAssets().open("new.txt");
            file_contents = getStringFromFile(f);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Verb file not found");
            return;
        }
        String[] rows = file_contents.split("\n");
        //Assert.assertEquals("Num rows in new.txt incorrect", 34852, rows.length);

        Verb v = new Verb();

        int line = 1;
        for (int verb_num = 0; verb_num < 127; verb_num++) {

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

            int dep = v.deponentType();
            String deponent_type = "";
            switch (dep) {
                case 0:
                    deponent_type = "";
                    break;
                case 1:
                    deponent_type = " (Middle Deponent)";
                    break;
                case 2:
                    deponent_type = " (Passive Deponent)";
                    break;
                case 3:
                    deponent_type = " (Partial Deponent)";
                    break;
                case 4:
                    deponent_type = " (Deponent gignomai)";
                    break;
                case 5:
                    deponent_type = " (Middle Deponent with 6th pp)";
                    break;
            }

            vf.verbid = verb_num;
            String lemma = v.present;
            if (v.present.length() == 0) {
                lemma = v.future;
            }
            Assert.assertEquals("Verb row incorrect", rows[line],"Verb " + verb_num + ". " + lemma + deponent_type);
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

                        String voi = "";
                        if (voice == 1/*.middle*/ && mood == 3/*.imperative*/) {
                            voi = "Middle";
                        }
                        else if (voice == 2/*.passive*/ && mood == 3/*.imperative*/) {
                            voi = "Passive";
                        }
                        else if (vf.getVoiceDescription().equals("Middle/Passive") && voice == 1/*.middle*/) {
                            voi = "Middle (" + vf.getVoiceDescription() + ")";
                        }
                        else if (vf.getVoiceDescription().equals("Middle/Passive") && voice == 2/*.passive*/) {
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

                        if (!rows[line].equals(sec)) {
                            Assert.assertEquals("Tense Voice Mood description incorrect", rows[line], sec);
                            //Log.e("abc", "abc: " + rows[line] + " : " + sec);
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

                                if (form.equals(""))
                                {
                                    form = "NF";
                                }
                                if (form_d.equals(""))
                                {
                                    form_d = "NDF";
                                }
                                String label = (person + 1) + ((number == 0/*.singular*/) ? "s" : "p");

                                String form_row = label + ": " + form + " ; " + form_d;
                                if (print_lines) {
                                    Log.i("abc","\t" + line + " - " + form_row);
                                }
                                Assert.assertEquals("Form row incorrect", rows[line], form_row);

                                boolean mfpressed = false;
                                if (form.contains(",")) {
                                    mfpressed = true;
                                }
                                //Log.e("abc", label + ": " + form + " : " + rows[line].split(";")[0] + " : " + mfpressed);

                                //Also check verbs with real comparison function
                                Assert.assertTrue("check compare forms", vf.compareFormsCheckMF(label + ": " + form, rows[line].split(" ;")[0], mfpressed));

                                if (!rows[line].equals(form_row)) {
                                    Assert.assertEquals("forms not equal", rows[line], form_row);
                                    return;
                                }
                                line += 1;
                            }//person
                        }//number
                        line += 1;
                    }
                }
            }
            //next verb
            //line += 1;
        }
    }
}
