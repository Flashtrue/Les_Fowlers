package ch.hearc.cafheg;

import org.springframework.boot.SpringApplication;

public class TestCafhegApplication {

    static void main(String[] args) {
        SpringApplication.from(CafhegApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
