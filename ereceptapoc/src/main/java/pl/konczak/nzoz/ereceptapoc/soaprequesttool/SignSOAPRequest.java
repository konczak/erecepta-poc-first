package pl.konczak.nzoz.ereceptapoc.soaprequesttool;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class SignSOAPRequest {
    protected String resourceNameConfig =
            "src/main/resources/soapsignrequest/signature.properties";
    protected String resourceNameMensagem =
            "src/main/resources/soapsignrequest/message.txt";
    private String originalMessage = "";

    public void setResourceNameConfig(String resourceNameConfig) {
        this.resourceNameConfig = resourceNameConfig;
    }

    public String sign(String originalMsg) throws Exception {
        this.originalMessage = originalMsg;
        return this.sign();
    }

    public String sign(String resourceNameConfig, String originalMsg) throws Exception {
        this.resourceNameConfig = resourceNameConfig;
        this.originalMessage = originalMsg;
        return this.sign();
    }

    public String sign() throws Exception {

        InputStream file;
        if (this.originalMessage.equals("")) {
            file = new FileInputStream(this.resourceNameMensagem);
        } else {
            file = new ByteArrayInputStream(this.originalMessage.getBytes());
        }

        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(null, file);

        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

        Name nome = soapEnvelope.createName("id", "soapsec",
                "http://schemas.xmlsoap.org/soap/security/2000-12");

        SOAPBody soapBody = soapEnvelope.getBody();
        soapBody.addAttribute(nome, "Body");

        WSSecurityHandler handler = new WSSecurityHandler(loadConfig());

        return handler.handleMessage(soapMessage);
    }

    protected Properties loadConfig() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(resourceNameConfig));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }
}
