package ch.hearc.cafheg;

import ch.hearc.cafheg.domain.allocations.AllocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

public class TestCafhegApplication {

    private static final Logger logger = LoggerFactory.getLogger(TestCafhegApplication.class);

     static void main(String[] args) {

        SpringApplication.from(CafhegApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);




    }
}