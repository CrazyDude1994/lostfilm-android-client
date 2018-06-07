package com.crazydude.common.utils;

import java.io.File;

/**
 * Created by CrazyDude on 6/24/17.
 */

public interface StorageManager {

    boolean hasEnoughSpace(File fileToWrite);

    float getFileLoadProgress(File file);

    boolean isFileFullyDownloaded(File file);
}
