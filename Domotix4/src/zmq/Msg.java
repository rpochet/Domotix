// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Msg
{

    public static final int identity = 64;
    public static final int more = 1;
    public static final int shared = 128;
    private static final byte type_delimiter = 104;
    private static final byte type_lmsg = 103;
    private static final byte type_max = 105;
    private static final byte type_min = 101;
    private static final byte type_vsm = 102;
    private ByteBuffer buf;
    private byte data[];
    private int flags;
    private int size;
    private byte type;

    public Msg()
    {
        init((byte)102);
        size(0);
    }

    public Msg(int i)
    {
        init((byte)102);
        size(i);
    }

    public Msg(int i, boolean flag)
    {
        if (flag)
        {
            init((byte)103);
        } else
        {
            init((byte)102);
        }
        size(i);
    }

    public Msg(String s)
    {
        this(s.getBytes(), false);
    }

    public Msg(ByteBuffer bytebuffer)
    {
        init((byte)103);
        buf = bytebuffer.duplicate();
        buf.rewind();
        size = buf.remaining();
    }

    public Msg(Msg msg)
    {
        clone(msg);
    }

    public Msg(boolean flag)
    {
        if (flag)
        {
            init((byte)103);
            return;
        } else
        {
            init((byte)102);
            return;
        }
    }

    public Msg(byte abyte0[])
    {
        this(abyte0, false);
    }

    public Msg(byte abyte0[], boolean flag)
    {
label0:
        {
            this();
            if (abyte0 != null)
            {
                size = abyte0.length;
                if (!flag)
                {
                    break label0;
                }
                data = Arrays.copyOf(abyte0, abyte0.length);
            }
            return;
        }
        data = abyte0;
    }

    private void clone(Msg msg)
    {
        type = msg.type;
        flags = msg.flags;
        size = msg.size;
        buf = msg.buf;
        data = msg.data;
    }

    private final void size(int i)
    {
        size = i;
        if (type == 103)
        {
            flags = 0;
            buf = ByteBuffer.allocate(i);
            data = null;
            return;
        } else
        {
            flags = 0;
            data = new byte[i];
            buf = null;
            return;
        }
    }

    public final ByteBuffer buf()
    {
        if (buf == null && type != 103)
        {
            buf = ByteBuffer.wrap(data);
        }
        return buf;
    }

    public final boolean check()
    {
        return type >= 101 && type <= 105;
    }

    public final void close()
    {
        if (!check())
        {
            throw new IllegalStateException();
        } else
        {
            init((byte)102);
            return;
        }
    }

    public final byte[] data()
    {
        if (data == null && type == 103)
        {
            if (buf.arrayOffset() == 0)
            {
                data = buf.array();
            } else
            {
                data = new byte[size];
                System.arraycopy(buf.array(), buf.arrayOffset(), data, 0, size);
            }
        }
        return data;
    }

    public final int flags()
    {
        return flags;
    }

    public final boolean has_more()
    {
        return (1 & flags) > 0;
    }

    protected final void init(byte byte0)
    {
        type = byte0;
        flags = 0;
        size = -1;
        data = null;
        buf = null;
    }

    public final void init_delimiter()
    {
        type = 104;
        flags = 0;
    }

    public final boolean is_delimiter()
    {
        return type == 104;
    }

    public final boolean is_identity()
    {
        return (0x40 & flags) == 64;
    }

    public final boolean is_vsm()
    {
        return type == 102;
    }

    public final void put(byte byte0)
    {
        data[0] = byte0;
    }

    public final void put(byte byte0, int i)
    {
        data[i] = byte0;
    }

    public final void put(String s, int i)
    {
        put(s.getBytes(), i);
    }

    public final void put(Msg msg, int i)
    {
        put(msg.data, i);
    }

    public final void put(byte abyte0[], int i)
    {
        if (abyte0 == null)
        {
            return;
        } else
        {
            System.arraycopy(abyte0, 0, data, i, abyte0.length);
            return;
        }
    }

    public final void put(byte abyte0[], int i, int j)
    {
        if (j == 0 || abyte0 == null)
        {
            return;
        } else
        {
            System.arraycopy(abyte0, 0, data, i, j);
            return;
        }
    }

    public final void reset_flags(int i)
    {
        flags = flags & ~i;
    }

    public final void set_flags(int i)
    {
        flags = i | flags;
    }

    public final int size()
    {
        return size;
    }

    public String toString()
    {
        return (new StringBuilder()).append(super.toString()).append("[").append(type).append(",").append(size).append(",").append(flags).append("]").toString();
    }

    public final byte type()
    {
        return type;
    }
}
