package pl.konczak.nzoz.ereceptapoc.soaprequesttool;


import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.w3c.dom.Document;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.Properties;


/**
 * Created by sidney.oliveira on 11/11/2015.
 */
public class WSSecurityHandler {

    private Properties properties;

    public WSSecurityHandler(Properties properties) {
        this.properties = properties;
    }

    public String handleMessage(SOAPMessage message) {
        String ret;
        try {
            Document doc = message.getSOAPBody().getOwnerDocument();
            Crypto crypto = CryptoFactory.getInstance(properties); //File

            WSSecSignature sign = new WSSecSignature();
            sign.setUserInfo(properties.getProperty("org.apache.ws.security.crypto.merlin.keystore.alias"), properties.getProperty("privatekeypassword"));
            sign.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE); // Binary Security Token - SecurityTokenReference
            sign.setUseSingleCertificate(true);
            sign.setDigestAlgo(DigestMethod.SHA256);

            WSSecHeader secHeader = new WSSecHeader();
            secHeader.insertSecurityHeader(doc);
            Document signedDoc = sign.build(doc, crypto, secHeader);

            ret = org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(signedDoc);

        } catch (SOAPException e) {
            e.printStackTrace();
            return null;
        } catch (WSSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: " + e.getMessage());
        }
        return ret;
    }

}

