/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.futo.inputmethod.latin.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import org.futo.inputmethod.latin.define.JniLibName;

import java.io.File;

public final class JniUtils {
    private static final String TAG = JniUtils.class.getSimpleName();

    public static final String JNI_LIB_NAME_GOOGLE = "jni_latinimegoogle";
    public static final String JNI_LIB_IMPORT_FILE_NAME = "libjni_latinime.so";
    public static final String PREF_LIBRARY_CHECKSUM = "pref_gesture_library_checksum";

    public static boolean sHaveGestureLib = false;

    static {
        // 1. Try user-supplied library (extracted from Gboard)
        @SuppressLint("SdCardPath")
        String filesDir = "/data/data/org.futo.inputmethod.latin/files";
        // Best effort to get real path — if this fails we fall back to hardcoded
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            android.app.Application app = (android.app.Application)
                    activityThread.getMethod("currentApplication").invoke(null, (Object[]) null);
            if (app != null && app.getFilesDir() != null) {
                filesDir = app.getFilesDir().getAbsolutePath();
            }
        } catch (Exception ignored) { }

        File userLib = new File(filesDir + File.separator + JNI_LIB_IMPORT_FILE_NAME);
        if (userLib.isFile()) {
            try {
                System.load(userLib.getAbsolutePath());
                sHaveGestureLib = true;
                Log.i(TAG, "Loaded user-supplied gesture library");
            } catch (Throwable t) {
                Log.w(TAG, "Failed to load user-supplied gesture library", t);
            }
        }

        // 2. Try system Google gesture library (works if this is a system app or
        //    the device ships libjni_latinimegoogle.so)
        if (!sHaveGestureLib) {
            try {
                System.loadLibrary(JNI_LIB_NAME_GOOGLE);
                sHaveGestureLib = true;
                Log.i(TAG, "Loaded system Google gesture library");
            } catch (UnsatisfiedLinkError ul) {
                Log.w(TAG, "Could not load system gesture library: " + ul.getMessage());
            }
        }

        // 3. Fall back to FUTO's built-in swipe library (always present)
        try {
            System.loadLibrary(JniLibName.JNI_LIB_NAME);
        } catch (UnsatisfiedLinkError ule) {
            Log.e(TAG, "Could not load native library " + JniLibName.JNI_LIB_NAME, ule);
        }
    }

    private JniUtils() {
        // This utility class is not publicly instantiable.
    }

    public static void loadNativeLibrary() {
        // Ensures the static initializer is called
    }
}
