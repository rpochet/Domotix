// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import android.os.Parcel;
import android.os.Parcelable;

// Referenced classes of package eu.pochet.domotix.service:
//            Message

public static final class _cls1 extends Enum
    implements Parcelable
{

    public static final ENUM.VALUES CMD_EXEC_SCENARIO;
    public static final ENUM.VALUES CMD_GET_CONFIG;
    public static final ENUM.VALUES CMD_GET_SCENARIO;
    public static final ENUM.VALUES CMD_LIGHT_STATUS;
    public static final ENUM.VALUES CMD_OFF;
    public static final ENUM.VALUES CMD_ON;
    public static final ENUM.VALUES CMD_RESET;
    public static final ENUM.VALUES CMD_RESET_LIGHT_STATUS;
    public static final ENUM.VALUES CMD_RESTART_REGISTER;
    public static final ENUM.VALUES CMD_RFU_05;
    public static final ENUM.VALUES CMD_RFU_06;
    public static final ENUM.VALUES CMD_RFU_07;
    public static final ENUM.VALUES CMD_RFU_08;
    public static final ENUM.VALUES CMD_RFU_09;
    public static final ENUM.VALUES CMD_RFU_0a;
    public static final ENUM.VALUES CMD_RFU_0b;
    public static final ENUM.VALUES CMD_RFU_0c;
    public static final ENUM.VALUES CMD_RFU_0d;
    public static final ENUM.VALUES CMD_RFU_0e;
    public static final ENUM.VALUES CMD_RFU_0f;
    public static final ENUM.VALUES CMD_RFU_18;
    public static final ENUM.VALUES CMD_RFU_19;
    public static final ENUM.VALUES CMD_RFU_1a;
    public static final ENUM.VALUES CMD_RFU_1b;
    public static final ENUM.VALUES CMD_RFU_1c;
    public static final ENUM.VALUES CMD_RFU_1d;
    public static final ENUM.VALUES CMD_RFU_1e;
    public static final ENUM.VALUES CMD_RFU_1f;
    public static final ENUM.VALUES CMD_SENSOR_DATA;
    public static final ENUM.VALUES CMD_SET_CONFIG;
    public static final ENUM.VALUES CMD_SET_SCENARIO;
    public static final ENUM.VALUES CMD_STATUS;
    public static final ENUM.VALUES CMD_TOGGLE;
    public static final android.os.or CREATOR = new android.os.Parcelable.Creator() {

        public Message.Command createFromParcel(Parcel parcel)
        {
            return Message.Command.values()[parcel.readInt()];
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public Message.Command[] newArray(int i)
        {
            return new Message.Command[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    private static final _cls1.newArray ENUM$VALUES[];

    public static _cls1 valueOf(String s)
    {
        return (_cls1)Enum.valueOf(eu/pochet/domotix/service/Message$Command, s);
    }

    public static _cls1[] values()
    {
        _cls1 a_lcls1[] = ENUM$VALUES;
        int i = a_lcls1.length;
        _cls1 a_lcls1_1[] = new ENUM.VALUES[i];
        System.arraycopy(a_lcls1, 0, a_lcls1_1, 0, i);
        return a_lcls1_1;
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(ordinal());
    }

    static 
    {
        CMD_RESET = new <init>("CMD_RESET", 0);
        CMD_STATUS = new <init>("CMD_STATUS", 1);
        CMD_RESTART_REGISTER = new <init>("CMD_RESTART_REGISTER", 2);
        CMD_GET_CONFIG = new <init>("CMD_GET_CONFIG", 3);
        CMD_SET_CONFIG = new <init>("CMD_SET_CONFIG", 4);
        CMD_RFU_05 = new <init>("CMD_RFU_05", 5);
        CMD_RFU_06 = new <init>("CMD_RFU_06", 6);
        CMD_RFU_07 = new <init>("CMD_RFU_07", 7);
        CMD_RFU_08 = new <init>("CMD_RFU_08", 8);
        CMD_RFU_09 = new <init>("CMD_RFU_09", 9);
        CMD_RFU_0a = new <init>("CMD_RFU_0a", 10);
        CMD_RFU_0b = new <init>("CMD_RFU_0b", 11);
        CMD_RFU_0c = new <init>("CMD_RFU_0c", 12);
        CMD_RFU_0d = new <init>("CMD_RFU_0d", 13);
        CMD_RFU_0e = new <init>("CMD_RFU_0e", 14);
        CMD_RFU_0f = new <init>("CMD_RFU_0f", 15);
        CMD_TOGGLE = new <init>("CMD_TOGGLE", 16);
        CMD_ON = new <init>("CMD_ON", 17);
        CMD_OFF = new <init>("CMD_OFF", 18);
        CMD_LIGHT_STATUS = new <init>("CMD_LIGHT_STATUS", 19);
        CMD_RESET_LIGHT_STATUS = new <init>("CMD_RESET_LIGHT_STATUS", 20);
        CMD_SET_SCENARIO = new <init>("CMD_SET_SCENARIO", 21);
        CMD_GET_SCENARIO = new <init>("CMD_GET_SCENARIO", 22);
        CMD_EXEC_SCENARIO = new <init>("CMD_EXEC_SCENARIO", 23);
        CMD_RFU_18 = new <init>("CMD_RFU_18", 24);
        CMD_RFU_19 = new <init>("CMD_RFU_19", 25);
        CMD_RFU_1a = new <init>("CMD_RFU_1a", 26);
        CMD_RFU_1b = new <init>("CMD_RFU_1b", 27);
        CMD_RFU_1c = new <init>("CMD_RFU_1c", 28);
        CMD_RFU_1d = new <init>("CMD_RFU_1d", 29);
        CMD_RFU_1e = new <init>("CMD_RFU_1e", 30);
        CMD_RFU_1f = new <init>("CMD_RFU_1f", 31);
        CMD_SENSOR_DATA = new <init>("CMD_SENSOR_DATA", 32);
        ordinal aordinal[] = new <init>[33];
        aordinal[0] = CMD_RESET;
        aordinal[1] = CMD_STATUS;
        aordinal[2] = CMD_RESTART_REGISTER;
        aordinal[3] = CMD_GET_CONFIG;
        aordinal[4] = CMD_SET_CONFIG;
        aordinal[5] = CMD_RFU_05;
        aordinal[6] = CMD_RFU_06;
        aordinal[7] = CMD_RFU_07;
        aordinal[8] = CMD_RFU_08;
        aordinal[9] = CMD_RFU_09;
        aordinal[10] = CMD_RFU_0a;
        aordinal[11] = CMD_RFU_0b;
        aordinal[12] = CMD_RFU_0c;
        aordinal[13] = CMD_RFU_0d;
        aordinal[14] = CMD_RFU_0e;
        aordinal[15] = CMD_RFU_0f;
        aordinal[16] = CMD_TOGGLE;
        aordinal[17] = CMD_ON;
        aordinal[18] = CMD_OFF;
        aordinal[19] = CMD_LIGHT_STATUS;
        aordinal[20] = CMD_RESET_LIGHT_STATUS;
        aordinal[21] = CMD_SET_SCENARIO;
        aordinal[22] = CMD_GET_SCENARIO;
        aordinal[23] = CMD_EXEC_SCENARIO;
        aordinal[24] = CMD_RFU_18;
        aordinal[25] = CMD_RFU_19;
        aordinal[26] = CMD_RFU_1a;
        aordinal[27] = CMD_RFU_1b;
        aordinal[28] = CMD_RFU_1c;
        aordinal[29] = CMD_RFU_1d;
        aordinal[30] = CMD_RFU_1e;
        aordinal[31] = CMD_RFU_1f;
        aordinal[32] = CMD_SENSOR_DATA;
        ENUM$VALUES = aordinal;
    }

    private _cls1(String s, int i)
    {
        super(s, i);
    }
}
