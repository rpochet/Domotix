// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;

import android.content.Context;
import eu.pochet.domotix.service.DownloadFilesTask;

// Referenced classes of package eu.pochet.domotix.dao:
//            ScenarioDao

class lesTask extends DownloadFilesTask
{

    private final Runnable val$postExecute;

    protected void onPostExecute(Long long1)
    {
        super.onPostExecute(long1);
        ScenarioDao.reset();
        val$postExecute.run();
    }

    lesTask(Runnable runnable)
    {
        val$postExecute = runnable;
        super(final_context);
    }
}
