package com.crazydude.common.utils;

import android.support.annotation.NonNull;

import com.crazydude.common.torrent.Torrent;

import java.io.File;

/**
 * Created by CrazyDude on 6/24/17.
 */

public class AndroidStorageManager implements StorageManager {

    private File mRootDirectory;

    public AndroidStorageManager(@NonNull File rootDirectory) {
        mRootDirectory = rootDirectory;
    }

    @Override
    public boolean hasEnoughSpace(@NonNull File fileToWrite) {
        return (fileToWrite.getFreeSpace() - fileToWrite.length()) > 0;
    }

    @Override
    public float getFileLoadProgress(@NonNull File file) {
        return 0;
    }

    @Override
    public boolean isFileFullyDownloaded(@NonNull File file) {
        return false;
    }
}
