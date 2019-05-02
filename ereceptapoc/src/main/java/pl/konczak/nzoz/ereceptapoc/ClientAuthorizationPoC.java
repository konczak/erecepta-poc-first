package pl.konczak.nzoz.ereceptapoc;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientAuthorizationPoC {

    private static final String TEST_URL = "https://client.example.com:8443";

    public void gzi() throws GeneralSecurityException, IOException {
        Path clientCertStore = Paths.get("C:\\work\\p1\\e-recepty\\poc\\clientcertstore.pfx");
        Path trustedCaStore = Paths.get("C:\\work\\p1\\e-recepty\\poc\\trustedcastore.jks");

        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(clientCertStore.toFile(), "gucio".toCharArray(), "gucio".toCharArray())
                .loadTrustMaterial(trustedCaStore.toFile(), "trusted".toCharArray())
                .build();
        try (CloseableHttpClient httpclient = HttpClients
                .custom().setSSLContext(sslContext).build();
             CloseableHttpResponse closeableHttpResponse = httpclient.execute(
                     new HttpGet(URI.create(TEST_URL)))) {
            log.info(closeableHttpResponse.getStatusLine().toString());
            HttpEntity entity = closeableHttpResponse.getEntity();
            try (InputStream content = entity.getContent();
                 ReadableByteChannel src = Channels.newChannel(content);
                 WritableByteChannel dest = Channels.newChannel(System.out)) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
                while (src.read(buffer) != -1) {
                    buffer.flip();
                    dest.write(buffer);
                    buffer.compact();
                }
                buffer.flip();
                while (buffer.hasRemaining())
                    dest.write(buffer);
            }
        }
    }
}
