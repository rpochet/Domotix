package eu.pochet.domotix.dao;

import java.util.ArrayList;
import java.util.List;

public class SwapDevice {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int address;
	
	private String product;
	
	private List<SwapRegister> regularRegisters = new ArrayList<SwapRegister>();
	
	private Location location = new Location();
	
	public int getAddress() {
		return address;
	}
	
	public void setAddress(int address) {
		this.address = address;
	}
	
	public String getProduct() {
		return product;
	}
	
	public void setProduct(String product) {
		this.product = product;
	}
	
	public List<SwapRegister> getSwapRegisters() {
		return regularRegisters;
	}
	
	public void addSwapResister(SwapRegister swapRegister) {
		getSwapRegisters().add(swapRegister);
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public SwapRegister getSwapRegisterById(int regId) {
		for (SwapRegister swapRegister : this.regularRegisters) {
			if(swapRegister.getId() == regId) {
				return swapRegister;
			}
		}
		return null;
	}
	
}
