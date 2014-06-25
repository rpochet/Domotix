// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

// Referenced classes of package eu.pochet.domotix.service:
//            DownloadableFile

public class DownloadFilesTask extends AsyncTask
{

    private static final String TAG = eu/pochet/domotix/service/DownloadFilesTask.getName();
    private Context ctx;

    public DownloadFilesTask(Context context)
    {
        ctx = null;
        ctx = context;
    }

    protected transient Long doInBackground(DownloadableFile adownloadablefile[])
    {
        byte abyte0[];
        AndroidHttpClient androidhttpclient;
        FileOutputStream fileoutputstream;
        Long long1;
        int i;
        int j;
        abyte0 = new byte[1024];
        androidhttpclient = null;
        fileoutputstream = null;
        long1 = Long.valueOf(0L);
        i = adownloadablefile.length;
        j = 0;
_L3:
        DownloadableFile downloadablefile;
        if (j >= i)
        {
            return long1;
        }
        downloadablefile = adownloadablefile[j];
        InputStream inputstream;
        androidhttpclient = AndroidHttpClient.newInstance("Domotix");
        BasicHttpParams basichttpparams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basichttpparams, 4000);
        HttpConnectionParams.setSoTimeout(basichttpparams, 4000);
        inputstream = androidhttpclient.execute(new HttpGet(downloadablefile.uri)).getEntity().getContent();
        Log.d(TAG, (new StringBuilder("Getting file from ")).append(downloadablefile.uri).append(" to ").append(downloadablefile.file).toString());
        fileoutputstream = ctx.openFileOutput(downloadablefile.file, 0);
_L1:
        int k = inputstream.read(abyte0);
label0:
        {
            {
                if (k > 0)
                {
                    break label0;
                }
                if (androidhttpclient != null)
                {
                    androidhttpclient.close();
                }
                Exception exception;
                IOException ioexception;
                Exception exception1;
                IOException ioexception1;
                Long long2;
                if (fileoutputstream != null)
                {
                    try
                    {
                        fileoutputstream.close();
                    }
                    catch (IOException ioexception2)
                    {
                        ioexception2.printStackTrace();
                    }
                }
            }
            j++;
            continue; /* Loop/switch isn't completed */
        }
        fileoutputstream.write(abyte0, 0, k);
        long2 = Long.valueOf(long1.longValue() + (long)k);
        long1 = long2;
          goto _L1
        exception1;
        exception1.printStackTrace();
        if (androidhttpclient != null)
        {
            androidhttpclient.close();
        }
        if (fileoutputstream != null)
        {
            try
            {
                fileoutputstream.close();
            }
            // Misplaced declaration of an exception variable
            catch (IOException ioexception1)
            {
                ioexception1.printStackTrace();
            }
        }
        break MISSING_BLOCK_LABEL_185;
        exception;
        if (androidhttpclient != null)
        {
            androidhttpclient.close();
        }
        if (fileoutputstream != null)
        {
            try
            {
                fileoutputstream.close();
            }
            // Misplaced declaration of an exception variable
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        }
        throw exception;
        if (true) goto _L3; else goto _L2
_L2:
    }

    protected volatile transient Object doInBackground(Object aobj[])
    {
        return doInBackground((DownloadableFile[])aobj);
    }

    protected void onPostExecute(Long long1)
    {
        Log.i(TAG, "DownloadFilesTask done");
    }

    protected volatile void onPostExecute(Object obj)
    {
        onPostExecute((Long)obj);
    }

    protected transient void onProgressUpdate(Integer ainteger[])
    {
    }

    protected volatile transient void onProgressUpdate(Object aobj[])
    {
        onProgressUpdate((Integer[])aobj);
    }

}
