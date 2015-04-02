package eu.pochet.android;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

public class Util {

	public static final String SCREEN_SIZE_UNDEFINED = "undefined";
	public static final String SCREEN_SIZE_XLARGE = "xlarge";
	public static final String SCREEN_SIZE_LARGE = "large";
	public static final String SCREEN_SIZE_NORMAL = "normal";
	public static final String SCREEN_SIZE_SMALL = "small";

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getSizeName(Context context) {
		int screenLayout = context.getResources().getConfiguration().screenLayout;
		screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

		switch (screenLayout) {
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			return SCREEN_SIZE_SMALL;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			return SCREEN_SIZE_NORMAL;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return SCREEN_SIZE_LARGE;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			return SCREEN_SIZE_XLARGE;
		default:
			return SCREEN_SIZE_UNDEFINED;
		}
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static int getValueAsInt(byte[] value) {
		int valueAsInt = 0;
		switch (value.length) {
		case 4:
			valueAsInt = (value[0] > 0 ? value[0] : (256 + value[0])) * 256
					* 256 * 256 + (value[1] > 0 ? value[1] : (256 + value[1]))
					* 256 * 256 + (value[2] > 0 ? value[2] : (256 + value[2]))
					* 256 + (value[3] > 0 ? value[3] : (256 + value[3]));
			break;
		case 3:
			valueAsInt = (value[0] > 0 ? value[0] : (256 + value[0])) * 256
					* 256 + (value[1] > 0 ? value[1] : (256 + value[1])) * 256
					+ (value[2] > 0 ? value[2] : (256 + value[2]));
			break;
		case 2:
			valueAsInt = (value[0] > 0 ? value[0] : (256 + value[0])) * 256
					+ (value[1] > 0 ? value[1] : (256 + value[1]));
			break;
		case 1:
			valueAsInt = (value[0] > 0 ? value[0] : (256 + value[0]));
			break;
		default:
			break;
		}
		return valueAsInt;
	}

	/**
	 * Converting a string of hex character to bytes
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Converting a bytes array to string of hex character
	 * 
	 * @param b
	 * @return
	 */
	public static String byteArrayToHexString(byte[] b) {
		int len = b.length;
		String data = new String();

		for (int i = 0; i < len; i++) {
			data += Integer.toHexString((b[i] >> 4) & 0xf);
			data += Integer.toHexString(b[i] & 0xf);
		}
		return data;
	}

	public static Bundle newBundle(String key, String value) {
		Bundle bundle = new Bundle();
		bundle.putString(key, value);
		return bundle;
	}

}
