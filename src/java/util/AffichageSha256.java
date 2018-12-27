package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;

public class AffichageSha256 {

  /**
   * Affiche le SHA-256 à utiliser pour mettre dans Oracle, MySQL ou Java DB.
   * Teste aussi la classe Sha256.
   * Attention, utilise Base64 et pas Hex.
   */
  public static void main(String[] args) {
    // Juste pour information
    System.out.println("charset par défaut : " + Charset.defaultCharset());

    // Utilisation en entrée d'un tableau de char
    System.out.println("Utilisation en entrée d'un tableau de char");
    char[] mdpPasEncrypte = {'t', 'o', 't', 'o'};
    byte[] mdpEncrypte = Sha256.hash(mdpPasEncrypte);
    // Divers affichage de mdpEncrypte
    System.out.println(Arrays.toString(mdpEncrypte));
    for (byte b : mdpEncrypte) {
      System.out.printf("%h;", b);
    }
    System.out.println();
    System.out.println("Taille du tableau encrypté :" + mdpEncrypte.length);
    /*
     * C'est cet affichage qu'il faut utiliser pour entrer le mot
     * de passe dans Oracle ou MySQL.
     * Avec MySQL, il faut mettre "Ox" devant. Par exemple (mot de passe "toto"),
     * String sqlPourMdpGrin = 
    "update personne " +
    "set mot_de_passe_sha256 = 0xf31f7a65e315586ac198bd798b6629ce4903d0899476d5741a9f32e2e521b6a66," +
    "    login = 'grin', roles = 'admin', nb_plaquettes = 1, " +
    "    envoi_pgm_par_mail = 'o' " +
    "where nom = 'Grin'";
     * Déclaration dans la table personne de MySQL :
     * mot_de_passe_sha256 binary(32)
     * Avec Oracle :
     * String mdpGrin = 
    "update personne " +
    "set mot_de_passe_sha256 = '31f7a65e315586ac198bd798b6629ce4903d0899476d5741a9f32e2e521b6a66'," +
    "    login = 'grin', roles = 'admin', nb_plaquettes = 1, " +
    "    envoi_pgm_par_mail = 'o' " +
    "where nom = 'Grin'";
     * Déclaration dans la table personne de Oracle :
     * mot_de_passe_sha256 raw(64)
     * Avec Derby :
     * update utilisateur
     * set mot_de_passe = X'31f7a65e315586ac198bd798b6629ce4903d0899476d5741a9f32e2e521b6a66'
     * where id = 1
     * Type la colonne "CHAR(32) FOR BIT DATA" (taille 32). Est-ce binary(32) marche aussi ? Non
     */
    for (byte b : mdpEncrypte) {
      System.out.printf("%02x", b);
    }
    System.out.println();
    System.out.println("Utilisation en entrée d'une String");
    mdpEncrypte = Sha256.hash("toto");
    // Divers affichage de mdpEncrypte
    System.out.println(Arrays.toString(mdpEncrypte));
    for (byte b : mdpEncrypte) {
      System.out.printf("%h;", b);
    }
    System.out.println();
//    for (byte b : mdpEncrypte) {
//      // Indispensable d'ajouter d'éventuels 0 au début, par exemple pour la valeur 8
//      System.out.printf("%02x", b);
//    }
    // En passant par une String (qu'on affiche)
    System.out.println("En passant par une String :");
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    for (byte b : mdpEncrypte) {
      pw.printf("%02x", b);
    }
    System.out.println(sw);
    System.out.println("Avec méthode hashToStringHex :");
    String s = Sha256.hashToStringHex("toto");
    System.out.println(s + "; taille=" + s.length());
    System.out.println("Avec méthode hashToStringBase64 :");
    s = Sha256.hashToStringBase64("toto");
    System.out.println(s + "; taille=" + s.length());
    // Calcul de la taille de Base64 pour vérification
    int input_size = mdpEncrypte.length;
    int code_size = ((input_size * 4) / 3);
    int padding_size = (input_size % 3 == 0) ? (3 - (input_size % 3)) : 0;
    int crlfs_size = 2 + (2 * (code_size + padding_size) / 72);
    int total_size = code_size + padding_size + crlfs_size;
    System.out.println("Taille calculée = " + total_size);
  }
}
