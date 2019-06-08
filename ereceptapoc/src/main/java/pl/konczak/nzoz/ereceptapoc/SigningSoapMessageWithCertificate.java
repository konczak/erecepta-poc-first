package pl.konczak.nzoz.ereceptapoc;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.springframework.stereotype.Component;

@Component
public class SigningSoapMessageWithCertificate {

    public void execute() throws Exception {
        org.apache.xml.security.Init.init();

        String keystoreType = "JKS";
        String keystoreFile = "src/main/resources/pushpalanka/PushpalankaKeystore.jks";
        String keystorePass = "pushpalanka";
        String privateKeyAlias = "pushpalanka";
        String privateKeyPass = "pushpalanka";
        String certificateAlias = "pushpalanka";
        File signatureFile = new File("src/main/resources/pushpalanka/signature.xml");
        Element element = null;
        String BaseURI = signatureFile.toURI().toURL().toString();
        //SOAP envelope to be signed
        File attachmentFile = new File("src/main/resources/pushpalanka/sample.xml");

        //get the private key used to sign, from the keystore
        KeyStore ks = KeyStore.getInstance(keystoreType);
        FileInputStream fis = new FileInputStream(keystoreFile);
        ks.load(fis, keystorePass.toCharArray());
        PrivateKey privateKey =

                (PrivateKey) ks.getKey(privateKeyAlias, privateKeyPass.toCharArray());
        //create basic structure of signature
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(attachmentFile);
        XMLSignature sig =
                new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);

        //optional, but better
        element = doc.getDocumentElement();
        element.normalize();
        element.getElementsByTagName("soap:Header").item(0).appendChild(sig.getElement());

        {
            Transforms transforms = new Transforms(doc);
            transforms.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
            //Sign the content of SOAP Envelope
            sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

            //Adding the attachment to be signed
            File attachementFile = new File("d:\\programowanie\\zarnow\\nzoz\\e-recepty\\erecepta-poc-first\\ereceptapoc\\src\\main\\resources\\pushpalanka\\attachment.xml");
            String attachementFileBaseURI = signatureFile.toURI().toURL().toString();
            sig.addDocument(attachementFileBaseURI, transforms, Constants.ALGO_ID_DIGEST_SHA1);

        }

        //Signing procedure
        {
            X509Certificate cert =
                    (X509Certificate) ks.getCertificate(certificateAlias);
            sig.addKeyInfo(cert);
            sig.addKeyInfo(cert.getPublicKey());
            sig.sign(privateKey);
        }

        //write signature to file
        FileOutputStream f = new FileOutputStream(signatureFile);
        XMLUtils.outputDOMc14nWithComments(doc, f);
        f.close();
    }
}
