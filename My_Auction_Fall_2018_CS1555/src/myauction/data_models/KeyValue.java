package myauction.data_models;

public class KeyValue {
	private String key;
	private Integer value;

	public KeyValue(String key, Integer value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String toString() {
		String toReturn = "\n" + key + "\t" + value;
		return toReturn;
	}


}
