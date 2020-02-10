package me.old.li;

public enum InputType {

	TEXT(".+"), INTEGER_INCLUDE_ZERO("[0-9]+"), DOUBLE_INCLUDE_ZERO("[0-9]+(\\.[0-9]+)?"),
	INTEGER_NOT_INCLUDE_ZERO("[1-9][0-9]*"), DOUBLE_NOT_INCLUDE_ZERO("[1-9][0-9]*(\\.[0-9]+)?|0\\.(?!0+$)[0-9]+"),
	DATE("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"), INTEGER_AMOUNT("([0-9]+)|([0-9]+-[0-9]+)"),
	DOUBLE_AMOUNT("([0-9]+(\\.[0-9]+)?)|(([0-9]+(\\.[0-9]+)?)-([0-9]+(\\.[0-9]+)?))"),
	SOUND(".+,[0-9]+(\\.[0-9]+)?,[0-9]+(\\.[0-9]+)?");

	private String reg;

	private InputType(String reg) {
		this.reg = reg;
	}

	public String getReg() {
		return reg;
	}

}
