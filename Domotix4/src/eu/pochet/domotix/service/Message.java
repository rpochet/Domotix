package eu.pochet.domotix.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 0x70 Service Length CardNumber Command Data
 * 
 * @author romuald
 * 
 */
public class Message {
	
	

	
	
	
	
	
	
	private String fromCardAddress = "01";

	private String cardAddress = null;

	public static final String QUERY = "01";
	public static final String COMMAND = "02";
	private String function = QUERY;

	private String registerNb = null;

	private String registerValue = null;

	public Message(String cardAddress, String function) {
		this.cardAddress = cardAddress;
		this.function = function;
	}

	public void setRegisterNb(String registerNb) {
		this.registerNb = registerNb;
	}

	public void setRegisterValue(String registerValue) {
		this.registerValue = registerValue;
	}

	public String getData() {
		return new StringBuilder().append(cardAddress).append(fromCardAddress)
				.append("0000").append(function).append(cardAddress)
				.append(registerNb).append(registerValue).toString();
	}

	/*
	 * public static final String ACTION = "ACTION"; public static final String
	 * CARD_NB = "CARD_NB"; public static final String LIGHT_NB = "LIGHT_NB";
	 * public static final String LEVEL = "LEVEL";
	 */

	public enum Command implements Parcelable {
		CMD_RESET, CMD_STATUS, CMD_RESTART_REGISTER, CMD_GET_CONFIG, CMD_SET_CONFIG, CMD_RFU_05, CMD_RFU_06, CMD_RFU_07, CMD_RFU_08, CMD_RFU_09, CMD_RFU_0a, CMD_RFU_0b, CMD_RFU_0c, CMD_RFU_0d, CMD_RFU_0e, CMD_RFU_0f, CMD_TOGGLE, CMD_ON, CMD_OFF, CMD_LIGHT_STATUS, CMD_RESET_LIGHT_STATUS, CMD_SET_SCENARIO, CMD_GET_SCENARIO, CMD_EXEC_SCENARIO, CMD_RFU_18, CMD_RFU_19, CMD_RFU_1a, CMD_RFU_1b, CMD_RFU_1c, CMD_RFU_1d, CMD_RFU_1e, CMD_RFU_1f, CMD_SENSOR_DATA;

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(ordinal());
		}

		public static final Creator<Command> CREATOR = new Creator<Command>() {

			public Command createFromParcel(final Parcel source) {
				return Command.values()[source.readInt()];
			}

			public Command[] newArray(final int size) {
				return new Command[size];
			}
		};

	}

	public enum Action implements Parcelable {
		STATUS, TOGGLE, SWITCH_OFF_ALL;

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(ordinal());
		}

		public static final Creator<Action> CREATOR = new Creator<Action>() {

			public Action createFromParcel(final Parcel source) {
				return Action.values()[source.readInt()];
			}

			public Action[] newArray(final int size) {
				return new Action[size];
			}
		};

	}

}
