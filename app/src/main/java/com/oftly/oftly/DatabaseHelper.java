package com.oftly.oftly;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "OftlyDB";
	private static final int DATABASE_VERSION = 10;
	private static final String TABLE_PHOTOS_UPLOADED = "uploadedPhotos";
	
	private static final String KEY_ID = "id";
	private static final String KEY_TIME = "time";
	private static final String KEY_PHONE_NUMBER = "phoneNumber";
	
	private static final String CREATE_TABLE_UPLOADED_PHOTOS = 
				"CREATE TABLE " + TABLE_PHOTOS_UPLOADED  +
				"(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIME + " DATETIME, " + 
				KEY_PHONE_NUMBER + " STRING" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.v("VIVEK", CREATE_TABLE_UPLOADED_PHOTOS);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_UPLOADED_PHOTOS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS_UPLOADED);
		Log.v("VIVEK", "Dropping table");
		onCreate(db);
	}
	public long createUploadedPhotoEntry(UploadedPhotoEntry e) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_PHONE_NUMBER, e.getPhoneNumber());
		values.put(KEY_TIME, getDateTime());
		long id = db.insert(TABLE_PHOTOS_UPLOADED, null, values);
		return id;
	}
	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
	}
	public void getAllEntries() {
		String selectQuery = "SELECT * from " + TABLE_PHOTOS_UPLOADED;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		while (c.moveToNext()) {
			String num = c.getString(c.getColumnIndex(KEY_PHONE_NUMBER));
			String time = c.getString(c.getColumnIndex(KEY_TIME));
			Log.v("VIVEK", "Num is " + num + " " + time);
		};
		c.close();
	}
	public boolean phoneNumberExists(String phoneNumber) {
		String query = "SELECT COUNT(*) as cnt from " + TABLE_PHOTOS_UPLOADED + 
				" where " + KEY_PHONE_NUMBER + "=" + "\"" + Util.getCanonicalPhoneNumber(phoneNumber) + "\"";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(query,  null);
		c.moveToNext();
		String num = c.getString(c.getColumnIndex("cnt"));
		Log.v("VIVEK", "does " + phoneNumber + " exist? " + num);
		c.close();
		if (num.equals("0")) {
			return false;
		} else {
			return true;
		}
	}
}
