package com.es.api;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.es.api.service.commune.CommuneService;


@SpringBootApplication
@ComponentScan(basePackages = "com.es.api.service")
public class Application implements CommandLineRunner {

    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    private CommuneService communeService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
    public void run(String... arg0) throws Exception {
		
    	logger.info("Starting process");
    	
    	try {
    	    
    		logger.info("Initializing index");
		    communeService.init();
		    logger.info("Index initialized");
		    
		    logger.info("Starting populating index");
		    communeService.insertCommunes();
		    logger.info("Index populated");
		
    	} catch (Exception e) {

		    logger.error("Error found while processing index", e);
		}
    	
    	logger.info("Done processing");
    }
}
