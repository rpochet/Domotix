// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.Selector;
import java.util.List;
import zmq.Msg;
import zmq.PollItem;
import zmq.SocketBase;
import zmq.ZMQ;

public class ZDevice
{

    public ZDevice()
    {
    }

    public static boolean addressDevice(ZMQ.Socket socket, ZMQ.Socket socket1, List list)
    {
        SocketBase socketbase;
        SocketBase socketbase1;
        boolean flag;
        boolean flag1;
        boolean flag2;
        int i;
        PollItem apollitem[];
        socketbase = socket.base();
        socketbase1 = socket1.base();
        flag = true;
        flag1 = false;
        flag2 = false;
        i = list.size();
        apollitem = new PollItem[2];
        if (socketbase.getsockopt(16) != 6 || socketbase1.getsockopt(16) != 6)
        {
            throw new IllegalArgumentException("Both router socket is required");
        }
        PollItem pollitem = new PollItem(socketbase, 1);
        apollitem[0] = pollitem;
        PollItem pollitem1 = new PollItem(socketbase1, 1);
        apollitem[1] = pollitem1;
        Selector selector;
        try
        {
            selector = Selector.open();
        }
        catch (IOException ioexception)
        {
            zmq.ZError.IOException ioexception1 = new zmq.ZError.IOException(ioexception);
            throw ioexception1;
        }
_L4:
        if (!flag || ZMQ.zmq_poll(selector, apollitem, -1L) < 0)
        {
            Msg msg;
            boolean flag3;
            byte byte0;
            Msg msg1;
            boolean flag4;
            byte byte1;
            int j;
            byte abyte0[];
            Msg msg2;
            try
            {
                selector.close();
            }
            catch (Exception exception)
            {
                return flag;
            }
            return flag;
        }
        if (!apollitem[0].isReadable()) goto _L2; else goto _L1
_L1:
        msg1 = socketbase.recv(0);
        if (msg1 == null)
        {
            flag = false;
        } else
        {
label0:
            {
                if (flag1)
                {
                    break MISSING_BLOCK_LABEL_299;
                }
                j = msg1.data()[0];
                if (j < 0)
                {
                    j &= 0xff;
                }
                abyte0 = (byte[])list.get(j % i);
                msg2 = new Msg(abyte0);
                flag = socketbase1.send(msg2, 2);
                if (flag)
                {
                    break label0;
                }
                System.out.println("send 0 failed");
            }
        }
_L2:
        if (!flag || !apollitem[1].isReadable()) goto _L4; else goto _L3
_L3:
        msg = socketbase1.recv(0);
        if (msg != null)
        {
            break MISSING_BLOCK_LABEL_358;
        }
        System.out.println("recv failed");
        flag = false;
          goto _L4
        flag1 = true;
        flag4 = msg1.has_more();
        if (flag4)
        {
            byte1 = 2;
        } else
        {
            byte1 = 0;
        }
        flag = socketbase1.send(msg1, byte1);
        if (flag)
        {
            continue; /* Loop/switch isn't completed */
        }
        System.out.println("send failed");
          goto _L2
        if (flag4) goto _L1; else goto _L5
_L5:
        flag1 = false;
          goto _L2
label1:
        {
            if (flag2)
            {
                break label1;
            }
            flag2 = true;
        }
          goto _L3
        flag3 = msg.has_more();
        if (flag3)
        {
            byte0 = 2;
        } else
        {
            byte0 = 0;
        }
        flag = socketbase.send(msg, byte0);
        if (flag)
        {
            continue; /* Loop/switch isn't completed */
        }
        System.out.println("send 2 failed");
          goto _L4
        if (flag3) goto _L3; else goto _L6
_L6:
        flag2 = false;
          goto _L4
    }

    public static boolean loadBalanceDevice(ZMQ.Socket socket, ZMQ.Socket socket1, List list)
    {
        SocketBase socketbase;
        SocketBase socketbase1;
        boolean flag;
        boolean flag1;
        boolean flag2;
        int i;
        PollItem apollitem[];
        socketbase = socket.base();
        socketbase1 = socket1.base();
        flag = true;
        flag1 = false;
        flag2 = false;
        i = list.size();
        apollitem = new PollItem[2];
        if (socketbase.getsockopt(16) != 6 || socketbase1.getsockopt(16) != 6)
        {
            throw new IllegalArgumentException("Both router socket is required");
        }
        PollItem pollitem = new PollItem(socketbase, 1);
        apollitem[0] = pollitem;
        PollItem pollitem1 = new PollItem(socketbase1, 1);
        apollitem[1] = pollitem1;
        Selector selector;
        try
        {
            selector = Selector.open();
        }
        catch (IOException ioexception)
        {
            zmq.ZError.IOException ioexception1 = new zmq.ZError.IOException(ioexception);
            throw ioexception1;
        }
_L12:
label0:
        {
            if (flag)
            {
                if (i == 0)
                {
                    apollitem[0].interestOps(0);
                } else
                {
                    apollitem[0].interestOps(1);
                }
                if (ZMQ.zmq_poll(selector, apollitem, -1L) >= 0)
                {
                    break label0;
                }
            }
            Msg msg;
            boolean flag3;
            byte byte0;
            int j;
            Msg msg1;
            boolean flag4;
            byte byte1;
            byte abyte0[];
            Msg msg2;
            try
            {
                selector.close();
            }
            catch (Exception exception)
            {
                return flag;
            }
            return flag;
        }
        if (!apollitem[0].isReadable()) goto _L2; else goto _L1
_L1:
        msg1 = socketbase.recv(0);
        if (msg1 != null) goto _L4; else goto _L3
_L3:
        flag = false;
_L2:
        if (!flag || !apollitem[1].isReadable())
        {
            continue; /* Loop/switch isn't completed */
        }
          goto _L5
_L4:
        if (flag1)
        {
            break MISSING_BLOCK_LABEL_297;
        }
        i--;
        abyte0 = (byte[])list.get(i);
        msg2 = new Msg(abyte0);
        flag = socketbase1.send(msg2, 2);
        if (flag) goto _L7; else goto _L6
_L6:
        if (socketbase1.errno() != 65) goto _L2; else goto _L1
_L7:
        flag1 = true;
        flag4 = msg1.has_more();
        if (flag4)
        {
            byte1 = 2;
        } else
        {
            byte1 = 0;
        }
        flag = socketbase1.send(msg1, byte1);
        if (!flag) goto _L2; else goto _L8
_L8:
        if (flag4) goto _L1; else goto _L9
_L9:
        flag1 = false;
          goto _L2
_L5:
        do
        {
            msg = socketbase1.recv(0);
            if (msg == null)
            {
                flag = false;
                continue; /* Loop/switch isn't completed */
            }
label1:
            {
                if (flag2)
                {
                    break label1;
                }
                j = i + 1;
                list.set(i, msg.data());
                flag2 = true;
                i = j;
            }
        } while (true);
        flag3 = msg.has_more();
        if (flag3)
        {
            byte0 = 2;
        } else
        {
            byte0 = 0;
        }
        flag = socketbase.send(msg, byte0);
        if (!flag)
        {
            continue; /* Loop/switch isn't completed */
        }
        if (flag3) goto _L5; else goto _L10
_L10:
        flag2 = false;
        if (true) goto _L12; else goto _L11
_L11:
    }
}
