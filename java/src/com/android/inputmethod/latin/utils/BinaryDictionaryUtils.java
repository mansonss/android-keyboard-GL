package com.android.inputmethod.latin.utils;

public final class BinaryDictionaryUtils {
    static {
        org.futo.inputmethod.latin.utils.JniUtils.loadNativeLibrary();
    }

    private static native boolean createEmptyDictFileNative(String filePath, long dictVersion,
            String locale, String[] attributeKeyStringArray, String[] attributeValueStringArray);
    private static native float calcNormalizedScoreNative(int[] before, int[] after, int score);
    private static native int setCurrentTimeForTestNative(int currentTime);
}
