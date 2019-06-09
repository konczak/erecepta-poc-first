package pl.konczak.nzoz.ereceptapoc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import pl.konczak.nzoz.ereceptapoc.factory.EReceptaFactory;
import pl.konczak.nzoz.ereceptapoc.factory.EReceptaTemplateFactory;
import pl.konczak.nzoz.ereceptapoc.factory.ZapisReceptSoapRequestBodyFactory;
import pl.konczak.nzoz.ereceptapoc.factory.ZapisReceptSoapRequestBodyTemplateFactory;
import pl.konczak.nzoz.ereceptapoc.keystore.PrivateKeyData;
import pl.konczak.nzoz.ereceptapoc.util.XmlHelper;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SendEReceptaPoC {

    private final EReceptaTemplateFactory eReceptaTemplateFactory;
    private final EReceptaFactory eReceptaFactory;
    private final ZapisReceptSoapRequestBodyTemplateFactory zapisReceptSoapRequestBodyTemplateFactory;
    private final ZapisReceptSoapRequestBodyFactory zapisReceptSoapRequestBodyFactory;
    private final CsiozClient csiozClient;
    private final SignSoapMessageRequestAsCompany signSoapMessageRequestAsCompany;

    public void execute() throws Exception {
        String ereceptaTemplate = eReceptaTemplateFactory.getEReceptaTemplate();

        log.debug("ereceptaTemplate <{}>", ereceptaTemplate);

        String erecepta = eReceptaFactory.fillRecepta(ereceptaTemplate);

        log.debug("erecepta <{}>", erecepta);

        Document xmlWithErecepta = XmlHelper.convertStringToDocument(erecepta);

        log.debug("xmlWithErecepta <{}>", XmlHelper.convertDocumentToString(xmlWithErecepta));

        Document xmlWithSignedErecepta = signAsDoctor(xmlWithErecepta);

        String stringWithSignedErecepta = XmlHelper.convertDocumentToString(xmlWithSignedErecepta);
        log.debug("xmlWithSignedErecepta <{}>", stringWithSignedErecepta);

        byte[] bytesEncodedSignedErecepta = Base64.getEncoder().encode(stringWithSignedErecepta.getBytes(Charset.forName("UTF-8")));

        String stringEncodedSingedErecepta = new String(bytesEncodedSignedErecepta);
        log.debug("encodedErecepta <{}>", stringEncodedSingedErecepta);

        String zapisReceptSoapRequestBodyTemplate = zapisReceptSoapRequestBodyTemplateFactory.getZapisReceptSoapRequestBodyTemplate();

        log.debug("zapisReceptSoapRequestBodyTemplate <{}>", zapisReceptSoapRequestBodyTemplate);

        String soapRequestBody = zapisReceptSoapRequestBodyFactory.fillRequestBody(zapisReceptSoapRequestBodyTemplate, stringEncodedSingedErecepta);

        log.debug("soapRequestBody <{}>", soapRequestBody);

        SOAPMessage soapMessage = convertStringToSoapMessage(soapRequestBody);

        SOAPMessage signedSoapMessageRequest = signSoapMessageRequestAsCompany.signAsCompany(soapMessage);

        String singedSoapRequestAsString = soapMessageToString(signedSoapMessageRequest);

        log.debug("singedSoapRequestAsString <{}>", singedSoapRequestAsString);

        csiozClient.sendZapiszRecepty(singedSoapRequestAsString);
    }

    private Document signAsDoctor(final Document xmlWithErecepta) throws Exception {
        PrivateKeyData privateKeyData = new PrivateKeyData(
                "d:\\programowanie\\zarnow\\nzoz\\e-recepty\\KOMPLET_DANYCH_SZPL_NR_106\\Adam106 Leczniczy.p12",
                "UXG9DxASCm",
                "UXG9DxASCm");
        XmlSigner xmlSigner = new XmlSigner(privateKeyData);

        return xmlSigner.sign(xmlWithErecepta);

    }

    private SOAPMessage convertStringToSoapMessage(final String soapRequestBody) throws Exception {
        InputStream is = new ByteArrayInputStream(soapRequestBody.getBytes());
        SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);
        return request;
    }

    public String soapMessageToString(final SOAPMessage message) {
        String result = null;

        if (message != null) {
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                message.writeTo(baos);
                result = baos.toString();
            } catch (Exception e) {
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException ioe) {
                    }
                }
            }
        }
        return result;
    }

}
