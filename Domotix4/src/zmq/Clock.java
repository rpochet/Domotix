// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


public class Clock
{

    private Clock()
    {
    }

    public static final long now_ms()
    {
        return System.currentTimeMillis();
    }

    public static final long now_us()
    {
        return 1000L * System.currentTimeMillis();
    }

    public static final long rdtsc()
    {
        return 0L;
    }
}
