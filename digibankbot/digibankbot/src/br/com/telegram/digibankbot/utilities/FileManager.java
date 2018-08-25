package br.com.telegram.digibankbot.utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author João Marcos da Costa, André Aparecido de Souza
 * @version 1.0
 *
 */

public class FileManager {
	
	private final static String ARQUIVO;
	private static BufferedWriter bufferedWriter;

	static {
		String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		ARQUIVO = "digibankbot_" + data + "_" + System.currentTimeMillis() + ".txt";
		gerarArquivo();
	}

	private static void gerarArquivo() {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(ARQUIVO));
		} catch (IOException e) {
			e.printStackTrace();
			//System.out.println("Erro na geração do arquivo de log.");
		}
	}

	public static void escrever(String linha) {

		try {
			String novaLinha = System.getProperty("line.separator");
			bufferedWriter.append(linha + novaLinha);
			bufferedWriter.flush();
		} catch (IOException e) {
			System.out.println("Erro ao escrever no arquivo de log.");
		}

	}
	
}
