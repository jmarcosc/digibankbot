package br.com.telegram.digibankbot.entities;

import java.io.Serializable;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class Dependente extends Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String parentesco;

	public String getParentesco() {
		return parentesco;
	}

	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}
	
}
