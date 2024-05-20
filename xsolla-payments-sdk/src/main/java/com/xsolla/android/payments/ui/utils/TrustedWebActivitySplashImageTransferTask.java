package com.xsolla.android.payments.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.browser.customtabs.TrustedWebUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

final class TrustedWebActivitySplashImageTransferTask {
    private static final String TAG = "SplashImageTransferTask";
    private static final String FOLDER_NAME = "twa_splash";
    private static final String FILE_NAME = "splash_image.png";
    private static final String PREFS_FILE = "splashImagePrefs";
    private static final String PREF_LAST_UPDATE_TIME = "lastUpdateTime";

    @NonNull
    private final Context mContext;

    @NonNull
    private final Bitmap mBitmap;

    @NonNull
    private final String mAuthority;

    @NonNull
    private final CustomTabsSession mSession;

    @NonNull
    private final String mProviderPackage;

    @Nullable
    private Callback mCallback;

    @SuppressLint("StaticFieldLeak")
    private final AsyncTask<Void, Void, Boolean> mAsyncTask = new AsyncTask<Void, Void, Boolean>() {
        protected Boolean doInBackground(Void... args) {
            if (isCancelled()) {
                return false;
            } else {
                final File dir = new File(mContext.getFilesDir(), FOLDER_NAME);
                if (!dir.exists()) {
                    final boolean mkDirSuccessful = dir.mkdir();
                    if (!mkDirSuccessful) {
                        Log.w(TAG, "Failed to create a directory for storing a splash image");
                        return false;
                    }
                }

                final File file = new File(dir, FILE_NAME);

                Log.e(TAG, Arrays.toString(dir.listFiles()));
                final SharedPreferences prefs = mContext.getSharedPreferences(PREFS_FILE, 0);
                final long lastUpdateTime = getLastAppUpdateTime();

                if (file.exists() && lastUpdateTime == prefs.getLong(PREF_LAST_UPDATE_TIME, 0L)) {
                    return transferToCustomTabsProvider(file);
                } else {
                    try {
                        //noinspection IOStreamConstructor
                        final OutputStream os = new FileOutputStream(file);

                        boolean success = false;

                        try {
                            if (!isCancelled()) {
                                mBitmap.compress(CompressFormat.PNG, 100, os);

                                os.flush();

                                prefs.edit()
                                    .putLong(PREF_LAST_UPDATE_TIME, lastUpdateTime)
                                    .apply();

                                if (!isCancelled()) {
                                    success = transferToCustomTabsProvider(file);
                                }
                            }
                        } catch (Exception e) {
                            try {
                                os.close();
                            } catch (Exception e1) {
                                e.addSuppressed(e1);
                            }
                            throw e;
                        }

                        os.close();

                        return success;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        private boolean transferToCustomTabsProvider(@NonNull final File f) {
            return TrustedWebUtils.transferSplashImage(
                mContext, f, mAuthority, mProviderPackage, mSession
            );
        }

        private long getLastAppUpdateTime() {
            try {
                final PackageInfo packageInfo = mContext
                    .getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
                return packageInfo.lastUpdateTime;
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (mCallback != null && !isCancelled()) {
                mCallback.onFinished(success);
            }
        }
    };

    public TrustedWebActivitySplashImageTransferTask(
        @NonNull final Context context, @NonNull final Bitmap bitmap, @NonNull final String authority,
        @NonNull final CustomTabsSession session, @NonNull final String providerPackage
    ) {
        mContext = context.getApplicationContext();
        mBitmap = bitmap;
        mAuthority = authority;
        mSession = session;
        mProviderPackage = providerPackage;
    }

    public void execute(@NonNull final Callback callback) {
        assert mAsyncTask.getStatus() == Status.PENDING;

        mCallback = callback;
        mAsyncTask.execute();
    }

    public void cancel() {
        mAsyncTask.cancel(true);
        mCallback = null;
    }

    public interface Callback {
        void onFinished(boolean success);
    }
}
