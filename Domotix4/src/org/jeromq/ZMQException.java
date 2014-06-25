// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;


public class ZMQException extends RuntimeException
{
    public static class CtxTerminated extends ZMQException
    {

        private static final long serialVersionUID = 0x6989e53f3491f7ccL;

        public CtxTerminated()
        {
            super(0x9523dfd);
        }
    }

    public static class IOException extends RuntimeException
    {

        private static final long serialVersionUID = 0x75222a89e40460ecL;

        public IOException(java.io.IOException ioexception)
        {
            super(ioexception);
        }
    }


    private static final long serialVersionUID = 0x52ac57ee643a8155L;
    private final long code;

    public ZMQException(int i)
    {
        super((new StringBuilder()).append("Errno ").append(i).toString());
        code = i;
    }

    public long getErrorCode()
    {
        return code;
    }
}
