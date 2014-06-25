// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MultiMap
    implements Map
{
    public class MultiMapEntry
        implements java.util.Map.Entry
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

        public MultiMapEntry(Comparable comparable, Object obj)
        {
            this$0 = MultiMap.this;
            super();
            key = comparable;
            value = obj;
        }
    }

    public class MultiMapEntrySet
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
            return add((java.util.Map.Entry)obj);
        }

        public boolean add(java.util.Map.Entry entry)
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
                java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
                key = (Comparable)entry.getKey();
                iit = ((ArrayList)entry.getValue()).iterator();
            }
            return true;
        }

        public boolean isEmpty()
        {
            throw new UnsupportedOperationException();
        }

        public Iterator iterator()
        {
            it = map.keys.entrySet().iterator();
            return this;
        }

        public volatile Object next()
        {
            return next();
        }

        public java.util.Map.Entry next()
        {
            id = ((Long)iit.next()).longValue();
            return new MultiMapEntry(key, map.values.get(Long.valueOf(id)));
        }

        public void remove()
        {
            iit.remove();
            map.values.remove(Long.valueOf(id));
            if (((ArrayList)map.keys.get(key)).isEmpty())
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

        public MultiMapEntrySet(MultiMap multimap1)
        {
            this$0 = MultiMap.this;
            super();
            map = multimap1;
        }
    }


    private long id;
    private final TreeMap keys = new TreeMap();
    private final HashMap values = new HashMap();

    public MultiMap()
    {
        id = 0L;
    }

    public void clear()
    {
        keys.clear();
        values.clear();
    }

    public boolean containsKey(Object obj)
    {
        return keys.containsKey(obj);
    }

    public boolean containsValue(Object obj)
    {
        return values.containsValue(obj);
    }

    public Set entrySet()
    {
        return new MultiMapEntrySet(this);
    }

    public Object get(Object obj)
    {
        ArrayList arraylist = (ArrayList)keys.get(obj);
        if (arraylist == null)
        {
            return null;
        } else
        {
            return values.get(arraylist.get(0));
        }
    }

    public boolean isEmpty()
    {
        return keys.isEmpty();
    }

    public Set keySet()
    {
        return keys.keySet();
    }

    public Object put(Comparable comparable, Object obj)
    {
        ArrayList arraylist = (ArrayList)keys.get(comparable);
        if (arraylist == null)
        {
            ArrayList arraylist1 = new ArrayList();
            arraylist1.add(Long.valueOf(id));
            keys.put(comparable, arraylist1);
        } else
        {
            arraylist.add(Long.valueOf(id));
        }
        values.put(Long.valueOf(id), obj);
        id = 1L + id;
        return null;
    }

    public volatile Object put(Object obj, Object obj1)
    {
        return put((Comparable)obj, obj1);
    }

    public void putAll(Map map)
    {
        java.util.Map.Entry entry;
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); put((Comparable)entry.getKey(), entry.getValue()))
        {
            entry = (java.util.Map.Entry)iterator.next();
        }

    }

    public Object remove(Object obj)
    {
        ArrayList arraylist = (ArrayList)keys.get(obj);
        Object obj1;
        if (arraylist == null)
        {
            obj1 = null;
        } else
        {
            obj1 = values.remove(arraylist.remove(0));
            if (arraylist.isEmpty())
            {
                keys.remove(obj);
                return obj1;
            }
        }
        return obj1;
    }

    public int size()
    {
        return values.size();
    }

    public Collection values()
    {
        return values.values();
    }


}
