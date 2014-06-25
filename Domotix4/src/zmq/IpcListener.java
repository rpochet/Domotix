// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.net.InetAddress;
import java.net.InetSocketAddress;

// Referenced classes of package zmq:
//            TcpListener, IpcAddress, IOThread, SocketBase, 
//            Options

public class IpcListener extends TcpListener
{

    private final IpcAddress address = new IpcAddress();

    public IpcListener(IOThread iothread, SocketBase socketbase, Options options)
    {
        super(iothread, socketbase, options);
    }

    public String get_address()
    {
        return address.toString();
    }

    public int set_address(String s)
    {
        address.resolve(s, false);
        InetSocketAddress inetsocketaddress = (InetSocketAddress)address.address();
        return super.set_address((new StringBuilder()).append(inetsocketaddress.getAddress().getHostAddress()).append(":").append(inetsocketaddress.getPort()).toString());
    }
}
