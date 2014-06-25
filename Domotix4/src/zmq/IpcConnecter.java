// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            TcpConnecter, IOThread, SessionBase, Options, 
//            Address

public class IpcConnecter extends TcpConnecter
{

    public IpcConnecter(IOThread iothread, SessionBase sessionbase, Options options, Address address, boolean flag)
    {
        super(iothread, sessionbase, options, address, flag);
    }
}
