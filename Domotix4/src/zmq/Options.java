// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Referenced classes of package zmq:
//            EncoderBase, DecoderBase

public class Options
{

    long affinity;
    int backlog;
    Class decoder;
    int delay_attach_on_connect;
    boolean delay_on_close;
    boolean delay_on_disconnect;
    Class encoder;
    boolean filter;
    byte identity[];
    byte identity_size;
    int ipv4only;
    String last_endpoint;
    int linger;
    long maxmsgsize;
    int multicast_hops;
    int rate;
    int rcvbuf;
    int rcvhwm;
    int rcvtimeo;
    int reconnect_ivl;
    int reconnect_ivl_max;
    int recovery_ivl;
    boolean recv_identity;
    int sndbuf;
    int sndhwm;
    int sndtimeo;
    int socket_id;
    final List tcp_accept_filters = new ArrayList();
    int tcp_keepalive;
    int tcp_keepalive_cnt;
    int tcp_keepalive_idle;
    int tcp_keepalive_intvl;
    int type;

    public Options()
    {
        sndhwm = 1000;
        rcvhwm = 1000;
        affinity = 0L;
        identity_size = 0;
        rate = 100;
        recovery_ivl = 10000;
        multicast_hops = 1;
        sndbuf = 0;
        rcvbuf = 0;
        type = -1;
        linger = -1;
        reconnect_ivl = 100;
        reconnect_ivl_max = 0;
        backlog = 100;
        maxmsgsize = -1L;
        rcvtimeo = -1;
        sndtimeo = -1;
        ipv4only = 1;
        delay_attach_on_connect = 0;
        delay_on_close = true;
        delay_on_disconnect = true;
        filter = false;
        recv_identity = false;
        tcp_keepalive = -1;
        tcp_keepalive_cnt = -1;
        tcp_keepalive_idle = -1;
        tcp_keepalive_intvl = -1;
        socket_id = 0;
        identity = null;
        decoder = null;
        encoder = null;
    }

    public Object getsockopt(int i)
    {
        switch (i)
        {
        case 6: // '\006'
        case 7: // '\007'
        case 10: // '\n'
        case 13: // '\r'
        case 14: // '\016'
        case 15: // '\017'
        case 20: // '\024'
        case 26: // '\032'
        case 29: // '\035'
        case 30: // '\036'
        case 33: // '!'
        case 38: // '&'
        default:
            throw new IllegalArgumentException((new StringBuilder()).append("option=").append(i).toString());

        case 23: // '\027'
            return Integer.valueOf(sndhwm);

        case 24: // '\030'
            return Integer.valueOf(rcvhwm);

        case 4: // '\004'
            return Long.valueOf(affinity);

        case 5: // '\005'
            return identity;

        case 8: // '\b'
            return Integer.valueOf(rate);

        case 9: // '\t'
            return Integer.valueOf(recovery_ivl);

        case 11: // '\013'
            return Integer.valueOf(sndbuf);

        case 12: // '\f'
            return Integer.valueOf(rcvbuf);

        case 16: // '\020'
            return Integer.valueOf(type);

        case 17: // '\021'
            return Integer.valueOf(linger);

        case 18: // '\022'
            return Integer.valueOf(reconnect_ivl);

        case 21: // '\025'
            return Integer.valueOf(reconnect_ivl_max);

        case 19: // '\023'
            return Integer.valueOf(backlog);

        case 22: // '\026'
            return Long.valueOf(maxmsgsize);

        case 25: // '\031'
            return Integer.valueOf(multicast_hops);

        case 27: // '\033'
            return Integer.valueOf(rcvtimeo);

        case 28: // '\034'
            return Integer.valueOf(sndtimeo);

        case 31: // '\037'
            return Integer.valueOf(ipv4only);

        case 34: // '"'
            return Integer.valueOf(tcp_keepalive);

        case 39: // '\''
            return Integer.valueOf(delay_attach_on_connect);

        case 35: // '#'
        case 36: // '$'
        case 37: // '%'
            return Integer.valueOf(0);

        case 32: // ' '
            return last_endpoint;
        }
    }

    public void setsockopt(int i, Object obj)
    {
        i;
        JVM INSTR lookupswitch 25: default 212
    //                   4: 329
    //                   5: 341
    //                   8: 467
    //                   9: 479
    //                   11: 491
    //                   12: 503
    //                   17: 515
    //                   18: 527
    //                   19: 618
    //                   21: 573
    //                   22: 630
    //                   23: 239
    //                   24: 284
    //                   25: 642
    //                   27: 654
    //                   28: 666
    //                   31: 678
    //                   34: 731
    //                   35: 340
    //                   36: 340
    //                   37: 340
    //                   38: 845
    //                   39: 792
    //                   1001: 960
    //                   1002: 1039;
           goto _L1 _L2 _L3 _L4 _L5 _L6 _L7 _L8 _L9 _L10 _L11 _L12 _L13 _L14 _L15 _L16 _L17 _L18 _L19 _L20 _L20 _L20 _L21 _L22 _L23 _L24
_L21:
        break MISSING_BLOCK_LABEL_845;
_L23:
        break MISSING_BLOCK_LABEL_960;
_L24:
        break MISSING_BLOCK_LABEL_1039;
_L20:
        break; /* Loop/switch isn't completed */
_L1:
        throw new IllegalArgumentException((new StringBuilder()).append("Unknown Option ").append(i).toString());
_L13:
        sndhwm = ((Integer)obj).intValue();
        if (sndhwm < 0)
        {
            throw new IllegalArgumentException((new StringBuilder()).append("sndhwm ").append(obj).toString());
        }
        break; /* Loop/switch isn't completed */
_L14:
        rcvhwm = ((Integer)obj).intValue();
        if (rcvhwm < 0)
        {
            throw new IllegalArgumentException((new StringBuilder()).append("rcvhwm ").append(obj).toString());
        }
        break; /* Loop/switch isn't completed */
_L2:
        affinity = ((Long)obj).longValue();
_L26:
        return;
_L3:
        byte abyte0[];
        if (obj instanceof String)
        {
            abyte0 = ((String)obj).getBytes();
        } else
        if (obj instanceof byte[])
        {
            abyte0 = (byte[])(byte[])obj;
        } else
        {
            throw new IllegalArgumentException((new StringBuilder()).append("identity ").append(obj).toString());
        }
        if (abyte0 == null || abyte0.length > 255)
        {
            throw new IllegalArgumentException((new StringBuilder()).append("identity must not be null or less than 255 ").append(obj).toString());
        } else
        {
            identity = Arrays.copyOf(abyte0, abyte0.length);
            identity_size = (byte)identity.length;
            return;
        }
_L4:
        rate = ((Integer)obj).intValue();
        return;
_L5:
        recovery_ivl = ((Integer)obj).intValue();
        return;
_L6:
        sndbuf = ((Integer)obj).intValue();
        return;
_L7:
        rcvbuf = ((Integer)obj).intValue();
        return;
_L8:
        linger = ((Integer)obj).intValue();
        return;
_L9:
        reconnect_ivl = ((Integer)obj).intValue();
        if (reconnect_ivl >= -1) goto _L26; else goto _L25
_L25:
        throw new IllegalArgumentException((new StringBuilder()).append("reconnect_ivl ").append(obj).toString());
_L11:
        reconnect_ivl_max = ((Integer)obj).intValue();
        if (reconnect_ivl_max >= 0) goto _L26; else goto _L27
_L27:
        throw new IllegalArgumentException((new StringBuilder()).append("reconnect_ivl_max ").append(obj).toString());
_L10:
        backlog = ((Integer)obj).intValue();
        return;
_L12:
        maxmsgsize = ((Long)obj).longValue();
        return;
_L15:
        multicast_hops = ((Integer)obj).intValue();
        return;
_L16:
        rcvtimeo = ((Integer)obj).intValue();
        return;
_L17:
        sndtimeo = ((Integer)obj).intValue();
        return;
_L18:
        ipv4only = ((Integer)obj).intValue();
        if (ipv4only == 0 || ipv4only == 1) goto _L26; else goto _L28
_L28:
        throw new IllegalArgumentException((new StringBuilder()).append("ipv4only only accepts 0 or 1 ").append(obj).toString());
_L19:
        tcp_keepalive = ((Integer)obj).intValue();
        if (tcp_keepalive == -1 || tcp_keepalive == 0 || tcp_keepalive == 1) goto _L26; else goto _L29
_L29:
        throw new IllegalArgumentException((new StringBuilder()).append("tcp_keepalive only accepts one of -1,0,1 ").append(obj).toString());
_L22:
        delay_attach_on_connect = ((Integer)obj).intValue();
        if (delay_attach_on_connect == 0 || delay_attach_on_connect == 1) goto _L26; else goto _L30
_L30:
        throw new IllegalArgumentException((new StringBuilder()).append("delay_attach_on_connect only accept 0 or 1 ").append(obj).toString());
        String s = (String)obj;
        if (s == null)
        {
            tcp_accept_filters.clear();
            return;
        }
        if (s.length() == 0 || s.length() > 255)
        {
            throw new IllegalArgumentException((new StringBuilder()).append("tcp_accept_filter ").append(obj).toString());
        }
        TcpAddress.TcpAddressMask tcpaddressmask = new TcpAddress.TcpAddressMask();
        boolean flag;
        if (ipv4only == 1)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        tcpaddressmask.resolve(s, flag);
        tcp_accept_filters.add(tcpaddressmask);
        return;
        if (obj instanceof String)
        {
            try
            {
                encoder = Class.forName((String)obj).asSubclass(zmq/EncoderBase);
                return;
            }
            catch (ClassNotFoundException classnotfoundexception1)
            {
                throw new IllegalArgumentException(classnotfoundexception1);
            }
        }
        if (obj instanceof Class)
        {
            encoder = (Class)obj;
            return;
        } else
        {
            throw new IllegalArgumentException((new StringBuilder()).append("encoder ").append(obj).toString());
        }
        if (obj instanceof String)
        {
            try
            {
                decoder = Class.forName((String)obj).asSubclass(zmq/DecoderBase);
                return;
            }
            catch (ClassNotFoundException classnotfoundexception)
            {
                throw new IllegalArgumentException(classnotfoundexception);
            }
        }
        if (obj instanceof Class)
        {
            decoder = (Class)obj;
            return;
        } else
        {
            throw new IllegalArgumentException((new StringBuilder()).append("decoder ").append(obj).toString());
        }
    }
}
