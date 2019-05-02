package pl.konczak.nzoz.ereceptapoc.factory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EReceptaFactory {

    final String idLokalnePodmiotu = "2.16.840.1.113883.3.4424.2.7.201";
    final String idBiznesowePodmiotuExt = "000000926578";
    final String idBiznesowePodmiotuRoot = "2.16.840.1.113883.3.4424.2.3.1";
    final String peselPacjenta = "70032816894";
    final String idPracownikaExt = "7391208";
    final String miastoPodmiotu = "Warszawa";
    final String ulicaPodmiotu = "ul. Dubois";
    final String numerDomuPodmiotu = "5A";
    final String regon14 = "19330059000014";
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'yyyyMMddHHmmss'");

    public String fillRecepta(final String ereceptaTemplate) {
        LocalDateTime now = LocalDateTime.now();

        String formatDateTime = now.format(formatter);

        String id = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 22);

        return ereceptaTemplate
                .replace("@@numer_recepty@@", id)
                .replace("@@id_lokalne_podmiotu@@", idLokalnePodmiotu)
                .replace("@@data_wystawienia@@", formatDateTime)
                .replace("@@id_podmiotu@@", idBiznesowePodmiotuExt)
                .replace("@@id_podmiotu_root@@", idBiznesowePodmiotuRoot)
                .replace("@@pesel_pacjenta@@", peselPacjenta)
                .replace("@@id_pracownika_ext@@", idPracownikaExt)
                .replace("@@miasto_podmiotu@@", miastoPodmiotu)
                .replace("@@ulica_podmiotu@@", ulicaPodmiotu)
                .replace("@@numerDomu_podmiotu@@", numerDomuPodmiotu)
                .replace("@@regon14@@", regon14);
    }
}
