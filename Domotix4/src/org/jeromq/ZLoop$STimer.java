// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;


// Referenced classes of package org.jeromq:
//            ZLoop

private class when
{

    Object arg;
    int delay;
    andler handler;
    final ZLoop this$0;
    int times;
    long when;

    public andler(int i, int j, andler andler, Object obj)
    {
        this$0 = ZLoop.this;
        super();
        delay = i;
        times = j;
        handler = andler;
        arg = obj;
        when = -1L;
    }
}
