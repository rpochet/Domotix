// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Address
{
    public static interface IZAddress
    {

        public abstract SocketAddress address();

        public abstract void resolve(String s, boolean flag);

        public abstract String toString();
    }


    private final String address;
    private final String protocol;
    private IZAddress resolved;

    public Address(String s, String s1)
    {
        protocol = s;
        address = s1;
        resolved = null;
    }

    public Address(SocketAddress socketaddress)
    {
        protocol = "tcp";
        InetSocketAddress inetsocketaddress = (InetSocketAddress)socketaddress;
        address = (new StringBuilder()).append(inetsocketaddress.getAddress().getHostAddress()).append(":").append(inetsocketaddress.getPort()).toString();
    }

    public String address()
    {
        return address;
    }

    public String protocol()
    {
        return protocol;
    }

    public IZAddress resolved()
    {
        return resolved;
    }

    public IZAddress resolved(IZAddress izaddress)
    {
        resolved = izaddress;
        return resolved;
    }

    public String toString()
    {
        if (protocol.equals("tcp"))
        {
            if (resolved != null)
            {
                return resolved.toString();
            }
        } else
        if (protocol.equals("ipc") && resolved != null)
        {
            return resolved.toString();
        }
        if (!protocol.isEmpty() && !address.isEmpty())
        {
            return (new StringBuilder()).append(protocol).append("://").append(address).toString();
        } else
        {
            return null;
        }
    }
}
