package com.android.inputmethod.latin;

public final class DicTraverseSession {
    static {
        org.futo.inputmethod.latin.utils.JniUtils.loadNativeLibrary();
    }

    private static native long setDicTraverseSessionNative(String locale, long dictSize);
    private static native void initDicTraverseSessionNative(long nativeDicTraverseSession,
            long dictionary, int[] previousWord, int previousWordLength);
    private static native void releaseDicTraverseSessionNative(long nativeDicTraverseSession);
}
