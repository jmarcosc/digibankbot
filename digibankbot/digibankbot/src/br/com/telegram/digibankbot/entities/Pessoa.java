package br.com.telegram.digibankbot.entities;

import java.io.Serializable;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class Pessoa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String cpf;
	private String nome;
	private int idade;
	private String email;
	
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public int getIdade() {
		return idade;
	}
	public void setIdade(int idade) {
		this.idade = idade;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
