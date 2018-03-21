package com.deemsysinc.neetar11biophase2.expansion;
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

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class APKExpansionSupport {
    static String[] getAPKExpansionFiles(final Context ctx, int mainVersion, int patchVersion, String obbContentPath) {
        String packageName = ctx.getPackageName();
        Vector<String> ret = new Vector<String>();
        if (mainVersion > 0) {
            String strMainPath = obbContentPath + File.separator + "main." + mainVersion + "." + packageName + ".obb";
            Log.d("Path", strMainPath);
            File main = new File(strMainPath);
            if (main.isFile()) {
                ret.add(strMainPath);
            }
        }
        if (patchVersion > 0) {
            String strPatchPath = obbContentPath + File.separator + "patch." + mainVersion + "." + packageName + ".obb";
            File main = new File(strPatchPath);
            Log.d("Path", strPatchPath);
            if (main.isFile()) {
                ret.add(strPatchPath);
            }
        }
        String[] retArray = new String[ret.size()];
        ret.toArray(retArray);
        return retArray;
    }

    static public ZipResourceFile getResourceZipFile(String[] expansionFiles) throws IOException {
        ZipResourceFile apkExpansionFile = null;
        for (String expansionFilePath : expansionFiles) {
            Log.d("ZipFile", expansionFilePath);
            if (null == apkExpansionFile) {
                apkExpansionFile = new ZipResourceFile(expansionFilePath);
            } else {
                apkExpansionFile.addPatchFile(expansionFilePath);
            }
        }
        return apkExpansionFile;
    }

    static public ZipResourceFile getAPKExpansionZipFile(Context ctx, int mainVersion, int patchVersion, String obbContentPath) throws IOException {
        String[] expansionFiles = getAPKExpansionFiles(ctx, mainVersion, patchVersion, obbContentPath);
        return getResourceZipFile(expansionFiles);
    }
}
