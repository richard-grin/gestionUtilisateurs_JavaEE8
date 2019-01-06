package config;

import java.util.stream.Collectors;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InterceptionFactory;
import javax.faces.annotation.FacesConfig;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
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
                loginPage = "/login/login_custom_new.xhtml",
                errorPage = "" // errorPage = "/faces/noauth.xhtml"
//                useForwardToLogin = false
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
//@Alternative
//@Priority(500)
public class ApplicationConfig {

//  @Alternative
//  @Produces
//  public HttpAuthenticationMechanism produceAuthenticationMechanism(
//          InterceptionFactory<HttpAuthenticationMechanismWrapper> interceptionFactory, BeanManager beanManager) {
//    @RememberMe
//    class RememberMeClass {
//    };
//
//    interceptionFactory.configure().add(RememberMeClass.class.getAnnotation(RememberMe.class));
//    System.out.println("==================================================");
//    // Pas d'erreur avec resolve, dnc l'erreur vient de getReference()
//    // bean s'affiche ainsi :
//    // INFOS:   bean =Managed Bean [class config.HttpAuthenticationMechanismWrapper] with qualifiers [@Any @Default]
//    // Pour le moment tout est normal.
//    // Si on appelle getReference sur ce bean, il y a une erreur.
//    // resolve OK
//    Bean<?> bean = beanManager.resolve(
//    beanManager
//            .getBeans(HttpAuthenticationMechanism.class).stream()
//            .filter(b -> b.getBeanClass() != ApplicationConfig.class)
//            .filter(b -> b.getBeanClass().getName().equals("config.HttpAuthenticationMechanismWrapper"))
//            //            .filter(b -> b.getBeanClass().getName() == org.glassfish.soteria.cdi.CdiProducer.class)
//            .peek(c -> System.out.println("++++++"))
//            .peek(System.out::println)
//            .peek(c -> System.out.println(c.getBeanClass().getName() + ";" + c.getTypes()))
//            .peek(c -> System.out.println("++++++"))
//            .collect(Collectors.toSet()));
//    System.out.println("AAAAAA FIN partie 1 out");
//    System.out.println("bean =" + bean);
//    System.err.println("AAAAAA FIN partie 1 err");
//    System.out.println("Juste avant getReference");
//    System.err.println("Juste avant getReference");
//    // getReference OK
//    Object object = beanManager.getReference(bean, HttpAuthenticationMechanism.class,
//                            beanManager.createCreationalContext(null));
//    // Affiche INFOS:   Object =config.HttpAuthenticationMechanismWrapper@6cc256a7
//    System.out.println("Object =" + object);
//    System.out.println("Juste après getReference");
//    System.err.println("Juste après getReference");
//    // Pour le moment, pas d'erreur
//    // createInterceptedInstance OK !!!
//    HttpAuthenticationMechanism ham = 
//            interceptionFactory.createInterceptedInstance(new HttpAuthenticationMechanismWrapper((HttpAuthenticationMechanism) object));
//    System.out.println("Juste après createInterceptedInstance");
//    System.err.println("Juste après createInterceptedInstance");
//    // Affiche INFOS:   ham =config.HttpAuthenticationMechanismWrapper$Proxy$_$$_WeldInterceptedProxy2@41d91928
//    System.out.println("ham =" + ham);
//    return ham;

    // Retourne une instance d'un wrapper de HttpAuthenticationMechanismWrapper dont la classe
    // a l'annotation @RememberMe et dont les appels de méthode sont transmis
    // à l'instance de HttpAuthenticationMechanismWrapper qui est passée en
    // paramètre de la méthode createInterceptedInstance.
    // Cette instance passée en paramètre est une instance de la classe
    // HttpAuthenticationMechanismWrapper qui enveloppe elle-même une
    // instance de la classe interne de l'API de sécurité qui implémente
    // HttpAuthenticationMechanism pour CustomFormAuthenticationMechanism
    // avec, en plus, l'annotation @RememberMe.
    // Normalement, avec l'alternative activée, cette instance sera utilisée
    // à la place de l'instance de la classe interne quand l'annotation
    // @CustomFormAuthenticationMechanismDefinition sera utilisée.
    // MAIS il y a un problème d'ambiguïté quand je déplois l'application
    // car il semble ne pas tenir compte de l'alternative et il y a donc
    // 2 candidats pour injecter le type 
//    return null;
//    return interceptionFactory.createInterceptedInstance(new HttpAuthenticationMechanismWrapper(
//            (HttpAuthenticationMechanism) beanManager
//                    // getReference prend 3 paramètre : 
//                    //   - un bean (fournit par resolve), 
//                    //   - un type (HttpAuthenticationMechanism.class)
//                    //   - un contexte de création (??**??)
//                    // et retourne un objet qui est une référence contextuelle
//                    // qui représente le bean.
//                    // Le problème d'ambiguïté arrive ici :
//                    // org.jboss.weld.exceptions.AmbiguousResolutionException: WELD-001318: Cannot resolve an ambiguous dependency between: 
//                    //  - org.glassfish.soteria.cdi.CdiProducer@f428ee7,
//                    //  - Managed Bean [class config.HttpAuthenticationMechanismWrapper] with qualifiers [@Any @Default]
//                    // En effet, l'affichage ci-dessus (avec les peek), donne :
//                    // INFOS:   ==================================================
//                    //INFOS:   ++++++
//                    //INFOS:   Managed Bean [class config.HttpAuthenticationMechanismWrapper] with qualifiers [@Any @Default]
//                    //INFOS:   config.HttpAuthenticationMechanismWrapper;[interface javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism, class config.HttpAuthenticationMechanismWrapper, class java.lang.Object]
//                    //INFOS:   ++++++
//                    //INFOS:   org.glassfish.soteria.cdi.CdiProducer@f428ee7
//                    //INFOS:   Managed Bean [class config.HttpAuthenticationMechanismWrapper] with qualifiers [@Any @Default]
//                    .getReference(beanManager.resolve(beanManager
//                                    .getBeans(HttpAuthenticationMechanism.class).stream()
//                                    .peek(b -> System.out.println("AAAAAAAAAAAAAAAAAAA"))
//                                    .filter(b -> b.getBeanClass() != ApplicationConfig.class)
//                                    .collect(Collectors.toSet())),
//                            HttpAuthenticationMechanism.class,
//                            beanManager.createCreationalContext(null))));
//  }
}
