package dk.medcom.edelivery;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class MedComGateway {

    public static void main(String[] args) {
        SpringApplication.run(MedComGateway.class, args);
    }

}
