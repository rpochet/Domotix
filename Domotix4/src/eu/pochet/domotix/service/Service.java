// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;


public final class Service extends Enum
{

    public static final Service BRIDGE_SERVICE;
    public static final Service CARD_SERVICE;
    private static final Service ENUM$VALUES[];
    public static final Service LIGHT_CONTROLLER_SERVICE;
    public static final Service LIGHT_STATUS_SERVICE;
    public static final Service LIGHT_SWITCH_SERVICE;
    public static final Service SENSOR_RECEIVER_SERVICE;
    public static final Service SENSOR_SERVICE;
    private int id;

    private Service(String s, int i, int j)
    {
        super(s, i);
        id = j;
    }

    public static Service valueOf(String s)
    {
        return (Service)Enum.valueOf(eu/pochet/domotix/service/Service, s);
    }

    public static Service[] values()
    {
        Service aservice[] = ENUM$VALUES;
        int i = aservice.length;
        Service aservice1[] = new Service[i];
        System.arraycopy(aservice, 0, aservice1, 0, i);
        return aservice1;
    }

    public int getId()
    {
        return id;
    }

    static 
    {
        CARD_SERVICE = new Service("CARD_SERVICE", 0, 0);
        BRIDGE_SERVICE = new Service("BRIDGE_SERVICE", 1, 1);
        LIGHT_CONTROLLER_SERVICE = new Service("LIGHT_CONTROLLER_SERVICE", 2, 2);
        LIGHT_SWITCH_SERVICE = new Service("LIGHT_SWITCH_SERVICE", 3, 3);
        LIGHT_STATUS_SERVICE = new Service("LIGHT_STATUS_SERVICE", 4, 4);
        SENSOR_SERVICE = new Service("SENSOR_SERVICE", 5, 5);
        SENSOR_RECEIVER_SERVICE = new Service("SENSOR_RECEIVER_SERVICE", 6, 6);
        Service aservice[] = new Service[7];
        aservice[0] = CARD_SERVICE;
        aservice[1] = BRIDGE_SERVICE;
        aservice[2] = LIGHT_CONTROLLER_SERVICE;
        aservice[3] = LIGHT_SWITCH_SERVICE;
        aservice[4] = LIGHT_STATUS_SERVICE;
        aservice[5] = SENSOR_SERVICE;
        aservice[6] = SENSOR_RECEIVER_SERVICE;
        ENUM$VALUES = aservice;
    }
}
