// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;


// Referenced classes of package eu.pochet.domotix.service:
//            LagartoEventService

class this._cls0
    implements Runnable
{

    final LagartoEventService this$0;

    public void run()
    {
        LagartoEventService.access$0(LagartoEventService.this, "{\"lagarto\":{\"procname\": \"SWAP network\",\"httpserver\": \"192.168.1.34:8001\",\"status\":[{\"id\": \"10.12.0\",\"location\": \"garden\",\"name\": \"relay1\",\"value\": \"ON\",\"type\": \"bin\",\"direction\": \"out\",\"timestamp\": \"19 Feb 2012 16:15:17\"},{\"id\": \"10.12.1\",\"location\": \"garden\",\"name\": \"relay2\",\"value\": \"OFF\",\"type\": \"bin\",\"direction\": \"out\",\"timestamp\": \"19 Feb 2012 16:15:17\"}]}}");
    }

    ()
    {
        this$0 = LagartoEventService.this;
        super();
    }
}
