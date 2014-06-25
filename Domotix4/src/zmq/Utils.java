// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.List;

public class Utils
{

    private static SecureRandom random = new SecureRandom();

    public Utils()
    {
    }

    public static byte[] bytes(ByteBuffer bytebuffer)
    {
        byte abyte0[] = new byte[bytebuffer.limit()];
        bytebuffer.get(abyte0);
        return abyte0;
    }

    public static boolean delete(File file)
    {
        boolean flag = true;
        if (!file.exists())
        {
            return false;
        }
        boolean flag1 = true;
        if (file.isDirectory())
        {
            File afile[] = file.listFiles();
            int i = afile.length;
            int j = 0;
            while (j < i) 
            {
                File file1 = afile[j];
                if (flag1 && delete(file1))
                {
                    flag1 = flag;
                } else
                {
                    flag1 = false;
                }
                j++;
            }
        }
        if (!flag1 || !file.delete())
        {
            flag = false;
        }
        return flag;
    }

    public static int generate_random()
    {
        return random.nextInt();
    }

    public static byte[] realloc(byte abyte0[], int i)
    {
        byte abyte1[] = new byte[i];
        if (abyte0 != null)
        {
            System.arraycopy(abyte0, 0, abyte1, 0, abyte0.length);
        }
        return abyte1;
    }

    public static Object[] realloc(Class class1, Object aobj[], int i, boolean flag)
    {
        if (i > aobj.length)
        {
            Object aobj2[] = (Object[])(Object[])Array.newInstance(class1, i);
            if (flag)
            {
                System.arraycopy(((Object) (aobj)), 0, ((Object) (aobj2)), 0, aobj.length);
                return aobj2;
            } else
            {
                System.arraycopy(((Object) (aobj)), 0, ((Object) (aobj2)), i - aobj.length, aobj.length);
                return aobj2;
            }
        }
        if (i < aobj.length)
        {
            Object aobj1[] = (Object[])(Object[])Array.newInstance(class1, i);
            if (flag)
            {
                System.arraycopy(((Object) (aobj)), aobj.length - i, ((Object) (aobj1)), 0, i);
                return aobj1;
            } else
            {
                System.arraycopy(((Object) (aobj)), 0, ((Object) (aobj1)), 0, i);
                return aobj1;
            }
        } else
        {
            return aobj;
        }
    }

    public static void swap(List list, int i, int j)
    {
        if (i != j)
        {
            Object obj = list.get(i);
            Object obj1 = list.get(j);
            if (obj != null)
            {
                list.set(j, obj);
            }
            if (obj1 != null)
            {
                list.set(i, obj1);
                return;
            }
        }
    }

    public static void tune_tcp_keepalives(Socket socket, int i, int j, int k, int l)
        throws SocketException
    {
        if (i == 1)
        {
            socket.setKeepAlive(true);
        } else
        if (i == 0)
        {
            socket.setKeepAlive(false);
            return;
        }
    }

    public static void tune_tcp_keepalives(SocketChannel socketchannel, int i, int j, int k, int l)
        throws SocketException
    {
        tune_tcp_keepalives(socketchannel.socket(), i, j, k, l);
    }

    public static void tune_tcp_socket(Socket socket)
        throws SocketException
    {
        try
        {
            socket.setTcpNoDelay(true);
            return;
        }
        catch (SocketException socketexception)
        {
            return;
        }
    }

    public static void tune_tcp_socket(SocketChannel socketchannel)
        throws SocketException
    {
        tune_tcp_socket(socketchannel.socket());
    }

    public static void unblock_socket(SelectableChannel selectablechannel)
        throws IOException
    {
        selectablechannel.configureBlocking(false);
    }

}
