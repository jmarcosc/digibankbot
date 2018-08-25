package br.com.telegram.digibankbot;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

import br.com.telegram.digibankbot.utilities.BotProperties;
import br.com.telegram.digibankbot.utilities.FileManager;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class BotStart {
	
	public static GetUpdatesResponse updatesResponse;
	public static SendResponse sendResponse;
	public static BaseResponse baseResponse;
	public static int idMessage;
	
	public static void main(String[] args) throws IOException {
		
		FileManager.escrever("Bot iniciado dia " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
		TelegramBot digibankbot = new TelegramBot(BotProperties.getToken());
		BotHandler botHandler = new BotHandler();
		String action = "";
		idMessage = 0;
		
		while (true) {

			updatesResponse = digibankbot.execute(new GetUpdates().limit(100).offset(idMessage));

			List<Update> updates = updatesResponse.updates();

			for (Update update : updates) {
				
				idMessage = update.updateId() + 1;
				
				FileManager.escrever(update.message().text());

				switch (update.message().text()) {
				case "/start":
					botHandler.escrever(digibankbot, update);
					BotHandler.iniciar(digibankbot, update);
					break;
				case "/criarConta":
					action = "criarConta";
					botHandler.escrever(digibankbot, update);
					botHandler.prepararPerguntas(action);
					botHandler.criarConta(digibankbot, update);
					break;
				case "/modificarConta":
					action = "modificarConta";
					botHandler.escrever(digibankbot, update);
					botHandler.prepararPerguntas(action);
					botHandler.modificarConta(digibankbot, update);
					break;
				case "/incluirDependente":
					action = "incluirDependente";
					botHandler.escrever(digibankbot, update);
					botHandler.prepararPerguntas(action);
					botHandler.incluirDependente(digibankbot, update);
					break;
				case "/dadosConta":
					botHandler.escrever(digibankbot, update);
					botHandler.dadosConta(digibankbot, update);
					break;
				case "/depositar":
					action = "depositar";
					botHandler.escrever(digibankbot, update);
					botHandler.prepararPerguntas(action);
					botHandler.depositar(digibankbot, update);
					break;
				case "/sacar":
					action = "sacar";
					botHandler.escrever(digibankbot, update);
					botHandler.prepararPerguntas(action);
					botHandler.sacar(digibankbot, update);
					break;
				case "/extrato":
					botHandler.escrever(digibankbot, update);
					botHandler.extrato(digibankbot, update);
					break;
				case "/emprestimo":
					action = "emprestimo";
					botHandler.escrever(digibankbot, update);
					botHandler.prepararPerguntas(action);
					botHandler.emprestimo(digibankbot, update);
					break;
				case "/saldoEmprestimo":
					botHandler.escrever(digibankbot, update);
					botHandler.saldoEmprestimo(digibankbot, update);
					break;
				case "/lancamentos":
					botHandler.escrever(digibankbot, update);
					botHandler.lancamentos(digibankbot, update);
					break;
				case "/retiradas":
					botHandler.escrever(digibankbot, update);
					botHandler.retiradas(digibankbot, update);
					break;
				case "/tarifas":
					FileManager.escrever(update.message().text());
					botHandler.escrever(digibankbot, update);
					botHandler.tarifas(digibankbot, update);
					break;
				case "/ajuda":
					botHandler.escrever(digibankbot, update);
					botHandler.ajuda(digibankbot, update);
					break;
				default:
					if ("criarConta".equals(action)) {
						botHandler.criarConta(digibankbot, update);
					} else if ("modificarConta".equals(action)) {
						botHandler.modificarConta(digibankbot, update);
					} else if ("incluirDependente".equals(action)) {
						botHandler.incluirDependente(digibankbot, update);
					} else if ("depositar".equals(action)) {
						botHandler.depositar(digibankbot, update);
					} else if ("sacar".equals(action)) {
						botHandler.sacar(digibankbot, update);
					} else if ("emprestimo".equals(action)) {
						botHandler.emprestimo(digibankbot, update);
					} else {
						botHandler.escrever(digibankbot, update);
						sendResponse = digibankbot.execute(new SendMessage(update.message().chat().id(), "Não entendi, pode repetir por favor?"));
					}
					break;
				}

			}

		}

	}

}
