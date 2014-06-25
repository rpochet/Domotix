// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.lang.reflect.Array;

public class YQueue
{
    private class Chunk
    {

        static final boolean $assertionsDisabled;
        Chunk next;
        final int pos[];
        Chunk prev;
        final YQueue this$0;
        final Object values[];

        static 
        {
            boolean flag;
            if (!zmq/YQueue.desiredAssertionStatus())
            {
                flag = true;
            } else
            {
                flag = false;
            }
            $assertionsDisabled = flag;
        }

        protected Chunk(Class class1, int i, int j)
        {
            this$0 = YQueue.this;
            super();
            values = (Object[])(Object[])Array.newInstance(class1, i);
            pos = new int[i];
            if (!$assertionsDisabled && values == null)
            {
                throw new AssertionError();
            }
            next = null;
            prev = null;
            for (int k = 0; k != values.length; k++)
            {
                pos[k] = j;
                j++;
            }

        }
    }


    private Chunk back_chunk;
    private int back_pos;
    private volatile Chunk begin_chunk;
    private int begin_pos;
    private Chunk end_chunk;
    private int end_pos;
    private final Class klass;
    private int memory_ptr;
    private final int size;
    private Chunk spare_chunk;

    public YQueue(Class class1, int i)
    {
        klass = class1;
        size = i;
        memory_ptr = 0;
        begin_chunk = new Chunk(class1, i, memory_ptr);
        memory_ptr = i + memory_ptr;
        begin_pos = 0;
        back_pos = 0;
        back_chunk = begin_chunk;
        spare_chunk = begin_chunk;
        end_chunk = begin_chunk;
        end_pos = 1;
    }

    public final Object back()
    {
        return back_chunk.values[back_pos];
    }

    public final int back_pos()
    {
        return back_chunk.pos[back_pos];
    }

    public final Object front()
    {
        return begin_chunk.values[begin_pos];
    }

    public final int front_pos()
    {
        return begin_chunk.pos[begin_pos];
    }

    public final Object pop()
    {
        Object obj = begin_chunk.values[begin_pos];
        begin_chunk.values[begin_pos] = null;
        begin_pos = 1 + begin_pos;
        if (begin_pos == size)
        {
            begin_chunk = begin_chunk.next;
            begin_chunk.prev = null;
            begin_pos = 0;
        }
        return obj;
    }

    public final void push(Object obj)
    {
        back_chunk.values[back_pos] = obj;
        back_chunk = end_chunk;
        back_pos = end_pos;
        end_pos = 1 + end_pos;
        if (end_pos != size)
        {
            return;
        }
        Chunk chunk = spare_chunk;
        if (chunk != begin_chunk)
        {
            spare_chunk = spare_chunk.next;
            end_chunk.next = chunk;
            chunk.prev = end_chunk;
        } else
        {
            end_chunk.next = new Chunk(klass, size, memory_ptr);
            memory_ptr = memory_ptr + size;
            end_chunk.next.prev = end_chunk;
        }
        end_chunk = end_chunk.next;
        end_pos = 0;
    }

    public final void unpush()
    {
        if (back_pos > 0)
        {
            back_pos = -1 + back_pos;
        } else
        {
            back_pos = -1 + size;
            back_chunk = back_chunk.prev;
        }
        if (end_pos > 0)
        {
            end_pos = -1 + end_pos;
            return;
        } else
        {
            end_pos = -1 + size;
            end_chunk = end_chunk.prev;
            end_chunk.next = null;
            return;
        }
    }
}
