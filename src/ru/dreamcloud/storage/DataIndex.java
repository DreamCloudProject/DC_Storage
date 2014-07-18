package ru.dreamcloud.storage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class DataIndex<Entity> {
	
	private PrimaryIndex<?, Entity> pIndex;
	private SecondaryIndex<?, ?, Entity> sIndex;
	private EntityStore store;

	public DataIndex(StorageSource<?> store) {
		this.store = (EntityStore) store.getStype();
	}

	public PrimaryIndex<?, Entity> getPrimIndex() {
		return pIndex;
	}

	public void setPrimIndex(Class<?> typeClass, Class<Entity> entityClass) {		
		this.pIndex = store.getPrimaryIndex(typeClass, entityClass);
	}

	public SecondaryIndex<?, ?, Entity> getSecIndex() {
		return sIndex;
	}

	public void setSecIndex(Class<?> typeClass, String fieldName) {
		this.sIndex = store.getSecondaryIndex(this.pIndex, typeClass, fieldName);
	}
	
	public void addRecord(Entity entity) {
		getPrimIndex().put(entity);
	}
	
	public void readAllRecords() {
		System.out.println("======================================");
		EntityCursor<Entity> records = getPrimIndex().entities();
		for (Entity rec : records) {
			System.out.println(rec.getClass().getName());	
			for (Method method : rec.getClass().getDeclaredMethods()) {
				if (method.getName().startsWith("get")) {
					try {
						System.out.println(method.invoke(rec));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
				
			}
			System.out.println("======================================");
		}
	}
	
	public String readAllRecordsToString() {
		String res = "======================================";
		EntityCursor<Entity> records = getPrimIndex().entities();
		for (Entity rec : records) {
			res += rec.getClass().getName();	
			for (Method method : rec.getClass().getDeclaredMethods()) {
				if (method.getName().startsWith("get")) {
					try {
						res += method.invoke(rec);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
				
			}
			res += "======================================";
		}
		return res;
	}
}
