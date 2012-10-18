package csci498.thevoiceless.lunchlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantHelper extends SQLiteOpenHelper
{	
	// Names and indices
	private static final String DATABASE_NAME = "lunchlist.db";
	private static final String TABLE_RESTAURANTS = "restaurants";
	private static final String COL_NAME = "name";
	private static final String COL_ADDR = "address";
	private static final String COL_TYPE = "type";
	private static final String COL_NOTES = "notes";
	private static final String COL_FEED = "feed";
	private static final int NAME_INT = 1;
	private static final int ADDR_INT = 2;
	private static final int TYPE_INT = 3;
	private static final int NOTES_INT = 4;
	private static final int FEED_INT = 5;
	
	private static final int SCHEMA_VERSION = 3;
	// Schema version 2: add "feed" column
	private static final String SCHEMA_UPGRADE_V2_FEED = "ALTER TABLE restaurants ADD COLUMN feed TEXT";
	// Schema version 3: add "lat" and "lon" columns
	private static final String SCHEMA_UPGRADE_V3_LAT = "ALTER TABLE restaurants ADD COLUMN lat REAL";
	private static final String SCHEMA_UPGRADE_V3_LON = "ALTER TABLE restaurants ADD COLUMN lon REAL";
	
	// SQL statements
	private static final String DB_CREATE = "CREATE TABLE restaurants (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT, type TEXT, notes TEXT, feed TEXT, lat REAL, lon REAL);";
	private static final String DB_GET_BY_ID = "SELECT _id, name, address, type, notes, feed FROM restaurants WHERE _ID=?";
	private static final String DB_GET_ALL_ORDER_BY = "SELECT _id, name, address, type, notes, feed FROM restaurants ORDER BY ";
	private static final String ID_MATCH_ARGS = "_ID=?";
	
	public RestaurantHelper(Context context)
	{
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DB_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Switch with no breaks, upgrades will cascade
		switch(oldVersion)
		{
			// Upgrade from v1
			case 1:
			{
				db.execSQL(SCHEMA_UPGRADE_V2_FEED);
			}
			// Upgrade from v2
			case 2:
			{
				db.execSQL(SCHEMA_UPGRADE_V3_LAT);
				db.execSQL(SCHEMA_UPGRADE_V3_LON);
			}
		}
	}
	
	public void update(String id, String name, String address, Restaurant.Type type, String notes, String feed)
	{
		ContentValues cv = new ContentValues();
		String[] args = {id};
		
		cv.put(COL_NAME, name);
		cv.put(COL_ADDR, address);
		cv.put(COL_TYPE, type.toString());
		cv.put(COL_NOTES, notes);
		cv.put(COL_FEED, feed);
		
		getWritableDatabase().update(TABLE_RESTAURANTS, cv, ID_MATCH_ARGS, args);
	}
	
	public Cursor getById(String id)
	{
		String[] args = {id};
		return getReadableDatabase().rawQuery(DB_GET_BY_ID, args);
	}
	
	public void insert(String name, String address, Restaurant.Type type, String notes, String feed)
	{
		ContentValues cv = new ContentValues();
		cv.put(COL_NAME, name);
		cv.put(COL_ADDR, address);
		cv.put(COL_TYPE, type.toString());
		cv.put(COL_NOTES, notes);
		cv.put(COL_FEED, feed);
		getWritableDatabase().insert(TABLE_RESTAURANTS, COL_NAME, cv);
	}
	
	public Cursor getAll(String orderBy)
	{
		return getReadableDatabase().rawQuery(DB_GET_ALL_ORDER_BY + orderBy, null);
	}
	
	public String getName(Cursor c)
	{
		return c.getString(NAME_INT);
	}
	
	public String getAddress(Cursor c)
	{
		return c.getString(ADDR_INT);
	}
	
	public Restaurant.Type getType(Cursor c)
	{
		String theType = c.getString(TYPE_INT);
		if(theType.equals("SIT_DOWN"))
		{
			return Restaurant.Type.SIT_DOWN;
		}
		else if(theType.equals("TAKE_OUT"))
		{
			return Restaurant.Type.TAKE_OUT;
		}
		else
		{
			return Restaurant.Type.DELIVERY;
		}
	}
	
	public String getNotes(Cursor c)
	{
		return c.getString(NOTES_INT);
	}
	
	public String getFeed(Cursor c)
	{
		return c.getString(FEED_INT);
	}
}
