package com.fourdudes.qshare.drive;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.core.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.Triple;


public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive driveService;
    private static final String LOG_TAG = "4dudes.DriveServHelper";

    public DriveServiceHelper(Drive driveService) {
        this.driveService = driveService;
    }

    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     * This reads from Google Drive
     */
    public Task<Pair<String, String>> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the metadata as a File object.
            File metadata = driveService.files().get(fileId).execute();
            String name = metadata.getName();

            // Stream the file contents to a String.
            try (InputStream is = driveService.files().get(fileId).executeMediaAsInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String contents = stringBuilder.toString();

                return Pair.create(name, contents);
            }
        });
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        return intent;
    }


    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Pair<Pair<String, String>, java.io.File>> openFileUsingStorageAccessFramework(
            ContentResolver contentResolver, Uri uri, Context context) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the document's display name from its metadata.
            String mimeType = contentResolver.getType(uri);
            String name;
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    name = cursor.getString(nameIndex);
                } else {
                    throw new IOException("Empty cursor returned for file.");
                }
            }

//            // Read the document's contents as a String.
//            String content;
//            try (InputStream is = contentResolver.openInputStream(uri);
//                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//                StringBuilder stringBuilder = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    stringBuilder.append(line);
//                }
//                content = stringBuilder.toString();
//            }

            java.io.File file = new java.io.File(context.getCacheDir(), "tempFileForDriveUpload.srl");

            try (OutputStream output = new FileOutputStream(file)) {
                InputStream is = contentResolver.openInputStream(uri);
                byte[] buffer = new byte[4 * 1024];
                int read;

                assert is != null;
                while((read = is.read(buffer)) != -1){
                    output.write(buffer, 0, read);
                }

                output.flush();
                is.close();
            }
            return Pair.create(Pair.create(name, mimeType), file);
        });
    }

    public Task<Boolean> uploadFileToDrive(Context context, Pair<Pair<String, String>, java.io.File> fileInfo, Uri uri){
        return Tasks.call(mExecutor, () -> {
            //create metadata
            File fileMetadata = new File();
            fileMetadata.setName(fileInfo.first.first);

//            String filename = "tempfile";
//            java.io.File filePath = new java.io.File(context.getFilesDir(), "tempfile");
//            try(FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
//                assert fileInfo.second != null;
//                fos.write(fileInfo.second.getBytes());
//            }
            FileContent mediaContent = new FileContent(fileInfo.first.second, fileInfo.second);
            try{
                File file = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
                Log.d(LOG_TAG, "successfully uploaded file with id " + file.getId());
                return true;
            }
            catch (java.io.IOException exception){
                Log.e(LOG_TAG, "Could not upload file to drive," + exception);
            }
            return false;
        });
    }

}
