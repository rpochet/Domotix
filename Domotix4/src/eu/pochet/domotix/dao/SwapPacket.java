package eu.pochet.domotix.dao;

import java.io.Serializable;


public class SwapPacket implements Serializable {

	private int dest;

	private int source;

	private int func;
	
	private int regAddress;

	private int regId;

	private byte[] regValue;

	public int getDest() {
		return dest;
	}

	public void setDest(int dest) {
		this.dest = dest;
		setRegAddress(dest);
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getFunc() {
		return func;
	}

	public void setFunc(int func) {
		this.func = func;
	}

	public int getRegAddress() {
		return regAddress;
	}
	
	public void setRegAddress(int regAddress) {
		this.regAddress = regAddress;		
	}

	public int getRegId() {
		return regId;
	}

	public void setRegId(int regId) {
		this.regId = regId;
	}

	public byte[] getRegValue() {
		return regValue;
	}

	public void setRegValue(byte[] value) {
		this.regValue = value;
	}

	public int getRegValueAsInt() {
		int valueLength = this.regValue.length;
		int res = this.regValue[0];
		if(valueLength > 1) {
			res += this.regValue[1] * 256;
			if(valueLength > 2) {
				res += this.regValue[2] * 256 * 256;
				if(valueLength > 3) {
					res += this.regValue[3] * 256 * 256 * 256;					
				}	
			}	
		}
		return res; 
	}
	
	public byte[] toByteArray() {
		int offset = 0;
		byte[] res = new byte[7 + (this.regValue == null ? 0 : this.regValue.length)];
		res[offset++] = (byte) this.dest;
		res[offset++] = 0;
		res[offset++] = 0;
		res[offset++] = 0;
		res[offset++] = (byte) this.func;
		res[offset++] = (byte) this.regAddress;
		res[offset++] = (byte) this.regId;
		if(this.regValue != null) {
			for (int i = 0; i < regValue.length; i++) {
				res[offset + i] = regValue[i];
			}
		}
		return res;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("dest: ").append(this.dest)
			.append(", func: ").append(this.func)
			.append(", regAddress: ").append(this.regAddress)
			.append(", regId: ").append(this.regId)
			.append(", regValue: ").append(this.regValue)
		.toString();
	}
	
}
