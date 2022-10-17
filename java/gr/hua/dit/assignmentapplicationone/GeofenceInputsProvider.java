package gr.hua.dit.assignmentapplicationone;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GeofenceInputsProvider extends ContentProvider {

    private UriMatcher uriMatcher;
    private DbHelper helper;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DbGeofenceInput.AUTHORITY, DbGeofenceInput.PATH, 1);
        uriMatcher.addURI(DbGeofenceInput.AUTHORITY, DbGeofenceInput.PATH+"/#", 2);
        helper = new DbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case 1:
                cursor = database.query(DbGeofenceInput.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            case 2:
                String id = uri.getLastPathSegment();
                cursor = database.query(DbGeofenceInput.TABLE_NAME,
                        null,
                        "ROWID=?",
                        new String[]{id},
                        null,
                        null,
                        null);

                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
