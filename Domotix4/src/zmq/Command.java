// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            ZObject

public class Command
{
    public static final class Type extends Enum
    {

        private static final Type $VALUES[];
        public static final Type activate_read;
        public static final Type activate_write;
        public static final Type attach;
        public static final Type bind;
        public static final Type done;
        public static final Type hiccup;
        public static final Type own;
        public static final Type pipe_term;
        public static final Type pipe_term_ack;
        public static final Type plug;
        public static final Type reap;
        public static final Type reaped;
        public static final Type stop;
        public static final Type term;
        public static final Type term_ack;
        public static final Type term_req;

        public static Type valueOf(String s)
        {
            return (Type)Enum.valueOf(zmq/Command$Type, s);
        }

        public static Type[] values()
        {
            return (Type[])$VALUES.clone();
        }

        static 
        {
            stop = new Type("stop", 0);
            plug = new Type("plug", 1);
            own = new Type("own", 2);
            attach = new Type("attach", 3);
            bind = new Type("bind", 4);
            activate_read = new Type("activate_read", 5);
            activate_write = new Type("activate_write", 6);
            hiccup = new Type("hiccup", 7);
            pipe_term = new Type("pipe_term", 8);
            pipe_term_ack = new Type("pipe_term_ack", 9);
            term_req = new Type("term_req", 10);
            term = new Type("term", 11);
            term_ack = new Type("term_ack", 12);
            reap = new Type("reap", 13);
            reaped = new Type("reaped", 14);
            done = new Type("done", 15);
            Type atype[] = new Type[16];
            atype[0] = stop;
            atype[1] = plug;
            atype[2] = own;
            atype[3] = attach;
            atype[4] = bind;
            atype[5] = activate_read;
            atype[6] = activate_write;
            atype[7] = hiccup;
            atype[8] = pipe_term;
            atype[9] = pipe_term_ack;
            atype[10] = term_req;
            atype[11] = term;
            atype[12] = term_ack;
            atype[13] = reap;
            atype[14] = reaped;
            atype[15] = done;
            $VALUES = atype;
        }

        private Type(String s, int i)
        {
            super(s, i);
        }
    }


    Object arg;
    private ZObject destination;
    private Type type;

    public Command()
    {
    }

    public Command(ZObject zobject, Type type1)
    {
        this(zobject, type1, null);
    }

    public Command(ZObject zobject, Type type1, Object obj)
    {
        destination = zobject;
        type = type1;
        arg = obj;
    }

    public ZObject destination()
    {
        return destination;
    }

    public String toString()
    {
        return (new StringBuilder()).append(super.toString()).append("[").append(type).append(", ").append(destination).append("]").toString();
    }

    public Type type()
    {
        return type;
    }
}
