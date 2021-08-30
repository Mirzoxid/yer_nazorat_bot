package uz.pr.yer_nazorat_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YerNazoratBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(YerNazoratBotApplication.class, args);
    }

}
