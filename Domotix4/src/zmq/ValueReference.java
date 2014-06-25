// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


public class ValueReference
{

    private Object value;

    public ValueReference()
    {
    }

    public ValueReference(Object obj)
    {
        value = obj;
    }

    public final Object get()
    {
        return value;
    }

    public final void set(Object obj)
    {
        value = obj;
    }
}
