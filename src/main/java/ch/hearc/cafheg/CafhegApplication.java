package ch.hearc.cafheg;

import ch.hearc.cafheg.infrastructure.persistence.Database;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CafhegApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(CafhegApplication.class, args);
        startDatabase(ctx.getEnvironment());
        System.out.println("""
                                   
                                   Swagger UI:\t http://localhost:8080/api/swagger-ui/index.html
                                   
                                   """);
    }

    private static void startDatabase(Environment env) {
        String jdbcUrl = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        if (jdbcUrl == null || username == null || password == null) {
            throw new IllegalStateException(
                    "Database configuration is missing. Please check your application.yaml or environment " +
                            "variables.");
        }

        Database database = new Database();
        database.start(jdbcUrl, username, password);
    }

}
