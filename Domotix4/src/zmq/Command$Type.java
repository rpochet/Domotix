// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Command

public static final class nit> extends Enum
{

    private static final .VALUES $VALUES[];
    public static final .VALUES activate_read;
    public static final .VALUES activate_write;
    public static final .VALUES attach;
    public static final .VALUES bind;
    public static final .VALUES done;
    public static final .VALUES hiccup;
    public static final .VALUES own;
    public static final .VALUES pipe_term;
    public static final .VALUES pipe_term_ack;
    public static final .VALUES plug;
    public static final .VALUES reap;
    public static final .VALUES reaped;
    public static final .VALUES stop;
    public static final .VALUES term;
    public static final .VALUES term_ack;
    public static final .VALUES term_req;

    public static nit> valueOf(String s)
    {
        return (nit>)Enum.valueOf(zmq/Command$Type, s);
    }

    public static lueOf[] values()
    {
        return (lueOf[])$VALUES.clone();
    }

    static 
    {
        stop = new <init>("stop", 0);
        plug = new <init>("plug", 1);
        own = new <init>("own", 2);
        attach = new <init>("attach", 3);
        bind = new <init>("bind", 4);
        activate_read = new <init>("activate_read", 5);
        activate_write = new <init>("activate_write", 6);
        hiccup = new <init>("hiccup", 7);
        pipe_term = new <init>("pipe_term", 8);
        pipe_term_ack = new <init>("pipe_term_ack", 9);
        term_req = new <init>("term_req", 10);
        term = new <init>("term", 11);
        term_ack = new <init>("term_ack", 12);
        reap = new <init>("reap", 13);
        reaped = new <init>("reaped", 14);
        done = new <init>("done", 15);
        e_3B_.clone aclone[] = new <init>[16];
        aclone[0] = stop;
        aclone[1] = plug;
        aclone[2] = own;
        aclone[3] = attach;
        aclone[4] = bind;
        aclone[5] = activate_read;
        aclone[6] = activate_write;
        aclone[7] = hiccup;
        aclone[8] = pipe_term;
        aclone[9] = pipe_term_ack;
        aclone[10] = term_req;
        aclone[11] = term;
        aclone[12] = term_ack;
        aclone[13] = reap;
        aclone[14] = reaped;
        aclone[15] = done;
        $VALUES = aclone;
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
