package eu.pochet.domotix.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadFilesTask extends AsyncTask<DownloadableFile, Integer, Long>
{
	private static final String TAG = DownloadFilesTask.class.getName();
	
	private Context ctx = null;
	
	public DownloadFilesTask(Context ctx)
	{
		this.ctx = ctx;
	}
	
    protected Long doInBackground(DownloadableFile... downloadableFiles) 
    {
    	// create a buffer...
		byte[] buffer = new byte[1024];
		int bufferLength = 0; // used to store a temporary size of the buffer
		
		AndroidHttpClient client = null;
    	FileOutputStream fileOutput = null;
    	InputStream inputStream = null;
    	Long res = 0L;
    	DownloadableFile downloadableFile = null;
        int count = downloadableFiles.length;
        for (int i = 0; i < count; i++) 
        {
        	downloadableFile = downloadableFiles[i];
        	try 
        	{
				client = AndroidHttpClient.newInstance("Domotix");

		        HttpParams params = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(params, 4000);
		        HttpConnectionParams.setSoTimeout(params, 4000);
		        
				HttpGet request = new HttpGet(downloadableFile.uri);
				HttpResponse response = client.execute(request);
				inputStream = response.getEntity().getContent();
				
				Log.d(TAG, "Getting file from " + downloadableFile.uri + " to " + downloadableFile.file);
				
				fileOutput = this.ctx.openFileOutput(downloadableFile.file, Context.MODE_PRIVATE);
				
				// now, read through the input buffer and write the contents to the file
				while ((bufferLength = inputStream.read(buffer)) > 0)
				{
					// add the data in the buffer to the file in the file output stream (the file on the sd card
					fileOutput.write(buffer, 0, bufferLength);
					res += bufferLength;
				}
				
				bufferLength = 0;
			} 
        	catch(Exception e) 
        	{
				e.printStackTrace();
			} 
        	finally 
        	{
				if(client != null) 
				{
					client.close();
				}
				if(fileOutput != null) 
				{
					try {fileOutput.close();} catch (IOException e) {e.printStackTrace();}
				}
			}
        }
        return res;
    }

    protected void onProgressUpdate(Integer... progress)
    {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
    	Log.i(TAG, "DownloadFilesTask done");
    }
    
}