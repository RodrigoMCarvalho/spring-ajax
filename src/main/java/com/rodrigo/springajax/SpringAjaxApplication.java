package com.rodrigo.springajax;

import com.rodrigo.springajax.domain.SocialMetaTag;
import com.rodrigo.springajax.service.SocialMetaTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAjaxApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringAjaxApplication.class, args);
	}

	@Autowired
	private SocialMetaTagService service;

	@Override
	public void run(String... args) throws Exception {
		/*SocialMetaTag og = service.getOpenGraphByUrl("https://www.udemy.com/course/spring-boot-mvc-com-ajax/");
		System.out.println(og.toString());

        SocialMetaTag twitter = service.getTwitterCardByUrl("https://www.udemy.com/course/spring-boot-mvc-com-ajax/");
        System.out.println(twitter.toString());

		SocialMetaTag tag = service.getSocialMetaTagByUrl("https://www.udemy.com/course/spring-boot-mvc-com-ajax/");
		System.out.println(tag);

		SocialMetaTag tag2 = service.getSocialMetaTagByUrl("https://www.pichau.com.br/hardware/gabinete-gamer-cougar-gemini-m-silver-385tmb0-0002");
		System.out.println(tag2);*/
	}
}
