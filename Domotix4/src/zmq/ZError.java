// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;

public class ZError
{
    public static class CtxTerminatedException extends RuntimeException
    {

        private static final long serialVersionUID = 0xc2de92ed12680d24L;

        public CtxTerminatedException()
        {
        }
    }

    public static class IOException extends RuntimeException
    {

        private static final long serialVersionUID = 0x7fb5be563a6a4bd6L;

        public IOException(java.io.IOException ioexception)
        {
            super(ioexception);
        }
    }

    public static class InstantiationException extends RuntimeException
    {

        private static final long serialVersionUID = 0xc2de92ed12680d25L;

        public InstantiationException(Throwable throwable)
        {
            super(throwable);
        }
    }


    public static final int EACCESS = 13;
    public static final int EADDRINUSE = 48;
    public static final int EADDRNOTAVAIL = 49;
    public static final int EAGAIN = 35;
    public static final int ECONNREFUSED = 61;
    public static final int EFAULT = 14;
    public static final int EFSM = 0x9523dfb;
    public static final int EHOSTUNREACH = 65;
    public static final int EINPROGRESS = 36;
    public static final int EINTR = 4;
    public static final int EINVAL = 22;
    public static final int EIOEXC = 0x9523e31;
    public static final int EISCONN = 56;
    public static final int EMFILE = 0x9523e33;
    public static final int EMTHREAD = 0x9523dfe;
    public static final int ENETDOWN = 50;
    public static final int ENOBUFS = 55;
    public static final int ENOCOMPATPROTO = 0x9523dfc;
    public static final int ENOTCONN = 57;
    public static final int ENOTSUP = 45;
    public static final int EPROTONOSUPPORT = 43;
    public static final int ESOCKET = 0x9523e32;
    public static final int ETERM = 0x9523dfd;
    private static final int ZMQ_HAUSNUMERO = 0x9523dc8;

    public ZError()
    {
    }

    public static int exccode(java.io.IOException ioexception)
    {
        if (ioexception instanceof SocketException)
        {
            return 0x9523e32;
        }
        return !(ioexception instanceof ClosedChannelException) ? 0x9523e31 : 57;
    }

    public static String toString(int i)
    {
        switch (i)
        {
        default:
            return "";

        case 48: // '0'
            return "Address already in use";

        case 156384763: 
            return "Operation cannot be accomplished in current state";

        case 156384764: 
            return "The protocol is not compatible with the socket type";

        case 156384765: 
            return "Context was terminated";

        case 156384766: 
            return "No thread available";
        }
    }
}
