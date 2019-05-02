package pl.konczak.nzoz.ereceptapoc.factory;

import org.springframework.stereotype.Component;

@Component
public class ZapisReceptSoapRequestBodyFactory {

    private final String idBiznesowePodmiotuRoot = "2.16.840.1.113883.3.4424.2.3.1";
    private final String idBiznesowePodmiotuExt = "000000926578";
    private final String idPracownikaRoot = "2.16.840.1.113883.3.4424.1.6.2";
    private final String idPracownikaExt = "7391208";
    private final String idMiejscaPracyRoot = "2.16.840.1.113883.3.4424.2.3.2";
    private final String idMiejscaPracyExt = "4";
    private final String rolaBiznesowa = "LEKARZ_LEK_DENTYSTA_FELCZER";

    public String fillRequestBody(String zapisReceptSoapRequestBodyTemplate, String erecepta) {
        return zapisReceptSoapRequestBodyTemplate.replace("@@recepta@@", erecepta)
                .replace("@@idBiznesowePodmiotuRoot@@", idBiznesowePodmiotuRoot)
                .replace("@@idBiznesowePodmiotuExt@@", idBiznesowePodmiotuExt)
                .replace("@@idPracownikaRoot@@", idPracownikaRoot)
                .replace("@@idPracownikaExt@@", idPracownikaExt)
                .replace("@@idMiejscaPracyRoot@@", idMiejscaPracyRoot)
                .replace("@@idMiejscaPracyExt@@", idMiejscaPracyExt)
                .replace("@@rolaBiznesowa@@", rolaBiznesowa);
    }
}
