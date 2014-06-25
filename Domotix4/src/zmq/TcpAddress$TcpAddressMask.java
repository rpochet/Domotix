// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

// Referenced classes of package zmq:
//            TcpAddress

public static class  extends TcpAddress
{

    public boolean match_address(SocketAddress socketaddress)
    {
        return address.equals(socketaddress);
    }

    public ()
    {
    }
}
