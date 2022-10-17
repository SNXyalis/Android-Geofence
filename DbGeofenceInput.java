package gr.hua.dit.assignmentapplicationtwo;

public class DbGeofenceInput {
    public static String DB_NAME = "GEOFENCEINPUT_DB";
    public static int DB_VERSION = 1;
    public static String TABLE_NAME = "GEOFENCEINPUT";
    public static String FIELD_1 = "LAT";
    public static String FIELD_2 = "LON";
    public static String FIELD_3 = "ACTION";
    public static String FIELD_4 = "T";

    public static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ('"+FIELD_1+"' REAL, '"+FIELD_2+"' REAL, '"+FIELD_3+"' TEXT, '"+FIELD_4 +"' TEXT);";

    public static String AUTHORITY = "gr.hua.dit.assignmentapplicationone.AUTHORITY";
    public static String PATH = DbGeofenceInput.TABLE_NAME;

}
