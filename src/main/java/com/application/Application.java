package com.application;

import com.application.controllers.api.ApiRestController;
import com.application.services.api.ApiRestService;
import com.application.services.mutant.MutantVerificationService;
import com.application.services.stats.MutantAndHumanStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.application"})
public class Application {
	public static Boolean isLoaded = false;
	public static void main(String[] args) {
		final Logger log = LoggerFactory.getLogger(Application.class);

		try( AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				Application.class)) {
			new ApiRestController(
					context.getBean(ApiRestService.class),
					context.getBean(MutantVerificationService.class),
					context.getBean(MutantAndHumanStatsService.class)
			);
			context.registerShutdownHook();
			isLoaded = true;
		}
		catch (Exception e){
			log.error("Ocurrió un error al iniciar la aplicación. ", e);
		}
	}
}