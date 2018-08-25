package br.com.telegram.digibankbot.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class Conta implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String tipo;
	private BigDecimal saldo;
	private String dataAbertura;
	private Titular titular;
	private List<Transacao> transacoes;
	private List<Emprestimo> emprestimos;
	
	public Conta() {
		this.saldo = BigDecimal.ZERO;
		this.dataAbertura = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		this.transacoes = new ArrayList<>();
		this.emprestimos = new ArrayList<>();
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public String getDataAbertura() {
		return dataAbertura;
	}
	public void setDataAbertura(String dataAbertura) {
		this.dataAbertura = dataAbertura;
	}
	public Titular getTitular() {
		return titular;
	}

	public void setTitular(Titular titular) {
		this.titular = titular;
	}
	public List<Transacao> getTransacoes() {
		return transacoes;
	}
	public void setTransacoes(List<Transacao> transacoes) {
		this.transacoes = transacoes;
	}
	public List<Emprestimo> getEmprestimos() {
		return emprestimos;
	}
	public void setEmprestimos(List<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
	}
	
}
