package config;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

/**
 * Configuration de l'entrepôt d'identité.
 *
 * @author grin
 */
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "java:app/jdbc/formationsPourJavaEE8",
        callerQuery = "select mot_de_passe from login where login=?",
        groupsQuery = "select nom_groupe from groupe join login_groupe on groupe.id = LOGIN_GROUPE.GROUPE_ID where login_login=?")
@CustomFormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/login/login_custom.xhtml", 
                errorPage = "", // errorPage = "/faces/noauth.xhtml"
                useForwardToLogin = true
        )
)
//@BasicAuthenticationMechanismDefinition(
//        realmName = "truc"
//)
//@MailSessionDefinition(
//        name = "mail/free",
//        host = "smtp.free.fr",
//        user = "richard.grin",
//        from = " richard.grin@free.fr",
//        storeProtocol = "imap",
//        transportProtocol = "smtp"
//)
@ApplicationScoped
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
public class Config {
}
