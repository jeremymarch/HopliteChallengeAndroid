#include <time.h>
#include <stdlib.h> // For random(), RAND_MAX
#include <string.h>  //for strlen
#include <stdbool.h> //for bool type
#include "libmorph.h"
#include "GreekUnicode.h"
#include "utilities.h"


extern Verb verbs[];
extern char *tenses[];
extern char *moods[];
extern char *voices[];

//contraction for present and imperfect
void contractEnding2(VerbFormC *vf, UCS2 *buffer, int *len, UCS2 *ending, int endingLen)
{
    if (1)//(utf8HasSuffix(vf->verb->present, "άω") || utf8HasSuffix(vf->verb->present, "άομαι") || utf8HasSuffix(vf->verb->present, "έω") || utf8HasSuffix(vf->verb->present, "έομαι") || utf8HasSuffix(vf->verb->present, "όω") || utf8HasSuffix(vf->verb->present, "όομαι")))
    {
        //1st sing and plural, 3rd pl
        // a/ + w
        if (buffer[*len - endingLen - 1] == GREEK_SMALL_LETTER_ALPHA_WITH_OXIA && ending[0] == GREEK_SMALL_LETTER_OMEGA)
        {
            leftShiftFromOffsetSteps(buffer, *len - endingLen - 1, 1, len);
            buffer[*len - endingLen] = GREEK_SMALL_LETTER_OMEGA_WITH_PERISPOMENI;
        }
        // a/ + ei
        else if (buffer[*len - endingLen - 1] == GREEK_SMALL_LETTER_ALPHA_WITH_OXIA && ending[0] == GREEK_SMALL_LETTER_EPSILON && endingLen > 1 && ending[1] == GREEK_SMALL_LETTER_IOTA)
        {
            leftShiftFromOffsetSteps(buffer, *len - endingLen, 2, len);
            buffer[*len - endingLen+1] = GREEK_SMALL_LETTER_ALPHA_WITH_PERISPOMENI_AND_YPOGEGRAMMENI;
        }
        // a/ + ou
        else if (buffer[*len - endingLen - 1] == GREEK_SMALL_LETTER_ALPHA_WITH_OXIA && ending[0] == GREEK_SMALL_LETTER_OMICRON && endingLen > 1 && ending[1] == GREEK_SMALL_LETTER_UPSILON)
        {
            leftShiftFromOffsetSteps(buffer, *len - endingLen, 2, len);
            buffer[*len - endingLen+1] = GREEK_SMALL_LETTER_OMEGA_WITH_PERISPOMENI;
        }
        // a/ + e
        else if (buffer[*len - endingLen - 1] == GREEK_SMALL_LETTER_ALPHA_WITH_OXIA && ending[0] == GREEK_SMALL_LETTER_EPSILON)
        {
            leftShiftFromOffsetSteps(buffer, *len - endingLen, 1, len);
            buffer[*len - endingLen] = GREEK_SMALL_LETTER_ALPHA_WITH_PERISPOMENI;
        }

    }
}

int main(int argc, char **argv)
{
    char utf8[1024];
    int fileBufferLen = 100;
    char fileBuffer[fileBufferLen];
    VerbFormC vf;

    UCS2 buffer1[1024] = { GREEK_SMALL_LETTER_NU, GREEK_SMALL_LETTER_IOTA, COMBINING_MACRON, GREEK_SMALL_LETTER_KAPPA, GREEK_SMALL_LETTER_ALPHA_WITH_OXIA, GREEK_SMALL_LETTER_OMEGA };
    int len1 = 6;
    UCS2 ending1[20] = { GREEK_SMALL_LETTER_OMEGA };
    int endingLen1 = 1;
    contractEnding2(&vf, buffer1, &len1, ending1, endingLen1);
    ucs2_to_utf8_string(buffer1, len1, utf8);
    printf("len %d, buffer: %s\n", len1, utf8);

    UCS2 buffer2[1024] = { GREEK_SMALL_LETTER_NU, GREEK_SMALL_LETTER_IOTA, COMBINING_MACRON, GREEK_SMALL_LETTER_KAPPA, GREEK_SMALL_LETTER_ALPHA_WITH_OXIA, GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_IOTA, GREEK_SMALL_LETTER_FINAL_SIGMA };
    int len2 = 8;
    UCS2 ending2[20] = { GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_IOTA, GREEK_SMALL_LETTER_FINAL_SIGMA };
    int endingLen2 = 3;

    contractEnding2(&vf, buffer2, &len2, ending2, endingLen2);
    ucs2_to_utf8_string(buffer2, len2, utf8);
    printf("len %d, buffer: %s\n", len2, utf8);

    UCS2 buffer4[1024] = { GREEK_SMALL_LETTER_NU, GREEK_SMALL_LETTER_IOTA, COMBINING_MACRON, GREEK_SMALL_LETTER_KAPPA, GREEK_SMALL_LETTER_ALPHA_WITH_OXIA, GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_IOTA };
    int len4 = 7;
    UCS2 ending4[20] = { GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_IOTA};
    int endingLen4 = 2;

    contractEnding2(&vf, buffer4, &len4, ending4, endingLen4);
    ucs2_to_utf8_string(buffer4, len4, utf8);
    printf("len %d, buffer: %s\n", len4, utf8);

    UCS2 buffer3[1024] = { GREEK_SMALL_LETTER_NU, GREEK_SMALL_LETTER_IOTA, COMBINING_MACRON, GREEK_SMALL_LETTER_KAPPA, GREEK_SMALL_LETTER_ALPHA_WITH_OXIA, GREEK_SMALL_LETTER_OMEGA, GREEK_SMALL_LETTER_MU, GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_NU };
    int len3 = 9;
    UCS2 ending3[20] = { GREEK_SMALL_LETTER_OMEGA, GREEK_SMALL_LETTER_MU, GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_NU };
    int endingLen3 = 4;

    contractEnding2(&vf, buffer3, &len3, ending3, endingLen3);
    ucs2_to_utf8_string(buffer3, len3, utf8);
    printf("len %d, buffer: %s\n", len3, utf8);

    UCS2 buffer5[1024] = { GREEK_SMALL_LETTER_NU, GREEK_SMALL_LETTER_IOTA, COMBINING_MACRON, GREEK_SMALL_LETTER_KAPPA, GREEK_SMALL_LETTER_ALPHA_WITH_OXIA, GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_TAU, GREEK_SMALL_LETTER_EPSILON };
    int len5 = 8;
    UCS2 ending5[20] = { GREEK_SMALL_LETTER_EPSILON, GREEK_SMALL_LETTER_TAU, GREEK_SMALL_LETTER_EPSILON };
    int endingLen5 = 3;

    contractEnding2(&vf, buffer5, &len5, ending5, endingLen5);
    ucs2_to_utf8_string(buffer5, len5, utf8);
    printf("len %d, buffer: %s\n", len5, utf8);

    UCS2 buffer6[1024] = { GREEK_SMALL_LETTER_NU, GREEK_SMALL_LETTER_IOTA, COMBINING_MACRON, GREEK_SMALL_LETTER_KAPPA, GREEK_SMALL_LETTER_ALPHA_WITH_OXIA, GREEK_SMALL_LETTER_OMICRON, GREEK_SMALL_LETTER_UPSILON, GREEK_SMALL_LETTER_SIGMA, GREEK_SMALL_LETTER_IOTA,  };
    int len6 = 9;
    UCS2 ending6[20] = { GREEK_SMALL_LETTER_OMICRON, GREEK_SMALL_LETTER_UPSILON, GREEK_SMALL_LETTER_SIGMA, GREEK_SMALL_LETTER_IOTA };
    int endingLen6 = 4;

    contractEnding2(&vf, buffer6, &len6, ending6, endingLen6);
    ucs2_to_utf8_string(buffer6, len6, utf8);
    printf("len %d, buffer: %s\n", len6, utf8);
}
