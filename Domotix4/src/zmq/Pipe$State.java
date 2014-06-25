// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Pipe

static final class g extends Enum
{

    private static final .VALUES $VALUES[];
    public static final .VALUES active;
    public static final .VALUES delimited;
    public static final .VALUES double_terminated;
    public static final ed pending;
    public static final ed terminated;
    public static final ed terminating;

    public static g valueOf(String s)
    {
        return (g)Enum.valueOf(zmq/Pipe$State, s);
    }

    public static g[] values()
    {
        return (g[])$VALUES.clone();
    }

    static 
    {
        active = new <init>("active", 0);
        delimited = new <init>("delimited", 1);
        pending = new <init>("pending", 2);
        terminating = new <init>("terminating", 3);
        terminated = new <init>("terminated", 4);
        double_terminated = new <init>("double_terminated", 5);
        g ag[] = new g[6];
        ag[0] = active;
        ag[1] = delimited;
        ag[2] = pending;
        ag[3] = terminating;
        ag[4] = terminated;
        ag[5] = double_terminated;
        $VALUES = ag;
    }

    private g(String s, int i)
    {
        super(s, i);
    }
}
