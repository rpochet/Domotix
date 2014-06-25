package eu.pochet.android;

public class Util {

	public static String byteArrayToHexString(byte[] values) {
		int len = values.length;
		String data = new String();

		for (int i = 0; i < len; i++)
		{
			data += Integer.toHexString((values[i] >> 4) & 0xf);
			data += Integer.toHexString(values[i] & 0xf);
		}
		return data;
	}
}
