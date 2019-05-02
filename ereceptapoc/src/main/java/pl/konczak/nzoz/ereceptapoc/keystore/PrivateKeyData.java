package pl.konczak.nzoz.ereceptapoc.keystore;

public class PrivateKeyData {

    public final String pathToKeystore;
    public final char[] passphraseForKeystore;
    public final char[] passphraseForKey;

    public PrivateKeyData() {
        this.pathToKeystore = "C:\\work\\p1\\e-recepty\\KOMPLET_DANYCH_SZPL_NR_106\\Adam106 Leczniczy.p12";
        this.passphraseForKeystore = "UXG9DxASCm".toCharArray();
        this.passphraseForKey = "UXG9DxASCm".toCharArray();
    }

//    public PrivateKeyData(String pathToKeystore, String passphraseForKeystore, String passphraseForKey) {
//        this.pathToKeystore = pathToKeystore;
//        this.passphraseForKeystore = passphraseForKeystore.toCharArray();
//        this.passphraseForKey = passphraseForKey.toCharArray();
//    }
}
