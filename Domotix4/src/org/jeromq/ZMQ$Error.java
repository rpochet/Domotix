// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;


// Referenced classes of package org.jeromq:
//            ZMQ

public static final class code extends Enum
{

    private static final .VALUES $VALUES[];
    public static final .VALUES EADDRINUSE;
    public static final .VALUES EADDRNOTAVAIL;
    public static final .VALUES ECONNREFUSED;
    public static final .VALUES EFSM;
    public static final .VALUES EINPROGRESS;
    public static final .VALUES EMTHREAD;
    public static final .VALUES ENETDOWN;
    public static final .VALUES ENOBUFS;
    public static final .VALUES ENOCOMPATPROTO;
    public static final .VALUES ENOTSUP;
    public static final .VALUES EPROTONOSUPPORT;
    public static final .VALUES ETERM;
    private final long code;

    public static code findByCode(int i)
    {
        code acode[] = (code[])org/jeromq/ZMQ$Error.getEnumConstants();
        int j = acode.length;
        for (int k = 0; k < j; k++)
        {
            code code1 = acode[k];
            if (code1.getCode() == (long)i)
            {
                return code1;
            }
        }

        throw new IllegalArgumentException((new StringBuilder()).append("Unknown ").append(org/jeromq/ZMQ$Error.getName()).append(" enum code:").append(i).toString());
    }

    public static er.toString valueOf(String s)
    {
        return (er.toString)Enum.valueOf(org/jeromq/ZMQ$Error, s);
    }

    public static f[] values()
    {
        return (f[])$VALUES.clone();
    }

    public long getCode()
    {
        return code;
    }

    static 
    {
        ENOTSUP = new <init>("ENOTSUP", 0, 45L);
        EPROTONOSUPPORT = new <init>("EPROTONOSUPPORT", 1, 43L);
        ENOBUFS = new <init>("ENOBUFS", 2, 55L);
        ENETDOWN = new <init>("ENETDOWN", 3, 50L);
        EADDRINUSE = new <init>("EADDRINUSE", 4, 48L);
        EADDRNOTAVAIL = new <init>("EADDRNOTAVAIL", 5, 49L);
        ECONNREFUSED = new <init>("ECONNREFUSED", 6, 61L);
        EINPROGRESS = new <init>("EINPROGRESS", 7, 36L);
        EMTHREAD = new <init>("EMTHREAD", 8, 0x9523dfeL);
        EFSM = new <init>("EFSM", 9, 0x9523dfbL);
        ENOCOMPATPROTO = new <init>("ENOCOMPATPROTO", 10, 0x9523dfcL);
        ETERM = new <init>("ETERM", 11, 0x9523dfdL);
        code acode[] = new <init>[12];
        acode[0] = ENOTSUP;
        acode[1] = EPROTONOSUPPORT;
        acode[2] = ENOBUFS;
        acode[3] = ENETDOWN;
        acode[4] = EADDRINUSE;
        acode[5] = EADDRNOTAVAIL;
        acode[6] = ECONNREFUSED;
        acode[7] = EINPROGRESS;
        acode[8] = EMTHREAD;
        acode[9] = EFSM;
        acode[10] = ENOCOMPATPROTO;
        acode[11] = ETERM;
        $VALUES = acode;
    }

    private er(String s, int i, long l)
    {
        super(s, i);
        code = l;
    }
}
