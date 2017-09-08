package uk.ac.ox.oerc.glam.touchtracker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Class to write data to file asynchronously
 */

public class FileConnection extends AsyncTask<String,String,String> {

    private Context mContext;

    protected String doInBackground(String... params) {
        this.writeFile(params[0] + "\n");
        return null;
    }

    public FileConnection () {
        mContext = TouchTrackApplication.getAppContext();
        Log.d("Context", mContext.toString());
    }

    public void writeFile (String params) {
        FileOutputStream outputStream;
        String filename = "multi_touch.txt";
        try {
            File file = new File(mContext.getExternalFilesDir(null), filename);
            // test if file exists.
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file, true);
            outputStream.write(params.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
