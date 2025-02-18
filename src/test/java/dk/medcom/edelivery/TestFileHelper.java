package dk.medcom.edelivery;

import org.springframework.core.io.ClassPathResource;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.UncheckedIOException;

public class TestFileHelper {

    public static DataHandler getDataHandler(String path, String type) {
        try (var is = new ClassPathResource(path).getInputStream()) {
            return new DataHandler(new ByteArrayDataSource(is, type));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
