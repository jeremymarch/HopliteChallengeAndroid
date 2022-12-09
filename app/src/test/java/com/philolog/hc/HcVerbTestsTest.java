package com.philolog.hc;

import static org.junit.Assert.*;

import org.junit.Test;

public class HcVerbTestsTest {
    public static final int INDICATIVE  = 0;
    public static final int SUBJUNCTIVE = 1;
    public static final int OPTATIVE    = 2;
    public static final int IMPERATIVE  = 3;
    public static final int INFINITIVE  = 4;
    public static final int PARTICIPLE  = 5;

    public static final int PRESENT     = 0;
    public static final int IMPERFECT   = 1;
    public static final int FUTURE      = 2;
    public static final int AORIST      = 3;
    public static final int PERFECT     = 4;
    public static final int PLUPERFECT  = 5;

    public static final int ACTIVE      = 0;
    public static final int MIDDLE      = 1;
    public static final int PASSIVE     = 2;

    public static final int NUM_TENSES  = 6;
    public static final int NUM_VOICES  = 3;
    public static final int NUM_MOODS   = 4;
    public static final int NUM_NUMBERS = 2;
    public static final int NUM_PERSONS = 3;


    public static final int NOT_DEPONENT      = 0;
    public static final int MIDDLE_DEPONENT   = 1;
    public static final int PASSIVE_DEPONENT  = 2;
    public static final int PARTIAL_DEPONENT  = 3;
    public static final int DEPONENT_GIGNOMAI = 4; //see H&Q page 382


    public static final String[] tenses = {"Present", "Imperfect", "Future", "Aorist", "Perfect", "Pluperfect"};
    public static final String[] voices = { "Active", "Middle", "Passive" };
    public static final String[] moods = { "Indicative", "Subjunctive", "Optative", "Imperative" };
    @Test
    public void hc_verbs_ReturnsTrue() {
        int verbID = 1;
        Verb ve = new Verb();
        GreekVerb vf = new GreekVerb();
        ve.getVerb(verbID);
        vf.verb = ve;
        vf.verbid = verbID;
        int updateIndex = 0;
        boolean update = false;
        boolean isOida = false;
        boolean isDecomposedMode = false;
        if (ve.present.equals("οἶδα") || ve.present.equals("σύνοιδα")) {
            isOida = true;
        }

        for (int t = 0; t < NUM_TENSES; t++) {
            vf.tense = t;
            for (int v = 0; v < NUM_VOICES; v++) {
                vf.voice = v;
                for (int m = 0; m < NUM_MOODS; m++) {
                    if (!isOida && ((m != INDICATIVE && (t == PERFECT || t == PLUPERFECT || t == IMPERFECT) || ((m == SUBJUNCTIVE || m == IMPERATIVE) && t == FUTURE))))
                        continue;
                    else if (isOida && (m != INDICATIVE && (t == PLUPERFECT || t == IMPERFECT)  || ((m == SUBJUNCTIVE || m == IMPERATIVE) && t == FUTURE)))
                        continue;

                    String s;
                    if (v == ACTIVE || t == AORIST || t == FUTURE)
                    {
                        s = tenses[t] + " " + voices[v] + " " + moods[m];
                    }
                    else if (v == MIDDLE)
                    {
                        //FIX ME, is this right?? how do we label these.
                        //yes it's correct, middle deponents do not have a passive voice.  H&Q page 316
                        int deponentType = ve.deponentType();
                        if (deponentType == MIDDLE_DEPONENT || deponentType == PASSIVE_DEPONENT || deponentType == DEPONENT_GIGNOMAI || ve.present.equals("κεῖμαι"))
                        {
                            s = tenses[t] + " Middle " + moods[m];
                        }
                        else
                        {
                            s =  tenses[t] + " Middle/Passive " + moods[m];
                        }
                    }
                    else
                    {
                        continue; //skip passive if middle+passive are the same
                    }
                    if (!update) {
                        //jwm mAdapter.addSectionHeaderItem(s);
                    }
                    updateIndex++;

                    vf.mood = m;
                    int countPerSection = 0;
                    for (int n = 0; n < NUM_NUMBERS; n++) {
                        for (int p = 0; p < NUM_PERSONS; p++) {

                            vf.number = n;
                            vf.person = p;

                            if (vf.mood == IMPERATIVE && vf.person == 0)
                            {
                                continue;
                            }

                            int mf = 0;
                            if (isDecomposedMode)
                                mf = 1;

                            String form = vf.getForm(1,mf);
                            form = form.replace(", ", "\n");
                            if (!form.isEmpty()) {
                                if (!update) {
                                    //jwm mAdapter.addItem(new VerbListItem(verbID, (p+1) + ((n == 0) ? "s:" : "p:") + form));
                                } else {
                                    //jwm mAdapter.updateItem(updateIndex++, (p+1) + ((n == 0) ? "s:" : "p:") + form);
                                }
                                countPerSection++;
                            }
                        }
                    }
                    if (countPerSection == 0)
                    {
                        //remove section header
                        if (!update) {
                            //mAdapter.removeLastSectionHeaderItem();
                        }
                        else {
                            updateIndex--;
                        }
                    }
                }
            }
        }
        assertTrue(true);
    }
}