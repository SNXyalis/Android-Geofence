package gr.hua.dit.assignmentapplicationone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;

public class GeofenceInput {
    private double lat;
    private double lon;
    private String action;
    private Timestamp t;

    private DbHelper helper;

    public GeofenceInput(double lat, double lon, String action, Timestamp t, Context context) {
        this.lat = lat;
        this.lon = lon;
        this.action = action;
        this.t = t;
        helper = new DbHelper(context);
    }

    public GeofenceInput(double lat, double lon, String action, Timestamp t) {
        this.lat = lat;
        this.lon = lon;
        this.action = action;
        this.t = t;
    }

    public GeofenceInput(DbHelper helper) {
        this.helper = helper;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getTimestamp() {
        return t;
    }

    public void setTimestamp(Timestamp t) {
        this.t = t;
    }

    public long persist() {
        ContentValues values = new ContentValues();
        values.put(DbGeofenceInput.FIELD_1, this.lat);
        values.put(DbGeofenceInput.FIELD_2, this.lon);
        values.put(DbGeofenceInput.FIELD_3, this.action);
        values.put(DbGeofenceInput.FIELD_4, String.valueOf(this.t));
        SQLiteDatabase db = helper.getWritableDatabase();
        long result = db.insert(DbGeofenceInput.TABLE_NAME, null, values);
        db.close();
        return result;
    }

    public static ArrayList<GeofenceInput> getGeofenceInputs(Context context) {
        DbHelper dbHelper = new DbHelper(context);

        ArrayList<GeofenceInput> geofenceInputs = new ArrayList<GeofenceInput>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DbGeofenceInput.TABLE_NAME,
                null,
                //new String[]{"ROWID", DbHelper.FIELD_1},
                null,
                null,
                null,
                null,
                null);

        if(cursor.moveToFirst()) {
            do {
                GeofenceInput geofenceInput = new GeofenceInput(cursor.getDouble(0), cursor.getDouble(1), cursor.getString(2), Timestamp.valueOf(cursor.getString(3)));
                geofenceInputs.add(geofenceInput);
            }while (cursor.moveToNext());
        }

        db.close();

        return geofenceInputs;
    }
}
