// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


public final class Config extends Enum
{

    private static final Config $VALUES[];
    public static final Config clock_precision;
    public static final Config command_pipe_granularity;
    public static final Config in_batch_size;
    public static final Config inbound_poll_rate;
    public static final Config max_command_delay;
    public static final Config max_io_events;
    public static final Config max_wm_delta;
    public static final Config message_pipe_granularity;
    public static final Config out_batch_size;
    public static final Config pgm_max_tpdu;
    public static final Config signaler_port;
    private int value;

    private Config(String s, int i, int j)
    {
        super(s, i);
        value = j;
    }

    public static Config valueOf(String s)
    {
        return (Config)Enum.valueOf(zmq/Config, s);
    }

    public static Config[] values()
    {
        return (Config[])$VALUES.clone();
    }

    public int getValue()
    {
        return value;
    }

    static 
    {
        message_pipe_granularity = new Config("message_pipe_granularity", 0, 256);
        command_pipe_granularity = new Config("command_pipe_granularity", 1, 16);
        inbound_poll_rate = new Config("inbound_poll_rate", 2, 100);
        in_batch_size = new Config("in_batch_size", 3, 8192);
        out_batch_size = new Config("out_batch_size", 4, 8192);
        max_wm_delta = new Config("max_wm_delta", 5, 1024);
        max_io_events = new Config("max_io_events", 6, 256);
        max_command_delay = new Config("max_command_delay", 7, 0x2dc6c0);
        clock_precision = new Config("clock_precision", 8, 0xf4240);
        pgm_max_tpdu = new Config("pgm_max_tpdu", 9, 1500);
        signaler_port = new Config("signaler_port", 10, 5905);
        Config aconfig[] = new Config[11];
        aconfig[0] = message_pipe_granularity;
        aconfig[1] = command_pipe_granularity;
        aconfig[2] = inbound_poll_rate;
        aconfig[3] = in_batch_size;
        aconfig[4] = out_batch_size;
        aconfig[5] = max_wm_delta;
        aconfig[6] = max_io_events;
        aconfig[7] = max_command_delay;
        aconfig[8] = clock_precision;
        aconfig[9] = pgm_max_tpdu;
        aconfig[10] = signaler_port;
        $VALUES = aconfig;
    }
}
