package br.com.telegram.digibankbot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;

import br.com.telegram.digibankbot.entities.Conta;
import br.com.telegram.digibankbot.entities.Dependente;
import br.com.telegram.digibankbot.entities.Emprestimo;
import br.com.telegram.digibankbot.entities.Tarifa;
import br.com.telegram.digibankbot.entities.Titular;
import br.com.telegram.digibankbot.entities.Transacao;
import br.com.telegram.digibankbot.singleton.ContaSingleton;
import br.com.telegram.digibankbot.utilities.FileManager;

/**
 * 
 * @author Jo�o Marcos da Costa, Andr� Aparecido de Souza
 * @version 1.0
 *
 */

public class BotHandler {
	
	private List<String> perguntasCriarConta;
	private Iterator<String> iteracaoCriarConta;
	private Map<Integer, String> dadosCriarConta;
	private int indexCriarConta;
	
	private List<String> perguntasModificarConta;
	private Iterator<String> iteracaoModificarConta;
	private Map<Integer, String> dadosModificarConta;
	private int indexModificarConta;
	
	private List<String> perguntasIncluirDependente;
	private Iterator<String> iteracaoIncluirDependente;
	private Map<Integer, String> dadosIncluirDependente;
	private int indexIncluirDependente;
	
	private List<String> perguntasDepositar;
	private Iterator<String> iteracaoDepositar;
	private Map<Integer, String> valorDeposito;
	private int indexDepositar;
	
	private List<String> perguntasSacar;
	private Iterator<String> iteracaoSacar;
	private Map<Integer, String> valorSaque;
	private int indexSacar;
	
	private List<String> perguntasEmprestimo;
	private Iterator<String> iteracaoEmprestimo;
	private Map<Integer, String> dadosEmprestimo;
	private int indexEmprestimo;

	public BaseResponse escrever(TelegramBot bot, Update update) {
		return bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
	}
	
	public String gerarCodigoTransacao() {
		UUID uuid = UUID.randomUUID();
		String codigo = uuid.toString();
		return codigo;
	}
	
	public void cadastrarTransacao(Conta conta, String tipo, String servico, String valor) {
		Transacao transacao = new Transacao();
		transacao.setCodigo(gerarCodigoTransacao());
		transacao.setTipo(tipo);
		transacao.setValor(new BigDecimal(valor).setScale(2));
		Tarifa tarifa = new Tarifa(servico);
		transacao.setTarifa(tarifa);
		conta.getTransacoes().add(transacao);
	}
	
	public void calcularEmprestimo(Conta conta, BigDecimal valorEmprestimo, Integer prazoEmprestimo) {
		Emprestimo emprestimo = new Emprestimo();
		emprestimo.setDataContratacao(LocalDate.now());
		emprestimo.setDataExpiracao(LocalDate.now().plusMonths(prazoEmprestimo));
		emprestimo.setValor(valorEmprestimo.setScale(2));
		emprestimo.setJuros(valorEmprestimo.multiply(new BigDecimal("0.05")).multiply(new BigDecimal(prazoEmprestimo)));
		emprestimo.setValorAtualizado(valorEmprestimo.add(emprestimo.getJuros()).setScale(2));
		conta.getEmprestimos().add(emprestimo);
	}
	
	public void prepararPerguntas(String action) {
		if(action.equals("criarConta")) {
			dadosCriarConta = new HashMap<>();
			perguntasCriarConta = Arrays.asList("Digite o seu CPF:", 
					  "Digite o seu nome:", 
					  "Digite a sua idade:", 
					  "Digite o seu email:",
					  "Qual ser� o tipo de conta? Digite CC para conta corrente ou CP para conta poupan�a:");
			iteracaoCriarConta = perguntasCriarConta.iterator();
			indexCriarConta = 0;
		}
		if(action.equals("modificarConta")) {
			dadosModificarConta = new HashMap<>();
			perguntasModificarConta = Arrays.asList("Digite seu novo e-mail:",  
					  "Qual ser� o novo tipo de conta? Digite CC para conta corrente ou CP para conta poupan�a:");
			iteracaoModificarConta = perguntasModificarConta.iterator();
			indexModificarConta = 0;
		}
		if(action.equals("incluirDependente")) {
			dadosIncluirDependente = new HashMap<>();
			perguntasIncluirDependente = Arrays.asList("Digite o CPF do dependente:", 
					  "Digite o nome do dependente:", 
					  "Digite a idade do dependente:", 
					  "Digite o e-mail do dependente:",
					  "Qual o grau de parentesco? Digite C para c�njuge, F para filho(a), N para netos(a) ou P para pais:");
			iteracaoIncluirDependente = perguntasIncluirDependente.iterator();
			indexIncluirDependente = 0;
		}
		if(action.equals("depositar")) {
			valorDeposito = new HashMap<>();
			perguntasDepositar = Arrays.asList("Digite o valor do dep�sito:");
			iteracaoDepositar = perguntasDepositar.iterator();
			indexDepositar = 0;
		}
		if(action.equals("sacar")) {
			valorSaque = new HashMap<>();
			perguntasSacar = Arrays.asList("Digite o valor do saque:");
			iteracaoSacar = perguntasSacar.iterator();
			indexSacar = 0;
		}
		if(action.equals("emprestimo")) {
			dadosEmprestimo = new HashMap<>();
			perguntasEmprestimo = Arrays.asList("Digite o valor do empr�stimo:",
												"Digite o prazo em meses do empr�stimo: ");
			iteracaoEmprestimo = perguntasEmprestimo.iterator();
			indexEmprestimo = 0;
		}
	}
	
	public static SendResponse iniciar(TelegramBot bot, Update update) {
		FileManager.escrever("Seja bem-vindo(a) ao digibank!\n\n"
				+ "Somos um banco digital que oferece uma experi�ncia online e �nica para nossos clientes!\n"
				+ "Vamos come�ar? Que tal digitar o comando /ajuda para ver nossas op��es dispon�veis?");
		return bot.execute(new SendMessage(update.message().chat().id(), "Seja bem-vindo(a) ao digibank!\n\n"
				+ "Somos um banco digital que oferece uma experi�ncia online e �nica para nossos clientes!\n"
				+ "Vamos come�ar? Que tal digitar o comando /ajuda para ver nossas op��es dispon�veis?"));
	}
	
	public SendResponse criarConta(TelegramBot bot, Update update) {
		try {
			if(ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� j� possui uma conta criada pelo visto n�o �?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� j� possui uma conta criada pelo visto n�o �?"));
			}
			if(indexCriarConta > 0) {
				FileManager.escrever(update.message().text());
				dadosCriarConta.put(indexCriarConta, update.message().text());
			}
			indexCriarConta++;
			if(iteracaoCriarConta.hasNext()) {
				return bot.execute(new SendMessage(update.message().chat().id(), iteracaoCriarConta.next()));
			} else {
				Conta conta = ContaSingleton.getInstance();
				Titular titular = new Titular();
				titular.setCpf(dadosCriarConta.get(1));
				titular.setNome(dadosCriarConta.get(2));
				titular.setIdade(Integer.parseInt(dadosCriarConta.get(3)));
				titular.setEmail(dadosCriarConta.get(4));
				if(dadosCriarConta.get(5).equals("CC")) {
					conta.setTipo("Conta corrente");
				} else if(dadosCriarConta.get(5).equals("CP")) {
					conta.setTipo("Poupan�a");
				}
				conta.setTitular(titular);
				FileManager.escrever("Prezado(a) " + conta.getTitular().getNome()  + " parab�ns! Voc� � nosso mais novo cliente!");
				return bot.execute(new SendMessage(update.message().chat().id(), "Prezado(a) " + conta.getTitular().getNome()  + " parab�ns! Voc� � nosso mais novo cliente!"));
			}
		} catch (Exception e) {
			ContaSingleton.destroy();
			FileManager.escrever("Ocorreu um erro durante a cria��o da sua conta, entre em contato"
					 + " com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante a cria��o da sua conta, entre em contato"
																			 + " com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse modificarConta(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			}
			Conta conta = ContaSingleton.getInstance();
			if(indexModificarConta > 0) {
				FileManager.escrever(update.message().text());
				dadosModificarConta.put(indexModificarConta, update.message().text());
			}
			indexModificarConta++;
			if(iteracaoModificarConta.hasNext()) {
				return bot.execute(new SendMessage(update.message().chat().id(), iteracaoModificarConta.next()));
			} else {
				conta.getTitular().setEmail(dadosModificarConta.get(1));
				if(dadosModificarConta.get(2).equals("CC")) {
					conta.setTipo("Conta corrente");
				} else if(dadosModificarConta.get(2).equals("CP")) {
					conta.setTipo("Poupan�a");
				}
				FileManager.escrever("Prezado(a) " + conta.getTitular().getNome() + " a sua conta foi modificada!");
				return bot.execute(new SendMessage(update.message().chat().id(), "Prezado(a) " + conta.getTitular().getNome() + " a sua conta foi modificada!"));
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a modifica��o da sua conta,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a modifica��o da sua conta,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse incluirDependente(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			}
			if(indexIncluirDependente > 0) {
				FileManager.escrever(update.message().text());
				dadosIncluirDependente.put(indexIncluirDependente, update.message().text());
			}
			indexIncluirDependente++;
			if(iteracaoIncluirDependente.hasNext()) {
				return bot.execute(new SendMessage(update.message().chat().id(), iteracaoIncluirDependente.next()));
			} else {
				Conta conta = ContaSingleton.getInstance();
				Dependente dependente = new Dependente();
				dependente.setCpf(dadosIncluirDependente.get(1));
				dependente.setNome(dadosIncluirDependente.get(2));
				dependente.setIdade(Integer.parseInt(dadosIncluirDependente.get(3)));
				dependente.setEmail(dadosIncluirDependente.get(4));
				if(dadosIncluirDependente.get(5).equals("C")) {
					dependente.setParentesco("C�njuge");
				}
				if(dadosIncluirDependente.get(5).equals("F")) {
					dependente.setParentesco("Filho");
				}
				if(dadosIncluirDependente.get(5).equals("N")) {
					dependente.setParentesco("Neto");
				}
				if(dadosIncluirDependente.get(5).equals("P")) {
					dependente.setParentesco("Pais");
				}
				dependente.setParentesco(dadosIncluirDependente.get(5));
				conta.getTitular().getDependentes().add(dependente);
				FileManager.escrever("O dependente " + dependente.getNome() + " foi adicionado a sua conta!");
				return bot.execute(new SendMessage(update.message().chat().id(), "O dependente " + dependente.getNome() + " foi adicionado a sua conta!"));
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a inclus�o de dependente,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a inclus�o de dependente,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse dadosConta(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			} else {
				Conta conta = ContaSingleton.getInstance();
				StringBuilder dados = new StringBuilder();
				dados.append("Prezado(a) " + conta.getTitular().getNome() + ", veja abaixo os dados de sua conta: \n\n");
				dados.append("CPF: " + conta.getTitular().getCpf() + "\n");
				dados.append("Idade: " + conta.getTitular().getIdade() + "\n");
				dados.append("E-mail: " + conta.getTitular().getEmail() + "\n");
				dados.append("Tipo: " + conta.getTipo() + "\n");
				dados.append("Data de abertura: " + conta.getDataAbertura() + "\n");
				dados.append("Saldo: R$ " + conta.getSaldo().setScale(2) + "\n");
				Stream<Dependente> dependentes = conta.getTitular().getDependentes().stream();
				dependentes.filter(d -> conta.getTitular().getDependentes().size() > 0).forEach(d -> dados.append("\nDependentes: \n\n"
						+ "CPF: " + d.getCpf() + "\n"
						+ "Nome: " + d.getNome() + "\n"
						+ "Idade: " + d.getIdade() + "\n"
						+ "E-mail: " + d.getEmail() + "\n"
						+ "Parentesco: " + d.getParentesco() + "\n"));
				FileManager.escrever(dados.toString());
				return bot.execute(new SendMessage(update.message().chat().id(), dados.toString()));
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a exibi��o dos dados da conta,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a exibi��o dos dados da conta,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse depositar(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			} 
			if(indexDepositar > 0) {
				FileManager.escrever(update.message().text());
				valorDeposito.put(indexDepositar, update.message().text());
			}
			indexDepositar++;
			if(iteracaoDepositar.hasNext()) {
				return bot.execute(new SendMessage(update.message().chat().id(), iteracaoDepositar.next()));
			} else {
				Conta conta = ContaSingleton.getInstance();
				conta.setSaldo(conta.getSaldo().add(new BigDecimal(valorDeposito.get(1))));
				cadastrarTransacao(conta, "CR�DITO", "DEP�SITO", valorDeposito.get(1));
				FileManager.escrever("Deposito realizado! Saldo atualizado: R$ " + conta.getSaldo().setScale(2));
				return bot.execute(new SendMessage(update.message().chat().id(), "Deposito realizado! Saldo atualizado: R$ " + conta.getSaldo().setScale(2)));
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante o dep�sito,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante o dep�sito,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse sacar(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			}
			if(indexSacar > 0) {
				FileManager.escrever(update.message().text());
				valorSaque.put(indexSacar, update.message().text());
			}
			indexSacar++;
			if(iteracaoSacar.hasNext()) {
				return bot.execute(new SendMessage(update.message().chat().id(), iteracaoSacar.next()));
			} else {
				Conta conta = ContaSingleton.getInstance();
				conta.setSaldo(conta.getSaldo().subtract(new BigDecimal(valorSaque.get(1))));
				cadastrarTransacao(conta, "D�BITO", "SAQUE", valorSaque.get(1));
				FileManager.escrever("Saque realizado! Saldo atualizado: R$ " + conta.getSaldo().setScale(2));
				return bot.execute(new SendMessage(update.message().chat().id(), "Saque realizado! Saldo atualizado: R$ " + conta.getSaldo().setScale(2)));
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante o saque,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante o saque,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse extrato(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			}
			Conta conta = ContaSingleton.getInstance();
			if(conta.getTransacoes().size() > 0) {
				StringBuilder dados = new StringBuilder();
				dados.append("\nEXTRATO \n");
				Stream<Transacao> transacao = conta.getTransacoes().stream();
				transacao.forEach(t -> dados.append("\nC�digo: " + t.getCodigo() + 
													"\nTipo: " + t.getTipo() +
													"\nOpera��o: " + t.getTarifa().getServico() +
													"\nValor: R$ " + t.getValor() + "\n"));
				cadastrarTransacao(conta, "", "EXTRATO", "0");
				FileManager.escrever(dados.toString());
				return bot.execute(new SendMessage(update.message().chat().id(), dados.toString()));
			} else {
				FileManager.escrever("Voc� ainda n�o realizou nenhuma transa��o!");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o realizou nenhuma transa��o!"));
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a exibi��o do extrato,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a exibi��o do extrato,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse emprestimo(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			}
			if(indexEmprestimo > 0) {
				FileManager.escrever(update.message().text());
				dadosEmprestimo.put(indexEmprestimo, update.message().text());
			}
			indexEmprestimo++;
			if(iteracaoEmprestimo.hasNext()) {
				return bot.execute(new SendMessage(update.message().chat().id(), iteracaoEmprestimo.next()));
			} else {
				Conta conta = ContaSingleton.getInstance();
				if(conta.getSaldo().compareTo(BigDecimal.ZERO) == 0 || conta.getSaldo().compareTo(BigDecimal.ZERO) == -1) {
					FileManager.escrever("Voc� n�o possui saldo suficiente!");
					return bot.execute(new SendMessage(update.message().chat().id(), "Voc� n�o possui saldo suficiente!"));
				}
				BigDecimal valorMaximo = conta.getSaldo().multiply(new BigDecimal("40"));
				BigDecimal valorSolicitado = new BigDecimal(dadosEmprestimo.get(1));
				Integer prazoSolicitado = Integer.parseInt(dadosEmprestimo.get(2));
				if(valorSolicitado.compareTo(valorMaximo) == 1) {
					FileManager.escrever("O valor que voc� digitou � maior que o valor m�ximo que voc� pode solicitar!");
					return bot.execute(new SendMessage(update.message().chat().id(), "O valor que voc� digitou � maior que o valor m�ximo que voc� pode solicitar!"));
				} else if(prazoSolicitado < 1 || prazoSolicitado > 36) {
					FileManager.escrever("O prazo que voc� digitou � maior que 36 meses ou menor que 1 m�s!");
					return bot.execute(new SendMessage(update.message().chat().id(), "O prazo que voc� digitou � maior que 36 meses ou menor que 1 m�s!"));
				} else {
					calcularEmprestimo(conta, valorSolicitado, prazoSolicitado);
					cadastrarTransacao(conta, "CR�DITO", "EMPR�STIMO", valorSolicitado.toString());
					conta.setSaldo(conta.getSaldo().add(valorSolicitado.setScale(2)));
					FileManager.escrever("Empr�stimo solicitado com sucesso!");
					return bot.execute(new SendMessage(update.message().chat().id(), "Empr�stimo solicitado com sucesso!"));
				}
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a solicita��o de empr�stimo,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a solicita��o de empr�stimo,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse saldoEmprestimo(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			} else {
				Conta conta = ContaSingleton.getInstance();
				StringBuilder dados = new StringBuilder();
				if(conta.getEmprestimos().size() > 0) {
					dados.append("EMPR�STIMOS\n");
					Stream<Emprestimo> emprestimo = conta.getEmprestimos().stream();
					emprestimo.forEach(e -> dados.append("\nValor atualizado para pagamento: " + e.getValorAtualizado().setScale(2) + 
														 "\nPrazo para pagamento do empr�stimo: " + e.getDataExpiracao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"));
				}
				FileManager.escrever(dados.toString());
				return bot.execute(new SendMessage(update.message().chat().id(), dados.toString()));
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a exibi��o do saldo devedor"
					+ " do empr�stimo, entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a exibi��o do saldo devedor"
					+ " do empr�stimo, entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse lancamentos(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			} else {
				Conta conta = ContaSingleton.getInstance();
				if(conta.getTransacoes().size() > 0) {
					BigDecimal totalLancamentos = BigDecimal.ZERO;
					StringBuilder dados = new StringBuilder();
					dados.append("EXTRATO DE LAN�AMENTOS DETALHADO\n\n");
					for(Transacao transacao : conta.getTransacoes()) {
						if(transacao.getTipo().equals("CR�DITO")) {
							dados.append("\nC�digo: " + transacao.getCodigo());
							dados.append("\nTipo: " + transacao.getTipo());
							dados.append("\nOpera��o: " + transacao.getTarifa().getServico());
							dados.append("\nValor: " + transacao.getValor().setScale(2));
							dados.append("\nCusto do servi�o: " + transacao.getTarifa().getValor());
							dados.append("\nDescri��o do servi�o: \n" + transacao.getTarifa().getServico());
							totalLancamentos = totalLancamentos.add(transacao.getValor().setScale(2));
						}
					}
					dados.append("\n\nTotal de lan�amentos: " + totalLancamentos.setScale(2));
					FileManager.escrever(dados.toString());
					return bot.execute(new SendMessage(update.message().chat().id(), dados.toString()));
				} else {
					FileManager.escrever("Voc� ainda n�o realizou nenhuma transa��o!");
					return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o realizou nenhuma transa��o!"));
				}
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a exibi��o dos lan�amentos,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a exibi��o dos lan�amentos,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse retiradas(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			} else {
				Conta conta = ContaSingleton.getInstance();
				if(conta.getTransacoes().size() > 0) {
					BigDecimal totalRetiradas = BigDecimal.ZERO;
					StringBuilder dados = new StringBuilder();
					for(Transacao transacao : conta.getTransacoes()) {
						if(transacao.getTipo().equals("D�BITO")) {
							dados.append("EXTRATO DE RETIRADAS \n");
							dados.append("\nOpera��o: " + transacao.getTarifa().getServico());
							dados.append("\nValor: " + transacao.getValor());
							totalRetiradas = totalRetiradas.add(transacao.getValor().setScale(2));
						}
					}
					dados.append("\n\nTotal de retiradas: " + totalRetiradas);
					FileManager.escrever(dados.toString());
					return bot.execute(new SendMessage(update.message().chat().id(), dados.toString()));
				} else {
					FileManager.escrever("Voc� ainda n�o realizou nenhuma transa��o!");
					return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o realizou nenhuma transa��o!"));
				}
			}
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a exibi��o das retiradas,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a exibi��o das retiradas,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse tarifas(TelegramBot bot, Update update) {
		try {
			if(!ContaSingleton.contaCriada()) {
				FileManager.escrever("Voc� ainda n�o possui uma conta criada n�o � mesmo?");
				return bot.execute(new SendMessage(update.message().chat().id(), "Voc� ainda n�o possui uma conta criada n�o � mesmo?"));
			}
			Conta conta = ContaSingleton.getInstance();
			BigDecimal saques = BigDecimal.ZERO;
			BigDecimal extratos = BigDecimal.ZERO;
			BigDecimal emprestimos = BigDecimal.ZERO;
			BigDecimal custoTotal = BigDecimal.ZERO;
			StringBuilder dados = new StringBuilder();
			dados.append("TARIFAS\n\n");
			dados.append("Saque: \t R$ 2,50\n");
			dados.append("Extrato: \t R$ 1,00\n");
			dados.append("Empr�stimo: \t R$ 15,00\n");
			if(conta.getTransacoes().size() > 0) {
				for(Transacao transacao : conta.getTransacoes()) {
					if(transacao.getTarifa().getServico().equals("SAQUE") || transacao.getTarifa().getServico().equals("EXTRATO") || transacao.getTarifa().getServico().equals("EMPR�STIMO")) {
						if(transacao.getTarifa().getServico().equals("SAQUE")) {
							saques = saques.add(new BigDecimal(1));
						}
						if(transacao.getTarifa().getServico().equals("EXTRATO")) {
							extratos = extratos.add(new BigDecimal(1));
						}
						if(transacao.getTarifa().getServico().equals("EMPR�STIMO")) {
							emprestimos = emprestimos.add(new BigDecimal(1));
						}
					}
				}
				saques = saques.multiply(new BigDecimal("2.50")).setScale(2);
				extratos = extratos.multiply(new BigDecimal("1.00")).setScale(2);
				emprestimos = emprestimos.multiply(new BigDecimal("15.00")).setScale(2);
				dados.append("\nServi�os utilizados em sua conta: \n");
				dados.append("Saques: " + saques + "\n");
				dados.append("Extratos: " + extratos + "\n");
				dados.append("Empr�stimos: " + emprestimos  + "\n");
				custoTotal = custoTotal.add(saques).add(extratos).add(emprestimos).setScale(2);
				dados.append("\nCusto total de tarifas: " + custoTotal);
			}
			FileManager.escrever(dados.toString());
			return bot.execute(new SendMessage(update.message().chat().id(), dados.toString()));
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a exibi��o das tarifas,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a exibi��o das tarifas,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
	public SendResponse ajuda(TelegramBot bot, Update update) {
		try {
			FileManager.escrever("Em que podemos ajud�-lo?\n"
					   + "/criarConta -> Criar uma conta para voc�?\n"
					   + "/modificarConta -> Modificar sua conta?\n"
					   + "/incluirDependente -> Incluir dependentes em sua conta?\n"
					   + "/dadosConta - > Checar os dados de sua conta?\n"
					   + "/depositar -> Fazer um dep�sito?\n"
					   + "/sacar -> Realizar um saque?\n"
					   + "/extrato -> Ver seu extrato?\n"
					   + "/emprestimo -> Solicitar um empr�stimo?\n"
					   + "/saldoEmprestimo -> Ver saldo devedor do empr�stimo?\n"
					   + "/lancamentos -> Ver seus lan�amentos detalhadamente?\n"
					   + "/retiradas -> Ver seu extrato de d�bitos?\n"
					   + "/tarifas -> Verificar as tarifas de servi�os?\n");
			return bot.execute(new SendMessage(update.message().chat().id(), "Em que podemos ajud�-lo?\n"
					   + "/criarConta -> Criar uma conta para voc�?\n"
					   + "/modificarConta -> Modificar sua conta?\n"
					   + "/incluirDependente -> Incluir dependentes em sua conta?\n"
					   + "/dadosConta - > Checar os dados de sua conta?\n"
					   + "/depositar -> Fazer um dep�sito?\n"
					   + "/sacar -> Realizar um saque?\n"
					   + "/extrato -> Ver seu extrato?\n"
					   + "/emprestimo -> Solicitar um empr�stimo?\n"
					   + "/saldoEmprestimo -> Ver saldo devedor do empr�stimo?\n"
					   + "/lancamentos -> Ver seus lan�amentos detalhadamente?\n"
					   + "/retiradas -> Ver seu extrato de d�bitos?\n"
					   + "/tarifas -> Verificar as tarifas de servi�os?\n"));
		} catch (Exception e) {
			FileManager.escrever("Ocorreu um erro durante durante a solicita��o de ajuda,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!");
			return bot.execute(new SendMessage(update.message().chat().id(), "Ocorreu um erro durante durante a solicita��o de ajuda,"
					+ " entre em contato com a central de servi�os ou repita a opera��o!"));
		}
	}
	
}
