package br.com.telegram.digibankbot.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class Emprestimo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private LocalDate dataContratacao;
	private LocalDate dataExpiracao;
	private BigDecimal valor;
	private BigDecimal juros;
	private BigDecimal valorAtualizado;
	
	public LocalDate getDataContratacao() {
		return dataContratacao;
	}
	public void setDataContratacao(LocalDate dataContratacao) {
		this.dataContratacao = dataContratacao;
	}
	public LocalDate getDataExpiracao() {
		return dataExpiracao;
	}
	public void setDataExpiracao(LocalDate dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public BigDecimal getJuros() {
		return juros;
	}
	public void setJuros(BigDecimal juros) {
		this.juros = juros;
	}
	public BigDecimal getValorAtualizado() {
		return valorAtualizado;
	}
	public void setValorAtualizado(BigDecimal valorAtualizado) {
		this.valorAtualizado = valorAtualizado;
	}
	
}
