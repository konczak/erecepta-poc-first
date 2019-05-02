package pl.konczak.nzoz.ereceptapoc;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import pl.konczak.nzoz.ereceptapoc.keystore.Pkcs12KeyProvider;
import pl.konczak.nzoz.ereceptapoc.keystore.PrivateKeyData;
import pl.konczak.nzoz.ereceptapoc.keystore.PrivateKeyProvider;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

import static java.util.Collections.singletonList;
import static javax.xml.crypto.dsig.CanonicalizationMethod.INCLUSIVE;
import static javax.xml.crypto.dsig.SignatureMethod.RSA_SHA1;
import static javax.xml.crypto.dsig.Transform.ENVELOPED;

public class XmlSigner {

    private static final String Mechanism_Type_Dom = "DOM";
    private final XMLSignatureFactory factory = XMLSignatureFactory.getInstance(Mechanism_Type_Dom);

    private static final String Entire_Document = "";
    private final PrivateKeyProvider provider;

    public XmlSigner(PrivateKeyData keyData) throws IOException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, CertificateException {
        this.provider = new Pkcs12KeyProvider(factory, keyData);
    }

    public Document sign(Document document) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, IOException, UnrecoverableEntryException, CertificateException, ParserConfigurationException, SAXException, MarshalException, XMLSignatureException, TransformerException {
        SignedInfo signedInfo = createSignature();
        KeyInfo keyInfo = provider.loadKeyInfo();
        PrivateKey privateKey = provider.loadPrivateKey();
        sign(document, privateKey, signedInfo, keyInfo);
        return document;
    }

    private void sign(Document document, PrivateKey privateKey, SignedInfo signedInfo, KeyInfo keyInfo) throws MarshalException, XMLSignatureException {
        DOMSignContext signContext = new DOMSignContext(privateKey, document.getDocumentElement());
        XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);
        signature.sign(signContext);
    }

    private SignedInfo createSignature() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        DigestMethod digestMethod = factory.newDigestMethod(DigestMethod.SHA1, null);
        Transform transform = factory.newTransform(ENVELOPED, (TransformParameterSpec) null);
        Reference reference = factory.newReference(Entire_Document, digestMethod, singletonList(transform), null, null);
        SignatureMethod signatureMethod = factory.newSignatureMethod(RSA_SHA1, null);
        CanonicalizationMethod canonicalizationMethod = factory.newCanonicalizationMethod(INCLUSIVE, (C14NMethodParameterSpec) null);
        return factory.newSignedInfo(canonicalizationMethod, signatureMethod, singletonList(reference));
    }
}
