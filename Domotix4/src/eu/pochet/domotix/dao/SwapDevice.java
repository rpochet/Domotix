package eu.pochet.domotix.dao;

import java.util.ArrayList;
import java.util.List;

public class SwapDevice {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String productCode;
	
	private int address;
	
	private String name;
	
	private List<SwapRegister> swapRegisters = new ArrayList<SwapRegister>();
	
	private Room room;
	
	private int x;
	
	private int y;
	
	private int z;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getAddress() {
		return address;
	}
	
	public void setAddress(int address) {
		this.address = address;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<SwapRegister> getSwapRegisters() {
		return swapRegisters;
	}
	
	public void addSwapResister(SwapRegister swapRegister) {
		getSwapRegisters().add(swapRegister);
	}

	public Room getRoom() {
		return room;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public class SwapRegister {
		
		private String name;
		
		private int nb;
		
		private int valueAsInt;
		
		private byte[] value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getNb() {
			return nb;
		}

		public void setNb(int nb) {
			this.nb = nb;
		}

		public int getValueAsInt() {
			return valueAsInt;
		}

		public void setValueAsInt(int valueAsInt) {
			this.valueAsInt = valueAsInt;
		}

		public byte[] getValue() {
			return value;
		}

		public void setValue(byte[] value) {
			this.value = value;
		}
		
	}

	@Override
	public String toString() {
	    return new StringBuilder(this.name)
		    	.append(", Product Code: ")
		    	.append(this.productCode)
		    	.append(", Address: ")
		    	.append(this.address)
		    	.append(", Room: ")
		    	.append(this.getRoom().getName())
	    	.toString();
	}
	
}
