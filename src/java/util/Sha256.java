package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
// Nécessite la librairie commons codec d'Apache
//import org.apache.commons.codec.binary.Base64;
import java.util.Base64;

public class Sha256 {
  
  /**
   * Renvoie le SHA-256 d'un mot de passe sous la forme d'un tableau d'octets.
   * Le mot de passe est passé sous la forme d'une String.
   */
  public static byte[] hash(String source) {
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      return sha256.digest(source.getBytes());
    } 
    catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
  
  /**
   * Renvoie le SHA-256 d'un mot de passe sous la forme d'une String.
   * Utilse Base64 pour transformer le tableau d'octets en String.
   * Le mot de passe est passé sous la forme d'une String.
   */
  public static String hashToStringBase64(String source) {
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      return Base64.getEncoder().encodeToString(hash(source));
//      return Base64.encodeBase64String(hash(source));
    } 
    catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
  
  /**
   * Renvoie le SHA-256 d'un mot de passe sous la forme d'une String.
   * Utilse Hex pour transformer le tableau d'octets en String.
   * Le mot de passe est passé sous la forme d'une String.
   * @param source
   * @return 
   */
  public static String hashToStringHex(String source) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    for (byte b : hash(source)) {
      pw.printf("%02x", b);
    }
    return sw.toString();
  }
  
  
  /**
   * Renvoie le SHA-256 d'un mot de passe sous la forme d'un tableau d'octets.
   * Le mot de passe est passé sous la forme d'un tableau de char.
   * C'est le cas le plus fréquent quand on travaille avec des mots de passe.
   * Voir, par exemple, la classe JPasswordField de Swing, méthode getPassword().
   * Les caractères sont codés en UTF-8.
   */
  public static byte[] hash(char[] source) {
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      return sha256.digest(charToByte(source));
    } 
    catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
  
  /**
   * Renvoie le SHA-256 d'un mot de passe sous la forme d'un tableau d'octets.
   * Le mot de passe est passé sous la forme d'un tableau de char.
   * C'est le cas le plus fréquent quand on travaille avec des mots de passe.
   * Voir, par exemple, la classe JPasswordField de Swing, méthode getPassword().
   * @param source
   * @param charset le charset qui code les char de la source
   * @return tableau d'octets du hash de la source par SHA-256.
   */
  public static byte[] hash(char[] source, Charset charset) {
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      return sha256.digest(charToByte(source, charset));
    } 
    catch (NoSuchAlgorithmException e) {
      return null;
    }
  }  
  
  /**
   * Transforme un tableau de char en un tableau d'octets.
   * Utilise un charset UTF-8.
   */
  public static byte[] charToByte(char[] source) {
    return charToByte(source, Charset.forName("UTF-8"));
  }

  /**
   * Transforme un tableau de char en un tableau d'octets.
   * @param source le tableau de char.
   * @param charset utilisé pour les caractères du tableau de char.
   * @return tableau d'octets qui représente le codage du tableau de char.
   */
  public static byte[] charToByte(char[] source, Charset charset) {
    CharsetEncoder encoder = charset.newEncoder();
    ByteBuffer bbuf = null;
    try {
      bbuf = encoder.encode(CharBuffer.wrap(source));
    } catch (CharacterCodingException e) {
      e.printStackTrace();
    }
    return bbuf.array();
  }

}

