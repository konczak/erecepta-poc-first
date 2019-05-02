package pl.konczak.nzoz.ereceptapoc.factory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class EReceptaTemplateFactory {

    public String getEReceptaTemplate() throws Exception {
        Path path = Paths.get(ClassLoader.getSystemResource("recepta-poprawna-1.3.1.xml").toURI());
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }
}
