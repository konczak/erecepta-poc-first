package pl.konczak.nzoz.ereceptapoc.factory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class ZapisReceptSoapRequestBodyTemplateFactory {

    public String getZapisReceptSoapRequestBodyTemplate() throws Exception {
        Path path = Paths.get(ClassLoader.getSystemResource("zapis-recept-soap.xml").toURI());
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }
}
