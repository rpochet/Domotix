// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.nio.channels.Selector;

// Referenced classes of package zmq:
//            PollItem, ZMQ, SocketBase, Msg

public class Proxy
{

    public Proxy()
    {
    }

    public static boolean proxy(SocketBase socketbase, SocketBase socketbase1, SocketBase socketbase2)
    {
        PollItem apollitem[];
        Selector selector;
        apollitem = new PollItem[2];
        apollitem[0] = new PollItem(socketbase, 1);
        apollitem[1] = new PollItem(socketbase1, 1);
        int i;
        try
        {
            selector = Selector.open();
        }
        catch (IOException ioexception)
        {
            throw new ZError.IOException(ioexception);
        }
_L6:
        if (Thread.currentThread().isInterrupted()) goto _L2; else goto _L1
_L1:
        i = ZMQ.zmq_poll(selector, apollitem, -1L);
        if (i < 0)
        {
            Exception exception;
            Msg msg;
            int j;
            long l;
            byte byte0;
            boolean flag;
            Msg msg1;
            int k;
            long l1;
            byte byte1;
            boolean flag1;
            Exception exception3;
            Msg msg2;
            byte byte2;
            boolean flag2;
            Exception exception4;
            Exception exception5;
            Exception exception6;
            Exception exception7;
            Msg msg3;
            byte byte3;
            boolean flag3;
            Exception exception8;
            Exception exception9;
            Exception exception10;
            try
            {
                selector.close();
            }
            catch (Exception exception11)
            {
                return false;
            }
            return false;
        }
        if (!apollitem[0].isReadable())
        {
            continue; /* Loop/switch isn't completed */
        }
_L4:
        msg = socketbase.recv(0);
        if (msg == null)
        {
            try
            {
                selector.close();
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception10)
            {
                return false;
            }
            return false;
        }
        j = socketbase.getsockopt(13);
        l = j;
        if (l < 0L)
        {
            try
            {
                selector.close();
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception9)
            {
                return false;
            }
            return false;
        }
        if (socketbase2 == null)
        {
            break MISSING_BLOCK_LABEL_198;
        }
        msg3 = new Msg(msg);
        if (l > 0L)
        {
            byte3 = 2;
        } else
        {
            byte3 = 0;
        }
        flag3 = socketbase2.send(msg3, byte3);
        if (!flag3)
        {
            try
            {
                selector.close();
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception8)
            {
                return false;
            }
            return false;
        }
        if (l > 0L)
        {
            byte0 = 2;
        } else
        {
            byte0 = 0;
        }
        flag = socketbase1.send(msg, byte0);
        if (!flag)
        {
            try
            {
                selector.close();
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception7)
            {
                return false;
            }
            return false;
        }
        if (l != 0L) goto _L4; else goto _L3
_L3:
        if (!apollitem[1].isReadable()) goto _L6; else goto _L5
_L5:
        msg1 = socketbase1.recv(0);
        if (msg1 == null)
        {
            try
            {
                selector.close();
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception6)
            {
                return false;
            }
            return false;
        }
        k = socketbase1.getsockopt(13);
        l1 = k;
        if (l1 < 0L)
        {
            try
            {
                selector.close();
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception5)
            {
                return false;
            }
            return false;
        }
        if (socketbase2 == null)
        {
            break MISSING_BLOCK_LABEL_367;
        }
        msg2 = new Msg(msg1);
        if (l1 > 0L)
        {
            byte2 = 2;
        } else
        {
            byte2 = 0;
        }
        flag2 = socketbase2.send(msg2, byte2);
        if (!flag2)
        {
            try
            {
                selector.close();
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception4)
            {
                return false;
            }
            return false;
        }
        if (l1 > 0L)
        {
            byte1 = 2;
        } else
        {
            byte1 = 0;
        }
        flag1 = socketbase.send(msg1, byte1);
        if (!flag1)
        {
            try
            {
                selector.close();
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception3)
            {
                return false;
            }
            return false;
        }
        if (l1 != 0L) goto _L5; else goto _L6
_L2:
        try
        {
            selector.close();
        }
        catch (Exception exception2) { }
        return true;
        exception;
        try
        {
            selector.close();
        }
        catch (Exception exception1) { }
        throw exception;
    }
}
