package eu.pochet.domotix.dao;

import java.io.Serializable;

public class Light implements Serializable 
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String name;
	
	private Location location = new Location();
	
	private String type;

	private int status;
	
	private int outputNb;
	
	private int swapDeviceAddress;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOutputNb() {
		return outputNb;
	}

	public void setOutputNb(int outputNb) {
		this.outputNb = outputNb;
	}
	
	public int getSwapDeviceAddress() {
		return swapDeviceAddress;
	}
	
	public void setSwapDeviceAddress(int swapDeviceAddress) {
		this.swapDeviceAddress = swapDeviceAddress;
	}
	
	@Override
	public String toString() {
	    return new StringBuilder()
	    	.append(this.name)
	    	.append(", Output Nb: ").append(this.outputNb)
	    	.append(", Status: ").append(this.status)
	    	.append(", Location: ").append(this.getLocation())
	    	.toString();
	}
	
}
