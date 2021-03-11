package ec.pymeapps.jpa.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class UdemySpringJpaApplication implements CommandLineRunner {

	@Autowired
	private BCryptPasswordEncoder passwordencoder;
	
	public static void main(String[] args) {
		SpringApplication.run(UdemySpringJpaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String password = "12345";
		
		String cripted = passwordencoder.encode(password);
		System.out.println(cripted);
		
		
	}

}
