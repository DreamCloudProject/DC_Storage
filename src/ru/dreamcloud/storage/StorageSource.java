package ru.dreamcloud.storage;

public class StorageSource<SourceType> {
	
	private boolean status;
	private String sourceId;
	private SourceType stype;
	private String prefix;
	
	public StorageSource(){		
	}
	
	public StorageSource(boolean status, String sourceId, SourceType stype, String prefix) {
		this.status = status;
		this.sourceId = sourceId;
		this.stype = stype;
		this.prefix = prefix;
	}
	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public SourceType getStype() {
		return stype;
	}
	public void setStype(SourceType stype) {
		this.stype = stype;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
