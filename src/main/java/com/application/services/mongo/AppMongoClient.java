package com.application.services.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Clase singleton para reutilizar la conexión a mongodb.
 */
public final class AppMongoClient {
	private static volatile MongoClient instance = null;
	private static volatile String databaseName = null;
	private static final Logger log = LoggerFactory.getLogger(AppMongoClient.class);

	private AppMongoClient() {}

	public static MongoClient getInstance() throws NumberFormatException, Exception{
		if (instance == null) {
			synchronized(MongoClient.class) {
				if (instance == null) {

					// Se crea mongoclient en base a las configuraciones tomadas del archivo properties.
					try (InputStream input = AppMongoClient.class.getClassLoader().getResourceAsStream("application.properties")) {
						Properties prop = new Properties();
						prop.load(input);

						String host = prop.getProperty("mongoHost");
						int port = Integer.valueOf(prop.getProperty("mongoPort")).intValue();
						databaseName = prop.getProperty("mongoDatabaseName");

						if(log.isDebugEnabled()){ log.debug("Conectando a la base de datos. Host:" + host + ", puerto: " + port + ", base: " + databaseName); }

						instance = new MongoClient(host, port);
					}
					catch (NumberFormatException e){
						log.error("Ocurrió un error al convertir valores. Por favor, revise las configuraciones de conexión a la base de datos.", e);
						throw e;
					}
					catch (Exception e){
						log.error("Ocurrió un error al crear la conexión a la base de datos.", e);
						throw e;
					}
				}
			}
		}
		return instance;
	}

	public static MongoDatabase getDb() throws Exception{
		try {
			return getInstance().getDatabase(databaseName);
		}
		catch (Exception e){
			log.error("Ocurrió un error al obtener la referencia a la base de datos.", e);
			throw e;
		}
	}
}