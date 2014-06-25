// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class ZFrame
{

    public static final int DONTWAIT = 4;
    public static final int MORE = 1;
    public static final int REUSE = 2;
    private byte data[];
    private boolean more;

    protected ZFrame()
    {
    }

    public ZFrame(String s)
    {
        if (s != null)
        {
            data = s.getBytes();
        }
    }

    public ZFrame(byte abyte0[])
    {
        if (abyte0 != null)
        {
            data = abyte0;
        }
    }

    private byte[] recv(ZMQ.Socket socket, int i)
    {
        if (socket == null)
        {
            throw new IllegalArgumentException("socket parameter must not be null");
        } else
        {
            data = socket.recv(i);
            more = socket.hasReceiveMore();
            return data;
        }
    }

    public static ZFrame recvFrame(ZMQ.Socket socket)
    {
        ZFrame zframe = new ZFrame();
        zframe.recv(socket, 0);
        return zframe;
    }

    public static ZFrame recvFrame(ZMQ.Socket socket, int i)
    {
        ZFrame zframe = new ZFrame();
        zframe.recv(socket, i);
        return zframe;
    }

    public byte[] data()
    {
        return data;
    }

    public void destroy()
    {
        if (hasData())
        {
            data = null;
        }
    }

    public ZFrame duplicate()
    {
        return new ZFrame(data);
    }

    public boolean equals(Object obj)
    {
        if (this != obj)
        {
            if (obj == null || getClass() != obj.getClass())
            {
                return false;
            }
            ZFrame zframe = (ZFrame)obj;
            if (!Arrays.equals(data, zframe.data))
            {
                return false;
            }
        }
        return true;
    }

    public byte[] getData()
    {
        return data;
    }

    public boolean hasData()
    {
        return data != null;
    }

    public boolean hasMore()
    {
        return more;
    }

    public boolean hasSameData(ZFrame zframe)
    {
        while (zframe == null || size() != zframe.size()) 
        {
            return false;
        }
        return Arrays.equals(data, zframe.data);
    }

    public int hashCode()
    {
        if (data != null)
        {
            return Arrays.hashCode(data);
        } else
        {
            return 0;
        }
    }

    public void print(String s)
    {
        StringWriter stringwriter = new StringWriter();
        PrintWriter printwriter = new PrintWriter(stringwriter);
        if (s != null)
        {
            printwriter.printf("%s", new Object[] {
                s
            });
        }
        byte abyte0[] = getData();
        int i = size();
        boolean flag = false;
        for (int j = 0; j < i; j++)
        {
            if (abyte0[j] < 9 || abyte0[j] > 127)
            {
                flag = true;
            }
        }

        Object aobj[] = new Object[1];
        aobj[0] = Integer.valueOf(i);
        printwriter.printf("[%03d] ", aobj);
        byte byte0;
        String s1;
        int k;
        if (flag)
        {
            byte0 = 35;
        } else
        {
            byte0 = 70;
        }
        s1 = "";
        if (i > byte0)
        {
            i = byte0;
            s1 = "...";
        }
        k = 0;
        while (k < i) 
        {
            if (flag)
            {
                Object aobj2[] = new Object[1];
                aobj2[0] = Byte.valueOf(abyte0[k]);
                printwriter.printf("%02X", aobj2);
            } else
            {
                Object aobj1[] = new Object[1];
                aobj1[0] = Byte.valueOf(abyte0[k]);
                printwriter.printf("%c", aobj1);
            }
            k++;
        }
        printwriter.printf("%s\n", new Object[] {
            s1
        });
        printwriter.flush();
        printwriter.close();
        try
        {
            stringwriter.close();
        }
        catch (IOException ioexception) { }
        System.out.print(stringwriter.toString());
    }

    public void reset(String s)
    {
        data = s.getBytes();
    }

    public void reset(byte abyte0[])
    {
        data = abyte0;
    }

    public boolean send(ZMQ.Socket socket, int i)
    {
        byte byte0 = 2;
        if (socket == null)
        {
            throw new IllegalArgumentException("socket parameter must be set");
        }
        byte byte1;
        boolean flag;
        int j;
        int k;
        if ((i & 1) > 0)
        {
            byte1 = byte0;
        } else
        {
            byte1 = 0;
        }
        if ((i & 4) > 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        j = byte1 | flag;
        if (i != byte0)
        {
            byte0 = 0;
        }
        k = j | byte0;
        return socket.send(data, k);
    }

    public boolean sendAndDestroy(ZMQ.Socket socket)
    {
        return sendAndDestroy(socket, 0);
    }

    public boolean sendAndDestroy(ZMQ.Socket socket, int i)
    {
        boolean flag = send(socket, i);
        if (flag)
        {
            destroy();
        }
        return flag;
    }

    public boolean sendAndKeep(ZMQ.Socket socket)
    {
        return sendAndKeep(socket, 0);
    }

    public boolean sendAndKeep(ZMQ.Socket socket, int i)
    {
        return send(socket, i);
    }

    public int size()
    {
        if (hasData())
        {
            return data.length;
        } else
        {
            return 0;
        }
    }

    public boolean streq(String s)
    {
        while (!hasData() || (new String(data)).compareTo(s) != 0) 
        {
            return false;
        }
        return true;
    }

    public String strhex()
    {
        StringBuilder stringbuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++)
        {
            int j = 0xf & data[i] >>> 4;
            int k = 0xf & data[i];
            stringbuilder.append("0123456789ABCDEF".charAt(j));
            stringbuilder.append("0123456789ABCDEF".charAt(k));
        }

        return stringbuilder.toString();
    }

    public String toString()
    {
        if (!hasData())
        {
            return null;
        }
        boolean flag = true;
        for (int i = 0; i < data.length; i++)
        {
            if (data[i] < 32 || data[i] > 127)
            {
                flag = false;
            }
        }

        if (flag)
        {
            return new String(data);
        } else
        {
            return strhex();
        }
    }
}
