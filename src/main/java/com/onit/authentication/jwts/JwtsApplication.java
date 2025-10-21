package com.onit.authentication.jwts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
/**
 * JwtsApplication
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */
@EnableJpaAuditing
@SpringBootApplication
public class JwtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtsApplication.class, args);
	}

}
