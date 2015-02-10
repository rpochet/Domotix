package eu.pochet.domotix.dao;

import java.util.ArrayList;
import java.util.List;

public class SwapRegister {
	
	private String name;
	
	private int id;
	
	private byte[] value;
	
	private List<SwapRegisterEndpoint> endpoints = new ArrayList<SwapRegisterEndpoint>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public List<SwapRegisterEndpoint> getSwapRegisterEndpoints() {
		return endpoints;
	}
	
	public void setSwapRegisterEndpoints(List<SwapRegisterEndpoint> swapRegisterEndpoints) {
		this.endpoints = swapRegisterEndpoints;
	}

	public void addSwapResisterEndpoint(SwapRegisterEndpoint swapRegisterEndpoint) {
		swapRegisterEndpoint.setSwapRegister(this);
		getSwapRegisterEndpoints().add(swapRegisterEndpoint);
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
	    return new StringBuilder(this.name)
		    	.append(", RegId: ")
		    	.append(this.id)
	    	.toString();
	}

}