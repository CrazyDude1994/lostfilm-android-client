package com.crazydude.lostfilmclient.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.utils.SettingsManager;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static android.support.v17.leanback.widget.GuidedAction.DEFAULT_CHECK_SET_ID;

/**
 * Created by CrazyDude on 5/10/17.
 */

public class SettingsFragment extends GuidedStepFragment {

    private static final int CACHE_ID = 0;
    private static final int NOT_CACHE_ID = 1;
    private static final long DOWNLOAD_FOLDER = 2;
    private static final int FILE_CODE = 0;
    private SettingsManager mSettingsManager;

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance("Настройки", "Настройте приложение под себя", null,
                getResources().getDrawable(R.drawable.ic_settings));
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        super.onCreateActions(actions, savedInstanceState);

        mSettingsManager = new SettingsManager(getActivity().getApplicationContext());

        actions.add(new GuidedAction.Builder(getActivity())
                .title("Кэшировать серии")
                .description("Кэшировать серии во время просмотра онлайн")
                .subActions(Arrays.asList(new GuidedAction.Builder(getActivity())
                        .title("Кэшировать")
                        .checked(true)
                        .id(CACHE_ID)
                        .checkSetId(DEFAULT_CHECK_SET_ID)
                        .build(), new GuidedAction.Builder(getActivity())
                        .checkSetId(DEFAULT_CHECK_SET_ID)
                        .id(NOT_CACHE_ID)
                        .title("Не кэшировать")
                        .build())).build());
        actions.add(new GuidedAction.Builder(getActivity())
                .title("Папка для хранения файлов")
                .description(mSettingsManager.getDownloadFolder())
                .id(DOWNLOAD_FOLDER)
                .build()
        );
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        super.onGuidedActionClicked(action);
        if (action.getId() == DOWNLOAD_FOLDER) {
            Intent i = new Intent(getActivity(), FilePickerActivity.class);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
            i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
            startActivityForResult(i, FILE_CODE);
        }
    }

    @Override
    public boolean onSubGuidedActionClicked(GuidedAction action) {
        if (action.getId() == CACHE_ID) {
            mSettingsManager.setRemoveTorrentAfterStop(false);
            return true;
        } else if (action.getId() == NOT_CACHE_ID) {
            mSettingsManager.setRemoveTorrentAfterStop(true);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            for (Uri uri : files) {
                File file = Utils.getFileForUri(uri);
                mSettingsManager.setDownloadDirectory(file.getAbsolutePath());
                break;
            }
        }
    }
}
