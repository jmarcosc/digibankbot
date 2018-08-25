package br.com.telegram.digibankbot.entities;

import java.io.Serializable;
import java.util.HashSet;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class Titular extends Pessoa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashSet<Dependente> dependentes;
	
	public Titular() {
		this.dependentes = new HashSet<>();
	}

	public HashSet<Dependente> getDependentes() {
		return dependentes;
	}

	public void setDependentes(HashSet<Dependente> dependentes) {
		this.dependentes = dependentes;
	}
	
	
}
