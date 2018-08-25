package br.com.telegram.digibankbot.entities;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class Transacao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String codigo;
	private String tipo;
	private BigDecimal valor;
	private Tarifa tarifa;
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public Tarifa getTarifa() {
		return tarifa;
	}
	public void setTarifa(Tarifa tarifa) {
		this.tarifa = tarifa;
	}

}
