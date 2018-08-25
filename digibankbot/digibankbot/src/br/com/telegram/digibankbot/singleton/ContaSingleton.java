package br.com.telegram.digibankbot.singleton;

import br.com.telegram.digibankbot.entities.Conta;

/**
 * 
 * @author Jo�o Marcos da Costa, Andr� Aparecido de Souza
 * @version 1.0
 *
 */

public class ContaSingleton {
	
	private static Conta conta;
	
	private ContaSingleton() {
		
	}
	
	public static synchronized Conta getInstance() {
		if(conta == null) {
			conta = new Conta();
		}
		return conta;
	}
	
	public static void destroy() {
		conta = null;
	}
	
	public static boolean contaCriada() {
		if(conta == null) {
			return false;
		}
		return true;
	}
}
