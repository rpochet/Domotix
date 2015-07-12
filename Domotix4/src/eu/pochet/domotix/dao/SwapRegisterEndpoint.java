package eu.pochet.domotix.dao;

import eu.pochet.android.Util;

public class SwapRegisterEndpoint {
	
	private SwapRegister swapRegister = null;

	private String name;
	
	private String type;
	
	private String dir;
	
	private String position;
	
	private String size;
	
	public SwapRegister getSwapRegister() {
		return swapRegister;
	}
	
	public void setSwapRegister(SwapRegister swapRegister) {
		this.swapRegister = swapRegister;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getDir() {
		return dir;
	}
	
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public String getPosition() {
		return position;
	}
	
	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	public byte[] getValue() {
		int positionInt = Integer.parseInt(this.position);
		int sizeInt = Integer.parseInt(this.size);
		byte[] res = new byte[sizeInt];
		System.arraycopy(getSwapRegister().getValue(), positionInt, res, 0, sizeInt);
		return res;
	}
	
	public int getValueAsInt() {
		return Util.getValueAsInt(getValue());
	}

	/*public void setValueAsInt(int valueAsInt) {
		value[0] = (byte) (valueAsInt / (256 * 256 * 256));
		value[1] = (byte) (valueAsInt / (256 * 256));
		value[2] = (byte) (valueAsInt / 256);
		value[3] = (byte) (valueAsInt % 256);
	}*/

	/**
	 * TODO Bit-length endpoint
	 * 
	 * @param value
	 */
	/*public void setValue(byte[] value) {
		int positionInt = Integer.parseInt(this.position);
		int sizeInt = Integer.parseInt(this.size);
		this.value = new byte[sizeInt];
		System.arraycopy(value, positionInt, this.value, 0, sizeInt);
	}

	public void setValueAsStr(String value) {
		if(value.length() % 2 == 1) {
			value = '0' + value;
		}
		this.value = Util.hexStringToByteArray(value);
	}*/
	
	@Override
	public String toString() {
	    return new StringBuilder(this.name)
		    	.append(", Type: ")
		    	.append(this.type)
		    	.append(", Value: ")
		    	.append(this.getValue())
		    	.append(", Value (int): ")
		    	.append(this.getValueAsInt())
	    	.toString();
	}

}