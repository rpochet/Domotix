// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.net.SocketAddress;

// Referenced classes of package zmq:
//            Address

public static interface 
{

    public abstract SocketAddress address();

    public abstract void resolve(String s, boolean flag);

    public abstract String toString();
}
