// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;


// Referenced classes of package org.jeromq:
//            ZLoop

private class errors
{

    Object arg;
    int errors;
    ndler handler;
    ndler item;
    final ZLoop this$0;

    protected ndler(ndler ndler, ndler ndler1, Object obj)
    {
        this$0 = ZLoop.this;
        super();
        item = ndler;
        handler = ndler1;
        arg = obj;
        errors = 0;
    }
}
