package lk.lab24.sdm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

public class Database extends SQLiteOpenHelper {
	public static SQLiteDatabase readabelDb;

	public Database(@Nullable Context context) {
		super(context, "database", null, 3);
		Log.d("fuck", "Database: Cons");
		readabelDb = this.getReadableDatabase();
	}

	public static void setCompleted(int id) {
		ContentValues c = new ContentValues();
		c.put("is_downloded", true);
		readabelDb.update("queue", c, "id = ?", new String[]{Integer.toString(id)});
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("fuck", "OnCreatDatabase");
		db.execSQL("CREATE TABLE queue( " +
				"id INTEGER PRIMARY KEY AUTOINCREMENT ," +
				"src VARCHAR(700) NOT NULL," +
				"path VARCHAR (700) NOT NULL," +
				"downloded BIGINT NOT NULL DEFAULT 0," +
				"file_size BIGING NOT NULL DEFAULT 0 ," +
				"file_name VARCHAR(255) NOT NULL," +
				"file_type VARCHAR(200)  NOT NULL DEFAULT 'unicode'," +
				"is_pause BOOLEAN NOT NULL DEFAULT 0," +
				"is_get BOOLEAN NOT NULL DEFAULT 0 ," +
				"is_downloded BOOLEAN NOT NULL DEFAULT 0," +
				"is_error BOOLEAN NOT NULL DEFAULT 0," +
				"error VARCHAR(700) NOT NULL DEFAULT 'cannot downlod file'" +
				")");
		Log.d("fuck", "onCreate: Data BAe TAbel");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + "queue");
		Log.d("fuck", "OnUpgrade");
		// Create tables again
		onCreate(db);
	}

	public int addItem(String src, String path, String filename) {
		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase dbR = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("src", src);
		contentValues.put("path", path);
		contentValues.put("file_name", filename);
		long d = db.insert("queue", null, contentValues);
		return (int) d;

	}

	public void setPauseState(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("is_pause ", true);
		db.update("queue", c, "id = ?", new String[]{Integer.toString(id)});

	}

	public void setResumeState(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("is_pause ", false);
		db.update("queue", c, "id = ?", new String[]{Integer.toString(id)});

	}

	public void setGetState(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("is_get", true);
		db.update("queue", c, "id  =?", new String[]{Integer.toString(id)});
	}

	public void setcancelState(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("queue", "id= ?", new String[]{Integer.toString(id)});

	}

	public void setErrorState(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("is_error", true);
		db.update("queue", c, "id  =?", new String[]{Integer.toString(id)});
	}

	public void setError(int id, String error) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("error", error);
		db.update("queue", c, "id  =?", new String[]{Integer.toString(id)});
	}

	public void removeErrorState(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("is_error", false);
		db.update("queue", c, "id  =?", new String[]{Integer.toString(id)});
	}


	public void setFileSize(int id, long size) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("file_size", size);
		db.update("queue", c, "id=?", new String[]{Integer.toString(id)});
	}

	public void setFileType(int id, String mm) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("file_type", mm);
		db.update("queue", c, "id=?", new String[]{Integer.toString(id)});

	}

	public void setDownloded(int id, long size) {
		Log.d("fuck", "setDownloded: " + size);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("downloded", size);
		db.update("queue", c, "id=?", new String[]{Integer.toString(id)});
	}

	public void setComplete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("is_downloded", true);
		db.update("queue", c, "id  =?", new String[]{Integer.toString(id)});
	}

	public long getDownloded(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT downloded from queue WHERE id = ?", new String[]{Integer.toString(id)});
		c.moveToPosition(0);
		long d = c.getLong(c.getColumnIndex("downloded"));
		Log.d("fuck", "getDownloded: " + d);
		return d;
	}

	public long getFilesize(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT file_size from queue WHERE id = ?", new String[]{Integer.toString(id)});
		c.moveToPosition(0);
		long d = c.getLong(c.getColumnIndex("file_size"));
		Log.d("fuck", "getFilesize: " + d);
		return d;
	}

}

