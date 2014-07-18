package ru.dreamcloud.storage.interfaces;

public interface IRecord {
	
	void save();
	void delete();
	IRecord read();
	
}
