package br.com.telegram.digibankbot.entities;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class Tarifa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private BigDecimal valor;
	private String servico;
	
	public Tarifa(String servico) {
		if(servico.equals("DEPÓSITO")) {
			this.valor = BigDecimal.ZERO;
		} else if(servico.equals("SAQUE")) {
			this.valor = new BigDecimal("2.50");
		} else if(servico.equals("EXTRATO")) {
			this.valor = new BigDecimal("1.00");
		} else if(servico.equals("EMPRÉSTIMO")) {
			this.valor = new BigDecimal("15.00");
		}
		this.servico = servico;
	}

	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public String getServico() {
		return servico;
	}
	public void setServico(String servico) {
		this.servico = servico;
	}
	
}
