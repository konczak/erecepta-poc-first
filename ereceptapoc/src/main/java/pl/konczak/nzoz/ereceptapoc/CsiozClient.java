package pl.konczak.nzoz.ereceptapoc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CsiozClient {

    private static final String CSIOZ_OBSLUGA_RECEPT_WS = "https://ws-int-p1.csioz.gov.pl/services/ObslugaReceptyWS";
    private final Path clientCertStore;
    private final Path trustedCaStore;

    public CsiozClient() {
        clientCertStore = Paths.get("C:\\work\\p1\\e-recepty\\KOMPLET_DANYCH_SZPL_NR_106\\Projekt_testow_soapui_dla_uslug_P2_e-Recepta_P2I6.1_20190416\\keys\\Podmiot_leczniczy_106-tls.p12");
        trustedCaStore = Paths.get("C:\\work\\p1\\e-recepty\\KOMPLET_DANYCH_SZPL_NR_106\\Projekt_testow_soapui_dla_uslug_P2_e-Recepta_P2I6.1_20190416\\keys\\Podmiot_leczniczy_106-wss.p12");
    }

    public String sendZapiszRecepty(String zapiszReceptyRequestBody) throws Exception {
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(new FileInputStream("C:\\work\\p1\\e-recepty\\KOMPLET_DANYCH_SZPL_NR_106\\Projekt_testow_soapui_dla_uslug_P2_e-Recepta_P2I6.1_20190416\\keys\\Podmiot_leczniczy_106-tls.p12"), "UXG9DxASCm".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, "UXG9DxASCm".toCharArray());
        KeyManager[] kms = kmf.getKeyManagers();

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream("C:\\work\\p1\\e-recepty\\KOMPLET_DANYCH_SZPL_NR_106\\Projekt_testow_soapui_dla_uslug_P2_e-Recepta_P2I6.1_20190416\\keys\\csiozTrustStore.jks"), "changeit".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        TrustManager[] tms = tmf.getTrustManagers();


        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kms, tms, new SecureRandom());
//                SSLContexts.custom()
//                .setProtocol("TLS")
//                .loadKeyMaterial(clientCertStore.toFile(), "UXG9DxASCm".toCharArray(), "UXG9DxASCm".toCharArray())
//                .loadTrustMaterial(trustedCaStore.toFile(), "UXG9DxASCm".toCharArray())
//                .build();

        StringEntity entity = new StringEntity(zapiszReceptyRequestBody);
        HttpPost httpPost = new HttpPost(URI.create(CSIOZ_OBSLUGA_RECEPT_WS));
        httpPost.setEntity(entity);
        httpPost.addHeader("Content-Type", "Content-Type: text/xml; charset=UTF-8");

        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();
        CloseableHttpResponse closeableHttpResponse = httpclient.execute(httpPost);
        String body = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
//        String body = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), "UTF-8");

        log.info("status <{}> body <{}>", closeableHttpResponse.getStatusLine().toString(), body);


        return "TODO";
    }

}
