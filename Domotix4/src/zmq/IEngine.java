// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            IOThread, SessionBase

public interface IEngine
{

    public abstract void activate_in();

    public abstract void activate_out();

    public abstract void plug(IOThread iothread, SessionBase sessionbase);

    public abstract void terminate();
}
