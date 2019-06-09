package pl.konczak.nzoz.ereceptapoc;

import org.w3c.dom.Document;
import pl.konczak.nzoz.ereceptapoc.soaprequesttool.WSSecurityHandler;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class SignSoapMessageRequestAsCompany {

    private String resourceNameConfig =
            "src/main/resources/soapsignrequest/signature.properties";

    public SOAPMessage signAsCompany(final SOAPMessage soapMessage) throws Exception {
        WSSecurityHandler wsSecurityHandler = new WSSecurityHandler(loadConfig());

        Document document = wsSecurityHandler.signSoapMessage(soapMessage);

        return toSOAPMessage(document);
    }

    private Properties loadConfig() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(resourceNameConfig));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }

    private SOAPMessage toSOAPMessage(Document doc) throws Exception {
        DOMSource src = new DOMSource(doc);

        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage soapMsg = mf.createMessage();
        soapMsg.getSOAPPart().setContent(src);
        return soapMsg;
    }
}
