// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            MultiMap

public class value
    implements java.util.
{

    private Comparable key;
    final MultiMap this$0;
    private Object value;

    public Comparable getKey()
    {
        return key;
    }

    public volatile Object getKey()
    {
        return getKey();
    }

    public Object getValue()
    {
        return value;
    }

    public Object setValue(Object obj)
    {
        Object obj1 = value;
        value = obj;
        return obj1;
    }

    public (Comparable comparable, Object obj)
    {
        this$0 = MultiMap.this;
        super();
        key = comparable;
        value = obj;
    }
}
