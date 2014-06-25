// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


public interface IPollEvents
{

    public abstract void accept_event();

    public abstract void connect_event();

    public abstract void in_event();

    public abstract void out_event();

    public abstract void timer_event(int i);
}
