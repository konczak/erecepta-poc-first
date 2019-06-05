package pl.konczak.nzoz.ereceptapoc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@SpringBootApplication
public class EreceptapocApplication
        implements CommandLineRunner {

    private final ClientAuthorizationPoC clientAuthorizationPoC;
    private final SendEReceptaPoC sendEReceptaPoC;

    public static void main(String[] args) {
        SpringApplication.run(EreceptapocApplication.class, args);
    }

    @Override
    public void run(String... args) {
//        first();
//        second();
//        third();
        fourth();
    }

    private void second() {
        try {
            sendEReceptaPoC.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void first() {
        try {
            clientAuthorizationPoC.gzi();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void third() {
        try {
            for (Provider provider : Security.getProviders()) {
                log.info("{}", provider.getInfo());
            }

            SecureRandom secureRandom = SecureRandom.getInstanceStrong();

            byte[] secrets = new byte[10];
            secureRandom.nextBytes(secrets);

            for (byte secret : secrets) {
                log.info("{}", secret);
            }

            log.info("Hex encoded {}", Hex.encodeHexString(secrets));

            //TODO how to recreate original byte array from String?
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fourth() {
        try {
            // InputStream certIs = new FileInputStream("d:\\programowanie\\zarnow\\nzoz\\e-recepty\\KOMPLET_DANYCH_SZPL_NR_106\\Adam106 Leczniczy.p12");
            InputStream certIs = new FileInputStream("d:\\programowanie\\zarnow\\nzoz\\e-recepty\\KOMPLET_DANYCH_SZPL_NR_106\\Podmiot_leczniczy_106-tls.p12");
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certIs, "UXG9DxASCm".toCharArray());
            Enumeration<String> enumeration = ks.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                System.out.println(alias);
                Certificate certificate = ks.getCertificate(alias);
                System.out.println(certificate);
            }

            System.out.println("-------- chain: ---------");
            enumeration = ks.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                System.out.println(alias);
                Certificate[] certificateChain = ks.getCertificateChain(alias);
                for (Certificate certificate : certificateChain) {
                    System.out.println(certificate);
                    System.out.println("finito");
                }
            }

            System.out.println("-------- CertPath: ---------");
            enumeration = ks.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                System.out.println(alias);
                List<Certificate> certificates = Arrays.asList(ks.getCertificateChain(alias));
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                CertPath certPath = certificateFactory.generateCertPath(certificates);
                System.out.println("certPath");
                System.out.println(certPath);
                //reversing order of certificates in array generates invalid CertPath
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
