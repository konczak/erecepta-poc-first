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

    public void execute() throws Exception {
        String ereceptaTemplate = eReceptaTemplateFactory.getEReceptaTemplate();

        log.debug("ereceptaTemplate <{}>", ereceptaTemplate);

        String erecepta = eReceptaFactory.fillRecepta(ereceptaTemplate);

        log.debug("erecepta <{}>", erecepta);

        Document xmlWithErecepta = XmlHelper.convertStringToDocument(erecepta);

        log.debug("xmlWithErecepta <{}>", XmlHelper.convertDocumentToString(xmlWithErecepta));

        Document xmlWithSignedErecepta = sign(xmlWithErecepta);

        String stringWithSignedErecepta = XmlHelper.convertDocumentToString(xmlWithSignedErecepta);
        log.debug("xmlWithSignedErecepta <{}>", stringWithSignedErecepta);

        byte[] bytesEncodedSignedErecepta = Base64.getEncoder().encode(stringWithSignedErecepta.getBytes(Charset.forName("UTF-8")));

        String stringEncodedSingedErecepta = new String(bytesEncodedSignedErecepta);
        log.debug("encodedErecepta <{}>", stringEncodedSingedErecepta);

        String zapisReceptSoapRequestBodyTemplate = zapisReceptSoapRequestBodyTemplateFactory.getZapisReceptSoapRequestBodyTemplate();

        log.debug("zapisReceptSoapRequestBodyTemplate <{}>", zapisReceptSoapRequestBodyTemplate);

        String soapRequestBody = zapisReceptSoapRequestBodyFactory.fillRequestBody(zapisReceptSoapRequestBodyTemplate, stringEncodedSingedErecepta);

        log.debug("soapRequestBody <{}>", soapRequestBody);

        csiozClient.sendZapiszRecepty(soapRequestBody);
    }

    private Document sign(final Document xmlWithErecepta) throws Exception {
        PrivateKeyData privateKeyData = new PrivateKeyData();
        XmlSigner xmlSigner = new XmlSigner(privateKeyData);

        return xmlSigner.sign(xmlWithErecepta);

    }

}
