// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

// Referenced classes of package zmq:
//            MultiMap

public class map
    implements Set, Iterator
{

    private long id;
    private Iterator iit;
    private Iterator it;
    private Comparable key;
    private MultiMap map;
    final MultiMap this$0;

    public volatile boolean add(Object obj)
    {
        return add((java.util.)obj);
    }

    public boolean add(java.util. )
    {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection collection)
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object obj)
    {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection collection)
    {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext()
    {
        if (iit == null || !iit.hasNext())
        {
            if (!it.hasNext())
            {
                return false;
            }
            java.util.ion ion = (java.util.ion)it.next();
            key = (Comparable)ion.ion();
            iit = ((ArrayList)ion.ion()).iterator();
        }
        return true;
    }

    public boolean isEmpty()
    {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator()
    {
        it = MultiMap.access$000(map).entrySet().iterator();
        return this;
    }

    public volatile Object next()
    {
        return next();
    }

    public java.util.ion next()
    {
        id = ((Long)iit.next()).longValue();
        return new it>(MultiMap.this, key, MultiMap.access$100(map).get(Long.valueOf(id)));
    }

    public void remove()
    {
        iit.remove();
        MultiMap.access$100(map).remove(Long.valueOf(id));
        if (((ArrayList)MultiMap.access$000(map).get(key)).isEmpty())
        {
            it.remove();
        }
    }

    public boolean remove(Object obj)
    {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection collection)
    {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection collection)
    {
        throw new UnsupportedOperationException();
    }

    public int size()
    {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray(Object aobj[])
    {
        throw new UnsupportedOperationException();
    }

    public Exception(MultiMap multimap1)
    {
        this$0 = MultiMap.this;
        super();
        map = multimap1;
    }
}
