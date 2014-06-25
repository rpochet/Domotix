// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Pipe

public static interface 
{

    public abstract void hiccuped(Pipe pipe);

    public abstract void read_activated(Pipe pipe);

    public abstract void terminated(Pipe pipe);

    public abstract void write_activated(Pipe pipe);
}
