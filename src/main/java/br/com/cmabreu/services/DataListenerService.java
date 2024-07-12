package br.com.cmabreu.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.cmabreu.ui.MainScreen;


@Service
public class DataListenerService {
	private Logger logger = LoggerFactory.getLogger( DataListenerService.class );	

	@PostConstruct
	private void init() {
		logger.info("init");

		try {
			logger.info("config file /layers/config.json");
			String content = readFile("/layers/config.json", StandardCharsets.UTF_8);
			JSONObject config = new JSONObject(content);
			
			MainScreen mainScreen = new MainScreen( config );
			mainScreen.startMap();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	
	private String readFile(String path, Charset encoding)  throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}	
}
