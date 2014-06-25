// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class TcpAddress
    implements Address.IZAddress
{
    public static class TcpAddressMask extends TcpAddress
    {

        public boolean match_address(SocketAddress socketaddress)
        {
            return address.equals(socketaddress);
        }

        public TcpAddressMask()
        {
        }
    }


    protected InetSocketAddress address;

    public TcpAddress()
    {
    }

    public TcpAddress(String s)
    {
        resolve(s, false);
    }

    public SocketAddress address()
    {
        return address;
    }

    public void resolve(String s, boolean flag)
    {
        int j;
        InetAddress inetaddress;
        int i = s.lastIndexOf(':');
        if (i < 0)
        {
            throw new IllegalArgumentException(s);
        }
        String s1 = s.substring(0, i);
        String s2 = s.substring(i + 1);
        if (s1.length() >= 2 && s1.charAt(0) == '[' && s1.charAt(-1 + s1.length()) == ']')
        {
            s1 = s1.substring(1, -1 + s1.length());
        }
        InetAddress ainetaddress[];
        int k;
        int l;
        InetAddress inetaddress1;
        boolean flag1;
        if (s2.equals("*") || s2.equals("0"))
        {
            j = 0;
        } else
        {
            j = Integer.parseInt(s2);
            if (j == 0)
            {
                throw new IllegalArgumentException(s);
            }
        }
        if (s1.equals("*"))
        {
            s1 = "0.0.0.0";
        }
        try
        {
            ainetaddress = InetAddress.getAllByName(s1);
            k = ainetaddress.length;
        }
        catch (UnknownHostException unknownhostexception)
        {
            throw new IllegalArgumentException(unknownhostexception);
        }
        l = 0;
        inetaddress = null;
        if (l >= k)
        {
            break MISSING_BLOCK_LABEL_205;
        }
        inetaddress1 = ainetaddress[l];
        if (!flag)
        {
            break; /* Loop/switch isn't completed */
        }
        flag1 = inetaddress1 instanceof Inet6Address;
        if (!flag1)
        {
            break; /* Loop/switch isn't completed */
        }
        l++;
        if (true) goto _L2; else goto _L1
_L2:
        break MISSING_BLOCK_LABEL_141;
_L1:
        inetaddress = inetaddress1;
        if (inetaddress == null)
        {
            throw new IllegalArgumentException(s);
        } else
        {
            address = new InetSocketAddress(inetaddress, j);
            return;
        }
    }

    public String toString()
    {
        if (address == null)
        {
            return null;
        }
        if (address.getAddress() instanceof Inet6Address)
        {
            return (new StringBuilder()).append("tcp://[").append(address.getAddress().getHostAddress()).append("]:").append(address.getPort()).toString();
        } else
        {
            return (new StringBuilder()).append("tcp://").append(address.getAddress().getHostAddress()).append(":").append(address.getPort()).toString();
        }
    }
}
