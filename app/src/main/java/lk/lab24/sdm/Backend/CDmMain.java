package lk.lab24.sdm.Backend;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import lk.lab24.sdm.Database;

public class CDmMain {
    public CDmMain(Database d, Context context) {
        SQLiteDatabase database = d.getReadableDatabase();
        CustomDownlodManager customDownlodManager = new CustomDownlodManager(d, context);
        String paths = Environment.getExternalStorageDirectory().getAbsolutePath();
        File a = new File(paths, "folder");
        a.mkdir();
        Log.d("fuck", "CDmMain: path = " + paths);
        String srrc = "http://www.ict24.tk/static/images/ict24.png";


//        Cursor c = database.rawQuery("SELECT * FROM queue ", null);
//        while (c.moveToNext()) {
//            int id = c.getInt(c.getColumnIndex("id"));
//            String src = c.getString(c.getColumnIndex("src"));
//            String path = c.getString(c.getColumnIndex("path"));
//            String file_name = c.getString(c.getColumnIndex("file_name"));
//            boolean is_pause = c.getInt(c.getColumnIndex("is_pause")) == 1;
//            boolean is_cancel = c.getInt(c.getColumnIndex("is_cancel")) == 1;
//            Log.d("fuck", src + "id = " + id);
//        }
    }

    public void addNewDownlod(int id) {

    }
}
