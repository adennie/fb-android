package com.fizzbuzz.android.persist;

import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_READ;
import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_WRITE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.EnumSet;
import java.util.UUID;

import android.content.Context;

import com.fizzbuzz.android.util.VersionedStrictModeWrapper;

public class Installation {
    private static String sGuid = null;
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String getGuid(final Context context) {
        if (sGuid == null) {
            VersionedStrictModeWrapper.runWithStrictModeOverride(
                    EnumSet.of(ALLOW_DISK_READ, ALLOW_DISK_WRITE),
                    new Runnable() {
                        @Override
                        public void run() {
                            File installFile = new File(context.getFilesDir(), INSTALLATION);
                            try {
                                if (!installFile.exists())
                                    writeInstallationFile(installFile);
                                sGuid = readInstallationFile(installFile);
                            }
                            catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        }
        return sGuid;
    }

    private static String readInstallationFile(File installFile) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installFile, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installFile) throws IOException {
        FileOutputStream out = new FileOutputStream(installFile);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}
