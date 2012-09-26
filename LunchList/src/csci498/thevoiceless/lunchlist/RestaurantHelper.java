package csci498.thevoiceless.lunchlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "lunchlist.db";
	private static final int SCHEMA_VERSION = 1;
	private static final int NAME_INT = 1;
	private static final int ADDR_INT = 2;
	private static final int TYPE_INT = 3;
	private static final int NOTES_INT = 4;
	
	public RestaurantHelper(Context context)
	{
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE restaurants (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT, type TEXT, notes TEXT);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Not needed yet
	}
	
	public void update(String id, String name, String address, Restaurant.Type type, String notes)
	{
		ContentValues cv = new ContentValues();
		String[] args = {id};
		
		cv.put("name", name);
		cv.put("address", address);
		cv.put("type", type.toString());
		cv.put("notes", notes);
		
		getWritableDatabase().update("restaurants", cv, "_ID=?", args);
	}
	
	public Cursor getById(String id)
	{
		String[] args = {id};
		return getReadableDatabase().rawQuery("SELECT _id, name, address, type, notes FROM restaurants WHERE _ID=?", args);
	}
	
	public void insert(String name, String address, Restaurant.Type type, String notes)
	{
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("address", address);
		cv.put("type", type.toString());
		cv.put("notes", notes);
		getWritableDatabase().insert("restaurants", "name", cv);
	}
	
	public Cursor getAll()
	{
		return getReadableDatabase().rawQuery("SELECT _id, name, address, type, notes FROM restaurants ORDER BY name", null);
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
}
