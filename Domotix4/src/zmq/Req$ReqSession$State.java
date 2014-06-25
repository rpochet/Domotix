// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Req

static final class  extends Enum
{

    private static final .VALUES $VALUES[];
    public static final .VALUES body;
    public static final .VALUES bottom;
    public static final .VALUES identity;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(zmq/Req$ReqSession$State, s);
    }

    public static [] values()
    {
        return ([])$VALUES.clone();
    }

    static 
    {
        identity = new <init>("identity", 0);
        bottom = new <init>("bottom", 1);
        body = new <init>("body", 2);
        e_3B_.clone aclone[] = new <init>[3];
        aclone[0] = identity;
        aclone[1] = bottom;
        aclone[2] = body;
        $VALUES = aclone;
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
