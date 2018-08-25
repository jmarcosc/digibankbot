package br.com.telegram.digibankbot.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class BotProperties {

	private static Properties properties = new Properties();
	private static String token;
	
	public static Properties botProperties() throws IOException {
		
		FileInputStream fileProperties = new FileInputStream("./properties/digibankbot.properties");
		properties.load(fileProperties);
		return properties;
		
	}
	
	public static String getToken() throws IOException {
		
		properties = botProperties();
		token = properties.getProperty("prop.bot.token");
		return token;

	}
	
}
