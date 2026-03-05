package com.android.inputmethod.latin.utils;

/**
 * Stub class at the AOSP package path so libjni_latinimegoogle.so can register
 * its native methods via JNI_OnLoad. Actual implementation is in
 * org.futo.inputmethod.latin.utils.WordInputEventForPersonalization.
 */
public final class WordInputEventForPersonalization {
    public final int[] mTargetWord;
    public final int mPrevWordsCount;
    public final int[][] mPrevWordArray;
    public final boolean[] mIsPrevWordBeginningOfSentenceArray;
    public final int mTimestamp;

    // Fields must exist for JNI field lookup by the Google lib
    public WordInputEventForPersonalization() {
        mTargetWord = new int[0];
        mPrevWordsCount = 0;
        mPrevWordArray = new int[3][];
        mIsPrevWordBeginningOfSentenceArray = new boolean[3];
        mTimestamp = 0;
    }
}
