package ru.dreamcloud.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

import ru.dreamcloud.storage.interfaces.IStorage;

public class DC_Storage implements IStorage {

	private File fileDB;
	private EnvironmentConfig envConf;
	private Environment env;
	private DatabaseConfig dbConf;
	private StoreConfig esConf;
	private String esPrefix = "persist#";

	private HashMap<String, StorageSource<?>> dataSources;
	private HashMap<String, DataAccessor> dataAccessors;
	

	@Override
	public void initEnvironment(String dbPath) {		
		dataSources = new HashMap<String, StorageSource<?>>();
		dataAccessors = new HashMap<String, DataAccessor>();

		fileDB = new File(dbPath);
		fileDB.mkdirs();

		envConf = new EnvironmentConfig();
		envConf.setAllowCreate(true);
		envConf.setTransactional(true);

		env = new Environment(fileDB, envConf);

		dbConf = new DatabaseConfig();
		dbConf.setAllowCreate(false);
		dbConf.setTransactional(true);

		esConf = new StoreConfig();
		esConf.setAllowCreate(false);
		esConf.setTransactional(true);

		for (String name : env.getDatabaseNames()) {
			// persist#STORE_NAME#ENTITY_CLASS
			if (name.startsWith(esPrefix)) {
				EntityStore es = new EntityStore(env, name.split("#")[1], esConf);
				StorageSource<?> source = new StorageSource<EntityStore>(true, name, es, esPrefix);
				dataSources.put(name.split("#")[1], source);
				System.out.format("-- База хранилища %s.\n",	name/*name.split("#")[1]*/);
			} else {
				Database db = env.openDatabase(null, name, dbConf);
				StorageSource<?> source = new StorageSource<Database>(true,	name, db, "");
				dataSources.put(name, source);
				System.out.format("-- База хранилища %s.\n", name);
			}
		}
	}
	
	private void addDataAccessor(String dsName) {
		System.out.println("============= Список DataAccessor ==============");
		if (isEntityStore(dsName)) {			
			System.out.println("-- Хранилище: Разрешаю на добавление!");
			dataAccessors.put(dsName, new DataAccessor(getDataSource(dsName)));
			System.out.println("-- Хранилище: Создан DataAccessor()");
		} else {
			System.out.println("-- Хранилище: Запрещено на добавление!");
		}
	}
	
	public DataAccessor getDataAccessor(String dsName) {
		DataAccessor da;
		if(dataAccessors.containsKey(dsName)){
			da = dataAccessors.get(dsName);
		} else {			
			addDataAccessor(dsName);
			da = dataAccessors.get(dsName);
			System.out.println("-- Хранилище: DataAccessor с именем " + dsName + " существует.");
		}		
		return da;
	}

	@Override
	public void closeEnvironment() {
		env.close();
		System.out.println("-- Хранилище закрыто");
	}	


	@Override
	public void showAllDataSources() {
		for (Entry<String, StorageSource<?>> entry : dataSources.entrySet()) {
			String key = entry.getKey();
			StorageSource<?> source = entry.getValue();
			if (source.isStatus()) {
				System.out.format("%s - %s\n", key, "источник");
			} else {
				System.out.format("%s - %s\n", key, "источник");
			}
		}
	}

	@Override
	public void createDatabase(String dbName) {		
		dbConf.setAllowCreate(true);
		Database db = env.openDatabase(null, dbName, dbConf);
		StorageSource<Database> source = new StorageSource<Database>(true, db.getDatabaseName(), db, "");
		dataSources.put(dbName, source);

		System.out.format("-- База хранилища с именем %s создана\n", dbName);

	}

	public StorageSource<?> getDataSource(String dsName) {
		return dataSources.get(dsName);
	}
	
	public void createEntity(String dsName, String nameOfObject) {		
		File javaSrcFile = new File("D:/_work/projects/DC_Storage/src/ru/dreamcloud/entities/"+nameOfObject+".java");
		FileOutputStream javaOutput = null;		
		Properties props = new Properties();
		String javaSrcEntity = new String();
		String javaSrcBody = new String();
		String javaSrcMethods = new String();
		String javaSrcHead = new String("package ru.dreamcloud.entities;\n\n");
		
		javaSrcHead += "import com.sleepycat.persist.model.Entity;\n";
		javaSrcHead += "import com.sleepycat.persist.model.PrimaryKey;\n";
		
		javaSrcBody += "\n@Entity\n";
		javaSrcBody += "public class "+nameOfObject+" {\n";
		try {
			props.load(new FileInputStream("./tmp/" + nameOfObject	+ ".properties"));
			Enumeration<?> keys = props.propertyNames();			
			// loop through all properties
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String type = props.getProperty(key).split(":")[0];
				String name = props.getProperty(key).split(":")[1];
				
				if(key.indexOf("PrimaryKey") >= 0){
					javaSrcBody += "\t@"+key+"\n";					
				}
				
				if(key.indexOf("SecondaryKey") >= 0){
					String relation = props.getProperty(key).split(":")[2];
					javaSrcHead += "import com.sleepycat.persist.model.SecondaryKey;\n";
					javaSrcHead += "import com.sleepycat.persist.model.Relationship;\n";					
					javaSrcBody += "\t@"+key+"(relate=Relationship."+relation+")\n";
				}
				
				
				javaSrcBody += "\tprivate " + type + " " + name + ";\n";
				
				/**
				 * GET Methods
				 */
				javaSrcMethods += "\n\tpublic " + type + " get" + name.replace(name.charAt(0), name.toUpperCase().charAt(0)) + "() {\n";
				javaSrcMethods += "\t\treturn "+name+";\n";
				javaSrcMethods += "\t}\n";
				
				
				/**
				 * SET Methods
				 */
				javaSrcMethods += "\n\tpublic void set" + name.replace(name.charAt(0), name.toUpperCase().charAt(0)) + "("+ type +" " + name +") {\n";
				javaSrcMethods += "\t\tthis."+ name+"="+name+";\n";
				javaSrcMethods += "\t}\n";
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		} finally {
			try {
				if (javaOutput != null) {
					javaOutput.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		javaSrcBody += javaSrcMethods+"}";
		javaSrcEntity += javaSrcHead+javaSrcBody;
		//System.out.println(javaSrcEntity);
		try {
			if(!javaSrcFile.exists()) {
				javaSrcFile.createNewFile();
				javaOutput = new FileOutputStream(javaSrcFile);
				javaOutput.write(javaSrcEntity.getBytes());
				javaOutput.flush();
				javaOutput.close();
				System.out.println(" -- Создан файл "+javaSrcFile.getPath());					
			} else {
				System.out.println(" -- Файл с таким именем " + nameOfObject + ".java уже создан.");
			}
			// TODO: ?????????? DA
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}	

	@Override
	public void closeDatabase(String dbName) {
		try {
			Database db = (Database) dataSources.get(dbName).getStype();
			db.close();
			getDataSource(dbName).setStatus(false);
			System.out.format("-- База хранилища %s закрыта\n", dbName);
		} catch (Exception e) {
			System.out.format("-- База хранилища с таким именем %s не может быть закрыта\n", dbName);
			System.out.println(e);
		}

	}

	@Override
	public void deleteDataSource(String dbName) {
		try {
			StorageSource<?> source = dataSources.get(dbName);
			String name = source.getSourceId();
			env.removeDatabase(null, name);
			dataSources.remove(dbName);

			System.out.format("-- База хранилища с таким именем %s удалена\n", name);
		} catch (Exception e) {
			System.out.format("-- База хранилища с таким именем %s не может быть удалена\n", dbName);
			System.out.println(e);
		}

	}

	@Override
	public void createEntityStore(String esName) {		
		esConf.setAllowCreate(true);
		EntityStore es = new EntityStore(env, esName, esConf);
		for (String name : env.getDatabaseNames()) {
			// persist#STORE_NAME#ENTITY_CLASS
			if (name.startsWith(esPrefix + esName + "#")) {
				StorageSource<EntityStore> source = new StorageSource<EntityStore>(true, name, es, esPrefix);
				dataSources.put(esName, source);
				break;
			}
		}

		System.out.format("-- Хранилище сущностей с именем %s создано\n", esName);

	}

	@Override
	public void closeEntityStore(String esName) {
		try {
			EntityStore es = (EntityStore) dataSources.get(esName).getStype();
			es.close();
			getDataSource(esName).setStatus(false);
			System.out.format("-- Хранилище сущностей с именем %s закрыто\n", esName);
		} catch (Exception e) {
			System.out.format("-- Хранилище сущностей с именем %s не может быть закрыта\n",
					esName);
			System.out.println(e);
		}
	}

	@Override
	public void closeAllDataSources() {
		for (String name : env.getDatabaseNames()) {
			if (name.startsWith(esPrefix)) {
				closeEntityStore(name.split("#")[1]);
			} else {
				closeDatabase(name);
			}
		}
	}

	private boolean isEntityStore(String dsName) {
		boolean esType;
		if (getDataSource(dsName).getStype().getClass() == EntityStore.class) {
			esType = true;
		} else {
			esType = false;
		}		
		return esType;
	}

}
