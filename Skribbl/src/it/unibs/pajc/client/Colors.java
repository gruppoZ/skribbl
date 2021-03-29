package it.unibs.pajc.client;

public enum Colors {
	
	RED("RED"), GREEN("GREEN"), BLACK("BLACK"), ORANGE("ORANGE"), PINK("PINK");
	
	private String value;
	
	private Colors(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
