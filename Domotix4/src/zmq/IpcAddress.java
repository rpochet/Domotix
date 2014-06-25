// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class IpcAddress
    implements Address.IZAddress
{

    private InetSocketAddress address;
    private String name;

    public IpcAddress()
    {
    }

    public SocketAddress address()
    {
        return address;
    }

    public void resolve(String s, boolean flag)
    {
        name = s;
        int i = s.hashCode();
        if (i < 0)
        {
            i = -i;
        }
        int j = 10000 + i % 55536;
        try
        {
            address = new InetSocketAddress(InetAddress.getByName(null), j);
            return;
        }
        catch (UnknownHostException unknownhostexception)
        {
            throw new IllegalArgumentException(unknownhostexception);
        }
    }

    public String toString()
    {
        if (name == null)
        {
            return null;
        } else
        {
            return (new StringBuilder()).append("ipc://").append(name).toString();
        }
    }
}
