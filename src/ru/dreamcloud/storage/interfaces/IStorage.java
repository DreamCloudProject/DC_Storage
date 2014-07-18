package ru.dreamcloud.storage.interfaces;

public interface IStorage {
	
	void initEnvironment(String dbPath);
	void closeEnvironment();
	
	//void saveRecord(String dsName, Collection<?> entity);
	//void readRecord(String dsName, String key);
	
	void showAllDataSources();
	void deleteDataSource(String dbName);
	
	void createDatabase(String dbName);
	void closeDatabase(String dbName);
	
	void createEntityStore(String esName);
	void closeEntityStore(String esName);
	
	void closeAllDataSources();
}
