// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import android.os.Parcel;
import android.os.Parcelable;

public class Message
{
    public static final class Action extends Enum
        implements Parcelable
    {

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public Action createFromParcel(Parcel parcel)
            {
                return Action.values()[parcel.readInt()];
            }

            public volatile Object createFromParcel(Parcel parcel)
            {
                return createFromParcel(parcel);
            }

            public Action[] newArray(int i)
            {
                return new Action[i];
            }

            public volatile Object[] newArray(int i)
            {
                return newArray(i);
            }

        };
        private static final Action ENUM$VALUES[];
        public static final Action STATUS;
        public static final Action SWITCH_OFF_ALL;
        public static final Action TOGGLE;

        public static Action valueOf(String s)
        {
            return (Action)Enum.valueOf(eu/pochet/domotix/service/Message$Action, s);
        }

        public static Action[] values()
        {
            Action aaction[] = ENUM$VALUES;
            int i = aaction.length;
            Action aaction1[] = new Action[i];
            System.arraycopy(aaction, 0, aaction1, 0, i);
            return aaction1;
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
            STATUS = new Action("STATUS", 0);
            TOGGLE = new Action("TOGGLE", 1);
            SWITCH_OFF_ALL = new Action("SWITCH_OFF_ALL", 2);
            Action aaction[] = new Action[3];
            aaction[0] = STATUS;
            aaction[1] = TOGGLE;
            aaction[2] = SWITCH_OFF_ALL;
            ENUM$VALUES = aaction;
        }

        private Action(String s, int i)
        {
            super(s, i);
        }
    }

    public static final class Command extends Enum
        implements Parcelable
    {

        public static final Command CMD_EXEC_SCENARIO;
        public static final Command CMD_GET_CONFIG;
        public static final Command CMD_GET_SCENARIO;
        public static final Command CMD_LIGHT_STATUS;
        public static final Command CMD_OFF;
        public static final Command CMD_ON;
        public static final Command CMD_RESET;
        public static final Command CMD_RESET_LIGHT_STATUS;
        public static final Command CMD_RESTART_REGISTER;
        public static final Command CMD_RFU_05;
        public static final Command CMD_RFU_06;
        public static final Command CMD_RFU_07;
        public static final Command CMD_RFU_08;
        public static final Command CMD_RFU_09;
        public static final Command CMD_RFU_0a;
        public static final Command CMD_RFU_0b;
        public static final Command CMD_RFU_0c;
        public static final Command CMD_RFU_0d;
        public static final Command CMD_RFU_0e;
        public static final Command CMD_RFU_0f;
        public static final Command CMD_RFU_18;
        public static final Command CMD_RFU_19;
        public static final Command CMD_RFU_1a;
        public static final Command CMD_RFU_1b;
        public static final Command CMD_RFU_1c;
        public static final Command CMD_RFU_1d;
        public static final Command CMD_RFU_1e;
        public static final Command CMD_RFU_1f;
        public static final Command CMD_SENSOR_DATA;
        public static final Command CMD_SET_CONFIG;
        public static final Command CMD_SET_SCENARIO;
        public static final Command CMD_STATUS;
        public static final Command CMD_TOGGLE;
        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public Command createFromParcel(Parcel parcel)
            {
                return Command.values()[parcel.readInt()];
            }

            public volatile Object createFromParcel(Parcel parcel)
            {
                return createFromParcel(parcel);
            }

            public Command[] newArray(int i)
            {
                return new Command[i];
            }

            public volatile Object[] newArray(int i)
            {
                return newArray(i);
            }

        };
        private static final Command ENUM$VALUES[];

        public static Command valueOf(String s)
        {
            return (Command)Enum.valueOf(eu/pochet/domotix/service/Message$Command, s);
        }

        public static Command[] values()
        {
            Command acommand[] = ENUM$VALUES;
            int i = acommand.length;
            Command acommand1[] = new Command[i];
            System.arraycopy(acommand, 0, acommand1, 0, i);
            return acommand1;
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
            CMD_RESET = new Command("CMD_RESET", 0);
            CMD_STATUS = new Command("CMD_STATUS", 1);
            CMD_RESTART_REGISTER = new Command("CMD_RESTART_REGISTER", 2);
            CMD_GET_CONFIG = new Command("CMD_GET_CONFIG", 3);
            CMD_SET_CONFIG = new Command("CMD_SET_CONFIG", 4);
            CMD_RFU_05 = new Command("CMD_RFU_05", 5);
            CMD_RFU_06 = new Command("CMD_RFU_06", 6);
            CMD_RFU_07 = new Command("CMD_RFU_07", 7);
            CMD_RFU_08 = new Command("CMD_RFU_08", 8);
            CMD_RFU_09 = new Command("CMD_RFU_09", 9);
            CMD_RFU_0a = new Command("CMD_RFU_0a", 10);
            CMD_RFU_0b = new Command("CMD_RFU_0b", 11);
            CMD_RFU_0c = new Command("CMD_RFU_0c", 12);
            CMD_RFU_0d = new Command("CMD_RFU_0d", 13);
            CMD_RFU_0e = new Command("CMD_RFU_0e", 14);
            CMD_RFU_0f = new Command("CMD_RFU_0f", 15);
            CMD_TOGGLE = new Command("CMD_TOGGLE", 16);
            CMD_ON = new Command("CMD_ON", 17);
            CMD_OFF = new Command("CMD_OFF", 18);
            CMD_LIGHT_STATUS = new Command("CMD_LIGHT_STATUS", 19);
            CMD_RESET_LIGHT_STATUS = new Command("CMD_RESET_LIGHT_STATUS", 20);
            CMD_SET_SCENARIO = new Command("CMD_SET_SCENARIO", 21);
            CMD_GET_SCENARIO = new Command("CMD_GET_SCENARIO", 22);
            CMD_EXEC_SCENARIO = new Command("CMD_EXEC_SCENARIO", 23);
            CMD_RFU_18 = new Command("CMD_RFU_18", 24);
            CMD_RFU_19 = new Command("CMD_RFU_19", 25);
            CMD_RFU_1a = new Command("CMD_RFU_1a", 26);
            CMD_RFU_1b = new Command("CMD_RFU_1b", 27);
            CMD_RFU_1c = new Command("CMD_RFU_1c", 28);
            CMD_RFU_1d = new Command("CMD_RFU_1d", 29);
            CMD_RFU_1e = new Command("CMD_RFU_1e", 30);
            CMD_RFU_1f = new Command("CMD_RFU_1f", 31);
            CMD_SENSOR_DATA = new Command("CMD_SENSOR_DATA", 32);
            Command acommand[] = new Command[33];
            acommand[0] = CMD_RESET;
            acommand[1] = CMD_STATUS;
            acommand[2] = CMD_RESTART_REGISTER;
            acommand[3] = CMD_GET_CONFIG;
            acommand[4] = CMD_SET_CONFIG;
            acommand[5] = CMD_RFU_05;
            acommand[6] = CMD_RFU_06;
            acommand[7] = CMD_RFU_07;
            acommand[8] = CMD_RFU_08;
            acommand[9] = CMD_RFU_09;
            acommand[10] = CMD_RFU_0a;
            acommand[11] = CMD_RFU_0b;
            acommand[12] = CMD_RFU_0c;
            acommand[13] = CMD_RFU_0d;
            acommand[14] = CMD_RFU_0e;
            acommand[15] = CMD_RFU_0f;
            acommand[16] = CMD_TOGGLE;
            acommand[17] = CMD_ON;
            acommand[18] = CMD_OFF;
            acommand[19] = CMD_LIGHT_STATUS;
            acommand[20] = CMD_RESET_LIGHT_STATUS;
            acommand[21] = CMD_SET_SCENARIO;
            acommand[22] = CMD_GET_SCENARIO;
            acommand[23] = CMD_EXEC_SCENARIO;
            acommand[24] = CMD_RFU_18;
            acommand[25] = CMD_RFU_19;
            acommand[26] = CMD_RFU_1a;
            acommand[27] = CMD_RFU_1b;
            acommand[28] = CMD_RFU_1c;
            acommand[29] = CMD_RFU_1d;
            acommand[30] = CMD_RFU_1e;
            acommand[31] = CMD_RFU_1f;
            acommand[32] = CMD_SENSOR_DATA;
            ENUM$VALUES = acommand;
        }

        private Command(String s, int i)
        {
            super(s, i);
        }
    }


    public static final String COMMAND = "02";
    public static final String QUERY = "01";
    private String cardAddress;
    private String fromCardAddress;
    private String function;
    private String registerNb;
    private String registerValue;

    public Message(String s, String s1)
    {
        fromCardAddress = "01";
        cardAddress = null;
        function = "01";
        registerNb = null;
        registerValue = null;
        cardAddress = s;
        function = s1;
    }

    public String getData()
    {
        return (new StringBuilder()).append(cardAddress).append(fromCardAddress).append("0000").append(function).append(cardAddress).append(registerNb).append(registerValue).toString();
    }

    public void setRegisterNb(String s)
    {
        registerNb = s;
    }

    public void setRegisterValue(String s)
    {
        registerValue = s;
    }
}
