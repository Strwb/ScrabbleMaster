package run;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootConsoleApplication
        implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) {

        for (int i = 0; i < 10; ++i) {
            System.out.println("DUPA");
        }
    }
}