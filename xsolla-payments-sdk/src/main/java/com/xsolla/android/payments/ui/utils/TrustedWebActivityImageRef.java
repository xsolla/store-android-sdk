package com.xsolla.android.payments.ui.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xsolla.android.payments.R;

/**
 * An image reference used for designating the background
 * image of a {@link TrustedWebActivity}.
 */
public final class TrustedWebActivityImageRef implements Parcelable {
	private static final String TAG = TrustedWebActivityImageRef.class.getSimpleName();

    public interface DrawableIdCallback<T> {
        T onInvoke(@DrawableRes int drawableId);
    }

    public interface FilepathCallback<T> {
        T onInvoke(@NonNull final String filepath);
    }

    public interface EmptyCallback<T> {
        T onInvoke();
    }

    private static abstract class Value {
        public static final class DrawableId extends Value {
            @DrawableRes
            public final int drawableId;

            public DrawableId(@DrawableRes int drawableId) {
                this.drawableId = drawableId;
            }

            @Override
            public void writeToParcel(@NonNull final Parcel dest) {
                dest.writeInt(drawableId);
            }
        }

        public static final class Filepath extends Value {
            @NonNull
            public final String filepath;

            public Filepath(@NonNull final String filepath) {
                this.filepath = filepath;
            }

            @Override
            public void writeToParcel(@NonNull final Parcel dest) {
                dest.writeString(filepath);
            }
        }

        public static final class Empty extends Value {
            @Override
            public void writeToParcel(@NonNull final Parcel dest) {}
        }

        public abstract void writeToParcel(@NonNull final Parcel dest);
    }

    /**
     * An instance holder for the default splash screen image reference.
     * <p/>
     * Lazy construction.
     */
    private static class DefaultInstanceHolder {
        private final static TrustedWebActivityImageRef INSTANCE = forDrawableId(
            R.drawable.xsolla_payments_trusted_web_activity_background
        );
    }

    /**
     * An instance holder for the empty splash screen image reference.
     * <p/>
     * Lazy construction.
     */
    private static class EmptyInstanceHolder {
        private final static TrustedWebActivityImageRef INSTANCE =
            new TrustedWebActivityImageRef(new Value.Empty());
    }

    public static final Creator<TrustedWebActivityImageRef> CREATOR = new Creator<TrustedWebActivityImageRef>() {
        @Nullable
        @Override
        public TrustedWebActivityImageRef createFromParcel(@NonNull final Parcel source) {
            final String valueTypeName = source.readString();
            if (TextUtils.isEmpty(valueTypeName)) {
                return null;
            }

            Value value = null;

            if (valueTypeName.compareToIgnoreCase(Value.DrawableId.class.getSimpleName()) == 0) {
                try {
                    value = new Value.DrawableId(source.readInt());
                } catch (Exception e) {
                    Log.e(TAG, "Failed to read drawable ID from the parcel", e);
                }
            } else if (valueTypeName.compareToIgnoreCase(Value.Filepath.class.getSimpleName()) == 0) {
                try {
                    final String filepath = source.readString();
                    if (filepath != null) {
                        value = new Value.Filepath(filepath);
                    } else {
                        Log.e(TAG, "Failed to read filepath from the parcel");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to read filepath from the parcel", e);
                }
            } else if (valueTypeName.compareToIgnoreCase(Value.Empty.class.getSimpleName()) == 0) {
                value = new Value.Empty();
            }

            return value != null
                ? new TrustedWebActivityImageRef(value)
                : null;
        }

        @Override
        public TrustedWebActivityImageRef[] newArray(int size) {
			return new TrustedWebActivityImageRef[size];
		}
    };

    @NonNull
    private final Value value;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, int flags) {
        dest.writeString(value.getClass().getSimpleName());
        value.writeToParcel(dest);
    }

    public <T> T fold(
        @NonNull final DrawableIdCallback<T> onDrawableId,
        @NonNull final FilepathCallback<T> onFilepath,
        @NonNull final EmptyCallback<T> onEmpty
    ) {
        if (value instanceof Value.DrawableId) {
            return onDrawableId.onInvoke(((Value.DrawableId) value).drawableId);
        } else if (value instanceof Value.Filepath) {
            return onFilepath.onInvoke(((Value.Filepath) value).filepath);
        } {
            assert value instanceof Value.Empty;
            return onEmpty.onInvoke();
        }
    }

    @NonNull
    public static TrustedWebActivityImageRef forDrawableId(@DrawableRes int drawableId) {
        return new TrustedWebActivityImageRef(new Value.DrawableId(drawableId));
    }

    @NonNull
    public static TrustedWebActivityImageRef forFilepath(@NonNull final String filepath) {
        return new TrustedWebActivityImageRef(new Value.Filepath(filepath));
    }

    /**
     * Returns {@link TrustedWebActivityImageRef} pointing to the default
     * splash screen image of {@link TrustedWebActivity}.
     */
    @NonNull
    public static TrustedWebActivityImageRef getDefault() {
        return DefaultInstanceHolder.INSTANCE;
    }

    /**
     * Returns {@link TrustedWebActivityImageRef} that effectively disables
     * the splash screen for {@link TrustedWebActivity}.
     */
    @NonNull
    public static TrustedWebActivityImageRef getEmpty() {
        return EmptyInstanceHolder.INSTANCE;
    }

    private TrustedWebActivityImageRef(@NonNull final Value value) {
        this.value = value;
    }
}
