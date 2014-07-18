package ru.dreamcloud.storage;

import java.util.HashMap;

import com.sleepycat.je.DatabaseException;

public class DataAccessor {
	HashMap <String, DataIndex<?>> accessors;		
	private DataIndex<?> dIndex;
	private StorageSource<?> store;
	
	public StorageSource<?> getStore() {
		return store;
	}

	public DataAccessor(StorageSource<?> store) throws DatabaseException{
		accessors = new HashMap<String, DataIndex<?>>();
		this.store = store;
	}
	
	public void addAccessor(String accessorKey, DataIndex<?> dIndex) {
		if(!accessors.containsKey(accessorKey)){
			accessors.put(accessorKey, dIndex);
			System.out.println(" -- Data Accessor: Добавлен DataIndex на сущность " + accessorKey + "!");
		} else {
			System.out.println(" -- Data Accessor: DataIndex на сущность " + accessorKey + " существует!");
		}
	}	
	
	public DataIndex<?> getAccessor(String accessorKey) {
		if(accessors.containsKey(accessorKey)){
			dIndex = accessors.get(accessorKey);
		} else {
			dIndex = null;
		}
		return dIndex;
	}
	
}
