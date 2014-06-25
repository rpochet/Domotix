// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.zeromq;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

// Referenced classes of package org.zeromq:
//            ZFrame

public class ZMsg
    implements Iterable, Deque
{

    private ArrayDeque frames;

    public ZMsg()
    {
        frames = new ArrayDeque();
    }

    public static ZMsg load(DataInputStream datainputstream)
    {
        if (datainputstream != null) goto _L2; else goto _L1
_L1:
        ZMsg zmsg = null;
_L4:
        return zmsg;
_L2:
        zmsg = new ZMsg();
        int i;
        int j;
        byte abyte0[];
        try
        {
            i = datainputstream.readInt();
        }
        catch (IOException ioexception)
        {
            return null;
        }
        if (i <= 0)
        {
            continue; /* Loop/switch isn't completed */
        }
        j = 0;
        if (++j > i)
        {
            continue; /* Loop/switch isn't completed */
        }
        abyte0 = new byte[datainputstream.readInt()];
        datainputstream.read(abyte0);
        zmsg.add(new ZFrame(abyte0));
        break MISSING_BLOCK_LABEL_28;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public static transient ZMsg newStringMsg(String as[])
    {
        ZMsg zmsg = new ZMsg();
        int i = as.length;
        for (int j = 0; j < i; j++)
        {
            zmsg.addString(as[j]);
        }

        return zmsg;
    }

    public static ZMsg recvMsg(ZMQ.Socket socket)
    {
        return recvMsg(socket, 0);
    }

    public static ZMsg recvMsg(ZMQ.Socket socket, int i)
    {
        if (socket == null)
        {
            throw new IllegalArgumentException("socket is null");
        }
        ZMsg zmsg = new ZMsg();
        ZFrame zframe;
        do
        {
            zframe = ZFrame.recvFrame(socket, i);
            if (zframe == null)
            {
                zmsg.destroy();
                return null;
            }
            zmsg.add(zframe);
        } while (zframe.hasMore());
        return zmsg;
    }

    public static boolean save(ZMsg zmsg, DataOutputStream dataoutputstream)
    {
        if (zmsg == null)
        {
            return false;
        }
        try
        {
            dataoutputstream.writeInt(zmsg.size());
            if (zmsg.size() > 0)
            {
                ZFrame zframe;
                for (Iterator iterator1 = zmsg.iterator(); iterator1.hasNext(); dataoutputstream.write(zframe.getData()))
                {
                    zframe = (ZFrame)iterator1.next();
                    dataoutputstream.writeInt(zframe.size());
                }

            }
        }
        catch (IOException ioexception)
        {
            return false;
        }
        return true;
    }

    public volatile boolean add(Object obj)
    {
        return add((ZFrame)obj);
    }

    public boolean add(String s)
    {
        return add(new ZFrame(s));
    }

    public boolean add(ZFrame zframe)
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        return frames.add(zframe);
    }

    public boolean add(byte abyte0[])
    {
        return add(new ZFrame(abyte0));
    }

    public boolean addAll(Collection collection)
    {
        return frames.addAll(collection);
    }

    public volatile void addFirst(Object obj)
    {
        addFirst((ZFrame)obj);
    }

    public void addFirst(String s)
    {
        addFirst(new ZFrame(s));
    }

    public void addFirst(ZFrame zframe)
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        frames.addFirst(zframe);
    }

    public void addFirst(byte abyte0[])
    {
        addFirst(new ZFrame(abyte0));
    }

    public volatile void addLast(Object obj)
    {
        addLast((ZFrame)obj);
    }

    public void addLast(String s)
    {
        addLast(new ZFrame(s));
    }

    public void addLast(ZFrame zframe)
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        frames.addLast(zframe);
    }

    public void addLast(byte abyte0[])
    {
        addLast(new ZFrame(abyte0));
    }

    public void addString(String s)
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        frames.add(new ZFrame(s));
    }

    public void clear()
    {
        frames.clear();
    }

    public boolean contains(Object obj)
    {
        return frames.contains(obj);
    }

    public boolean containsAll(Collection collection)
    {
        return frames.containsAll(collection);
    }

    public long contentSize()
    {
        long l = 0L;
        for (Iterator iterator1 = frames.iterator(); iterator1.hasNext();)
        {
            l += ((ZFrame)iterator1.next()).size();
        }

        return l;
    }

    public Iterator descendingIterator()
    {
        return frames.descendingIterator();
    }

    public void destroy()
    {
        if (frames == null)
        {
            return;
        }
        for (Iterator iterator1 = frames.iterator(); iterator1.hasNext(); ((ZFrame)iterator1.next()).destroy()) { }
        frames.clear();
        frames = null;
    }

    public void dump()
    {
        dump(((Appendable) (System.out)));
    }

    public void dump(Appendable appendable)
    {
        StringWriter stringwriter;
        try
        {
            stringwriter = new StringWriter();
            PrintWriter printwriter = new PrintWriter(stringwriter);
            printwriter.printf("--------------------------------------\n", new Object[0]);
            Object aobj[];
            for (Iterator iterator1 = frames.iterator(); iterator1.hasNext(); printwriter.printf("[%03d] %s\n", aobj))
            {
                ZFrame zframe = (ZFrame)iterator1.next();
                aobj = new Object[2];
                aobj[0] = Integer.valueOf(zframe.size());
                aobj[1] = zframe.toString();
            }

        }
        catch (IOException ioexception)
        {
            throw new RuntimeException((new StringBuilder()).append("Message dump exception ").append(super.toString()).toString(), ioexception);
        }
        appendable.append(stringwriter.getBuffer());
        stringwriter.close();
        return;
    }

    public ZMsg duplicate()
    {
        ZMsg zmsg;
        if (frames != null)
        {
            zmsg = new ZMsg();
            for (Iterator iterator1 = frames.iterator(); iterator1.hasNext(); zmsg.add(((ZFrame)iterator1.next()).duplicate())) { }
        } else
        {
            zmsg = null;
        }
        return zmsg;
    }

    public volatile Object element()
    {
        return element();
    }

    public ZFrame element()
    {
        return (ZFrame)frames.element();
    }

    public boolean equals(Object obj)
    {
        boolean flag = true;
        if (this != obj) goto _L2; else goto _L1
_L1:
        boolean flag1 = flag;
_L4:
        return flag1;
_L2:
        Iterator iterator1;
        Iterator iterator2;
        flag1 = false;
        if (obj == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        Class class1 = getClass();
        Class class2 = obj.getClass();
        flag1 = false;
        if (class1 != class2)
        {
            continue; /* Loop/switch isn't completed */
        }
        ZMsg zmsg = (ZMsg)obj;
        ArrayDeque arraydeque = frames;
        flag1 = false;
        if (arraydeque == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        ArrayDeque arraydeque1 = zmsg.frames;
        flag1 = false;
        if (arraydeque1 == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        iterator1 = frames.iterator();
        iterator2 = zmsg.frames.iterator();
_L6:
        ZFrame zframe;
        ZFrame zframe1;
        do
        {
            if (!iterator1.hasNext() || !iterator2.hasNext())
            {
                break MISSING_BLOCK_LABEL_161;
            }
            zframe = (ZFrame)iterator1.next();
            zframe1 = (ZFrame)iterator2.next();
            if (zframe != null)
            {
                continue; /* Loop/switch isn't completed */
            }
            flag1 = false;
        } while (zframe1 == null);
        if (true) goto _L4; else goto _L3
_L3:
        if (zframe.equals(zframe1)) goto _L6; else goto _L5
_L5:
        return false;
        if (iterator1.hasNext() || iterator2.hasNext())
        {
            flag = false;
        }
        return flag;
    }

    public volatile Object getFirst()
    {
        return getFirst();
    }

    public ZFrame getFirst()
    {
        ZFrame zframe;
        try
        {
            zframe = (ZFrame)frames.getFirst();
        }
        catch (NoSuchElementException nosuchelementexception)
        {
            return null;
        }
        return zframe;
    }

    public volatile Object getLast()
    {
        return getLast();
    }

    public ZFrame getLast()
    {
        ZFrame zframe;
        try
        {
            zframe = (ZFrame)frames.getLast();
        }
        catch (NoSuchElementException nosuchelementexception)
        {
            return null;
        }
        return zframe;
    }

    public int hashCode()
    {
        int i;
        if (frames == null || frames.size() == 0)
        {
            i = 0;
        } else
        {
            i = 1;
            Iterator iterator1 = frames.iterator();
            while (iterator1.hasNext()) 
            {
                ZFrame zframe = (ZFrame)iterator1.next();
                int j = i * 31;
                int k;
                if (zframe == null)
                {
                    k = 0;
                } else
                {
                    k = zframe.hashCode();
                }
                i = j + k;
            }
        }
        return i;
    }

    public boolean isEmpty()
    {
        return frames.isEmpty();
    }

    public Iterator iterator()
    {
        return frames.iterator();
    }

    public volatile boolean offer(Object obj)
    {
        return offer((ZFrame)obj);
    }

    public boolean offer(ZFrame zframe)
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        return frames.offer(zframe);
    }

    public volatile boolean offerFirst(Object obj)
    {
        return offerFirst((ZFrame)obj);
    }

    public boolean offerFirst(ZFrame zframe)
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        return frames.offerFirst(zframe);
    }

    public volatile boolean offerLast(Object obj)
    {
        return offerLast((ZFrame)obj);
    }

    public boolean offerLast(ZFrame zframe)
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        return frames.offerLast(zframe);
    }

    public volatile Object peek()
    {
        return peek();
    }

    public ZFrame peek()
    {
        return (ZFrame)frames.peek();
    }

    public volatile Object peekFirst()
    {
        return peekFirst();
    }

    public ZFrame peekFirst()
    {
        ZFrame zframe;
        try
        {
            zframe = (ZFrame)frames.peekFirst();
        }
        catch (NoSuchElementException nosuchelementexception)
        {
            return null;
        }
        return zframe;
    }

    public volatile Object peekLast()
    {
        return peekLast();
    }

    public ZFrame peekLast()
    {
        ZFrame zframe;
        try
        {
            zframe = (ZFrame)frames.peekLast();
        }
        catch (NoSuchElementException nosuchelementexception)
        {
            return null;
        }
        return zframe;
    }

    public volatile Object poll()
    {
        return poll();
    }

    public ZFrame poll()
    {
        return (ZFrame)frames.poll();
    }

    public volatile Object pollFirst()
    {
        return pollFirst();
    }

    public ZFrame pollFirst()
    {
        return (ZFrame)frames.pollFirst();
    }

    public volatile Object pollLast()
    {
        return pollLast();
    }

    public ZFrame pollLast()
    {
        return (ZFrame)frames.pollLast();
    }

    public volatile Object pop()
    {
        return pop();
    }

    public ZFrame pop()
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        ZFrame zframe;
        try
        {
            zframe = (ZFrame)frames.pop();
        }
        catch (NoSuchElementException nosuchelementexception)
        {
            return null;
        }
        return zframe;
    }

    public String popString()
    {
        ZFrame zframe = pop();
        if (zframe == null)
        {
            return null;
        } else
        {
            return zframe.toString();
        }
    }

    public volatile void push(Object obj)
    {
        push((ZFrame)obj);
    }

    public void push(String s)
    {
        push(new ZFrame(s));
    }

    public void push(ZFrame zframe)
    {
        if (frames == null)
        {
            frames = new ArrayDeque();
        }
        frames.push(zframe);
    }

    public void push(byte abyte0[])
    {
        push(new ZFrame(abyte0));
    }

    public volatile Object remove()
    {
        return remove();
    }

    public ZFrame remove()
    {
        return (ZFrame)frames.remove();
    }

    public boolean remove(Object obj)
    {
        return frames.remove(obj);
    }

    public boolean removeAll(Collection collection)
    {
        return frames.removeAll(collection);
    }

    public volatile Object removeFirst()
    {
        return removeFirst();
    }

    public ZFrame removeFirst()
    {
        ZFrame zframe;
        try
        {
            zframe = (ZFrame)frames.removeFirst();
        }
        catch (NoSuchElementException nosuchelementexception)
        {
            return null;
        }
        return zframe;
    }

    public boolean removeFirstOccurrence(Object obj)
    {
        return frames.removeFirstOccurrence(obj);
    }

    public volatile Object removeLast()
    {
        return removeLast();
    }

    public ZFrame removeLast()
    {
        ZFrame zframe;
        try
        {
            zframe = (ZFrame)frames.removeLast();
        }
        catch (NoSuchElementException nosuchelementexception)
        {
            return null;
        }
        return zframe;
    }

    public boolean removeLastOccurrence(Object obj)
    {
        return frames.removeLastOccurrence(obj);
    }

    public boolean retainAll(Collection collection)
    {
        return frames.retainAll(collection);
    }

    public boolean send(ZMQ.Socket socket)
    {
        return send(socket, true);
    }

    public boolean send(ZMQ.Socket socket, boolean flag)
    {
        if (socket == null)
        {
            throw new IllegalArgumentException("socket is null");
        }
        if (frames == null)
        {
            throw new IllegalArgumentException("destroyed message");
        }
        boolean flag1;
        if (frames.size() == 0)
        {
            flag1 = true;
        } else
        {
            flag1 = true;
            Iterator iterator1 = frames.iterator();
            while (iterator1.hasNext()) 
            {
                ZFrame zframe = (ZFrame)iterator1.next();
                byte byte0;
                if (iterator1.hasNext())
                {
                    byte0 = 2;
                } else
                {
                    byte0 = 0;
                }
                flag1 = zframe.sendAndKeep(socket, byte0);
            }
            if (flag)
            {
                destroy();
                return flag1;
            }
        }
        return flag1;
    }

    public int size()
    {
        return frames.size();
    }

    public Object[] toArray()
    {
        return frames.toArray();
    }

    public Object[] toArray(Object aobj[])
    {
        return frames.toArray(aobj);
    }

    public ZFrame unwrap()
    {
        ZFrame zframe;
        if (size() == 0)
        {
            zframe = null;
        } else
        {
            zframe = pop();
            ZFrame zframe1 = getFirst();
            if (zframe1.hasData() && zframe1.size() == 0)
            {
                pop().destroy();
                return zframe;
            }
        }
        return zframe;
    }

    public void wrap(ZFrame zframe)
    {
        if (zframe != null)
        {
            push(new ZFrame(""));
            push(zframe);
        }
    }
}
