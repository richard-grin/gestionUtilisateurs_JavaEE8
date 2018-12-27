package ejb;

import fr.unice.formations.entite.Formation;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.mail.MailSessionDefinition;
import javax.sql.DataSource;
import util.HashMdp;

/**
 * Configuration de la source de données pour l'utiliser pour la sécurité.
 *
 * @author grin
 */
@Singleton
@Startup
@DataSourceDefinition(
        className = "org.apache.derby.jdbc.ClientDataSource",
        name = "java:app/jdbc/formationsPourJavaEE8",
        serverName = "localhost",
        portNumber = 1527,
        user = "toto", // nom et
        password = "toto", // mot de passe que vous avez donnés lors de la création de la base de données
        databaseName = "formationsPourJavaEE8"
)
// Une définition de session peut aussi être déclarée dans web.xml comme dans 
// https://github.com/javaeekickoff/java-ee-kickoff-app/blob/master/src/main/webapp/WEB-INF/web.xml
// Définition OK pour email free
@MailSessionDefinition(
        name = "java:app/mail/free",
        host = "smtp.free.fr",
        user = "richard.grin",
        from = " richard.grin@free.fr",
        storeProtocol = "imap",
        transportProtocol = "smtp"
)

// Définition pour email unice 
//@MailSessionDefinition(
//        name = "java:app/mail/unice",
//        host = "smtp.unice.fr",
//        user = "grin",
//        password = "******",
//        from = " Richard.Grin@unice.fr",
//        transportProtocol = "smtps"
//)
public class Init {

  @EJB
  private FormationFacade formationFacade;

  @Resource(lookup = "java:app/jdbc/formationsPourJavaEE8")
  private DataSource dataSource;

  @Inject
  // Pour coder le mot de passe
  private HashMdp passwordHash;

  /**
   * Génère les données de la base de l'entrepôt des identités si elles
   * n'existent pas déjà.
   */
  @PostConstruct
  public void init() {
    try (Connection c = dataSource.getConnection()) {
      // Si la table des logins n'existe pas déjà, créer les tables
      if (!existe(c, "LOGIN")) { // Attention, la casse compte !!!
        System.out.println("Création des tables");
        // Remarque : la table SEQUENCE est créée automatiquement ; pas besoin de la créer
        execute(c, "CREATE TABLE personne (id INTEGER PRIMARY KEY, nom VARCHAR(50), prenom VARCHAR(50))");
        execute(c, "CREATE TABLE login (login VARCHAR(50) PRIMARY KEY, code varchar(255), email varchar(255), mot_de_passe VARCHAR(160), statut varchar(10), personne_id INTEGER references personne)");
        execute(c, "CREATE TABLE groupe (id INTEGER PRIMARY KEY, nom_groupe varchar(255))");
        execute(c, "CREATE TABLE login_groupe (login_login VARCHAR(50) references login, groupe_id INTEGER references groupe)");
      } else {
        System.out.println("Tables existent déjà");
      }

      // Si la table LOGIN n'est pas vide, ne rien faire
      if (vide(c, "login")) {
        System.out.println("============ Table login vide ; initialisation des données dans ls tables");
        // La table LOGIN est vide
//      Map<String, String> parameters = new HashMap<>();
//      parameters.put("Pbkdf2PasswordHash.Iterations", "3072");
//      parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
//      parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
//      passwordHash.initialize(parameters);

//      execute(c, "CREATE TABLE login (login VARCHAR(64) PRIMARY KEY, mot_de_passe VARCHAR(255))");
//      execute(c, "CREATE TABLE groupe (login VARCHAR(64), groupe VARCHAR(64))");
        // Le mot de passe haché :
        String hashMdp = passwordHash.generate("toto");
//      System.out.println("******===== TAILLE mot de passe haché : " + hashMdp.length());

        execute(c, "INSERT INTO personne (id, nom, prenom) VALUES(1, 'Grin', 'Richard')");
        execute(c, "INSERT INTO login (LOGIN, MOT_DE_PASSE, email, statut, personne_id) VALUES('ric', '"
                + hashMdp + "', 'grin@unice.fr', 'ok', 1)");

        execute(c, "INSERT INTO groupe(id, nom_groupe) VALUES(1, 'admin')");
        execute(c, "INSERT INTO groupe(id, nom_groupe)  VALUES(2, 'inscrit')");
        execute(c, "INSERT INTO login_groupe(login_login, groupe_id) VALUES('ric', 1)");
        execute(c, "INSERT INTO login_groupe(login_login, groupe_id) VALUES('ric', 2)");

        execute(c, "INSERT INTO personne (id, nom, prenom) VALUES(2, 'Bernard', 'Pierre')");
        execute(c, "INSERT INTO login (LOGIN, MOT_DE_PASSE, email, statut, personne_id) VALUES('toto', '"
                + hashMdp + "', 'grin@unice.fr', 'ok', 2)");

        execute(c, "INSERT INTO login_groupe(login_login, groupe_id) VALUES('toto', 2)");
//      execute(c, "INSERT INTO caller_groups VALUES('juneau', 'group2')");
      }
      if (vide(c, "formation")) {
        System.out.println("Initialisation des données pour les formations");
        initFormations();
      }
    } catch (SQLException e) {
      // Pour les logs du serveur d'application
      e.printStackTrace();
    }
  }

  private void execute(Connection c, String query) {
    try (PreparedStatement stmt = c.prepareStatement(query)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      // Pour les logs du serveur d'application
      e.printStackTrace();
    }
  }

  /**
   * Teste si une table existe déjà.
   *
   * @param connection
   * @param nomTable
   * @return true ssi la table existe.
   * @throws SQLException
   */
  private static boolean existe(Connection connection, String nomTable)
          throws SQLException {
    boolean existe;
    DatabaseMetaData dmd = connection.getMetaData();
    try (ResultSet tables = dmd.getTables(connection.getCatalog(), null, nomTable, null)) {
      existe = tables.next();
    }
    return existe;
  }

  /**
   *
   * @return true ssi la table LOGIN est vide.
   */
  private boolean vide(Connection c, String nomTable) throws SQLException {
    Statement stmt = c.createStatement();
    ResultSet rset = stmt.executeQuery("select count(1) from " + nomTable);
    rset.next();
    int nb = rset.getInt(1);
    return nb == 0;
  }

  /**
   * Insertion de quelques formations.
   */
  private void initFormations() {
    Formation f1 = new Formation("Java", "France");
    Formation f2 = new Formation("Java EE", "Egypte");
    Formation f3 = new Formation("Python", "France");
    formationFacade.create(f1);
    formationFacade.create(f2);
    formationFacade.create(f3);
  }
}
