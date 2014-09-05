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
	
	private Room room;
	
	private int x;
	
	private int y;
	
	private int z;
	
	private String type;

	private int status;
	
	private int outputNb;
	
	private String cardAddress;
	
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
	
	public String getCardAddress() {
		return cardAddress;
	}
	
	public void setCardAddress(String cardAddress) {
		this.cardAddress = cardAddress;
	}
	
	/*public int getLevelX() {
		return getRoom().getLevel().getX() + getRoom().getX() + getX();
	}
	
	public int getLevelY() {
		return getRoom().getLevel().getY() + getRoom().getY() + getY();
	}*/
	
	@Override
	public String toString()
	{
	    return new StringBuilder().append(this.name).append(", Status: ").append(this.status).append("\nRoom: ").append(this.getRoom().getName()).toString();
	}
	
}
