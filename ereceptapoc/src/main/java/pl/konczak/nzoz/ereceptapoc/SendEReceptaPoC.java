package pl.konczak.nzoz.ereceptapoc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import pl.konczak.nzoz.ereceptapoc.factory.EReceptaFactory;
import pl.konczak.nzoz.ereceptapoc.factory.EReceptaTemplateFactory;
import pl.konczak.nzoz.ereceptapoc.keystore.PrivateKeyData;
import pl.konczak.nzoz.ereceptapoc.util.XmlHelper;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SendEReceptaPoC {

    private final EReceptaTemplateFactory eReceptaTemplateFactory;
    private final EReceptaFactory eReceptaFactory;

    public void execute() throws Exception {
        String ereceptaTemplate = eReceptaTemplateFactory.getEReceptaTemplate();

        log.debug("ereceptaTemplate <{}>", ereceptaTemplate);

        String erecepta = eReceptaFactory.fillRecepta(ereceptaTemplate);

        log.debug("erecepta <{}>", erecepta);

        Document xmlWithErecepta = XmlHelper.convertStringToDocument(erecepta);

        log.debug("xmlWithErecepta <{}>", XmlHelper.convertDocumentToString(xmlWithErecepta));

        Document xmlWithSignedErecepta = sign(xmlWithErecepta);

        log.info("xmlWithSignedErecepta <{}>", XmlHelper.convertDocumentToString(xmlWithSignedErecepta));
    }

    private Document sign(final Document xmlWithErecepta) throws Exception {
        PrivateKeyData privateKeyData = new PrivateKeyData();
        XmlSigner xmlSigner = new XmlSigner(privateKeyData);

        return xmlSigner.sign(xmlWithErecepta);

    }

}
