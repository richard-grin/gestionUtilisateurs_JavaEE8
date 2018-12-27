package formations.jsf;

import ejb.GroupeFacade;
import ejb.LoginFacade;
import ejb.PersonneFacade;
import fr.unice.formations.entite.Groupe;
import fr.unice.formations.entite.Login;
import fr.unice.formations.entite.Personne;
import java.io.Serializable;
import java.security.Principal;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;
import javax.persistence.NoResultException;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import util.EnvoyeurEmail;
import util.HashMdp;

/**
 * Gère l'utilisateur en cours. Portée session car ces informations sont
 * valables pour toute la session.
 *
 * @author richard
 */
@Named(value = "accueilController")
@SessionScoped
public class AccueilController implements Serializable {

  /**
   * Le chemin du bundle utilisé pour l'internationalisation.
   */
  private final String CHEMIN_BUNDLE = "/Bundle";
  /**
   * Bundle utilisé pour l'internationalisation.
   */
  private final ResourceBundle bundle = ResourceBundle.getBundle(CHEMIN_BUNDLE);
  
  @Inject
  private ExternalContext externalContext;
  
  private String NOM_JNDI_EMAIL;
  
  /**
   * Personne concernée par cette session. On peut s'en passer puisqu'on a le
   * login ? Pas certain à cause des utilisateurs qui n'ont pas encore de
   * compte.
   */
  private Personne personne;
  /**
   * Login pour cette session.
   * Mis par LoginBean juste après la saisie du login et du mot de passe.
   */
  private Login login;
  
  @EJB
  private PersonneFacade personneFacade;
  @EJB
  private LoginFacade loginFacade;
  @EJB
  private GroupeFacade groupeFacade;
  
  @Inject
  private HashMdp passwordHash;
  @Inject
  private SecurityContext securityContext;
  @Inject
  private HttpServletRequest httpServletRequest;

  private String id;
  private String motDePasse2;

  // valeur données par un paramètre de vue de confirmation.xhtml
  private String loginConfirm;
  private String codeConfirm;
  private String emailConfirm;

  // Les mots de passe pour entrer dans l'application
  private String mdp1;

  /**
   * Creates a new instance of AccueilController
   */
  public AccueilController() {
  }
  
  @PostConstruct
  private void init() {
    NOM_JNDI_EMAIL = externalContext.getInitParameter("fr.unice.EMAI_JNDI_NAME");
  }

  /**
   * Get the value of id
   *
   * @return the value of id
   */
  public String getId() {
    return id;
  }

  /**
   * Set the value of id
   *
   * @param id new value of id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get the value of mdp
   *
   * @return the value of mdp
   */
  public String getMotDePasse2() {
    return motDePasse2;
  }

  /**
   * Set the value of mdp
   *
   * @param mdp new value of mdp
   */
  public void setMotDePasse2(String mdp) {
    this.motDePasse2 = mdp;
  }

  /**
   * Exécuté quand l'utilisateur a oublié son mot de passe.
   *
   * @return
   */
  public String oubliMotDePasse() {
    return "oubliMotDePasse";
  }

  public Login getLogin() {
    String nomLogin = this.securityContext.getCallerPrincipal().getName();
//    System.out.println("+=+=+= Nom de l'utilisateur dans getLogin() de AcceuilController=" + nomLogin);
    this.login = loginFacade.findByNom(nomLogin);
    
    /*
    if (login == null) {
      // Récupérer le login
      String nomUtilisateur
              = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
      // Récupère le login et la personne associée (mode eager pour
      // les associations N-1).
      if (nomUtilisateur != null) {
        login = loginFacade.find(nomUtilisateur);
      }
    }*/
    return this.login;
  }

  public void setLogin(Login login) {
    this.login = login;
  }

  public Personne getPersonne() {
    return personne;
  }

  public void setPersonne(Personne personne) {
    this.personne = personne;
  }

  public String getCodeConfirm() {
    return codeConfirm;
  }

  public String getEmailConfirm() {
    return emailConfirm;
  }

  public String getLoginConfirm() {
    return loginConfirm;
  }

  public void setCodeConfirm(String codeConfirm) {
    this.codeConfirm = codeConfirm;
  }

  public void setEmailConfirm(String emailConfirm) {
    this.emailConfirm = emailConfirm;
  }

  public void setLoginConfirm(String loginConfirm) {
    this.loginConfirm = loginConfirm;
  }

  public boolean isAuthenticated() {
    return externalContext.getUserPrincipal() != null;
  }
  
  public boolean isAdministrateur() {
    System.out.println("!!!!!!isAdministrateur : " + securityContext.isCallerInRole("admin"));
    return securityContext.isCallerInRole("admin");
  }
  /**
   * Inscription d'un nouvel utilisateur.
   *
   * @return
   */
  public String inscription() {
    // Les informations saisies par l'utilisateur iront dans ces objets.
    personne = new Personne();
    login = new Login();
    return "/inscription";
  }

  /**
   * Entrer login et mot de passe.
   *
   * @return
   */
  public String entrer() {
    return "/login";
  }

  /**
   * Enregistrer un nouvel utilisateur. Appelée depuis la page JSF
   * inscription.xhtml.
   *
   * @return
   */
  public String enregistrer() {
    // Vérifications
    // Vérifier que le login (impossible) ou l'email (warning) n'existe pas déjà.
    // TODO

    // Enregister le nouvel utilisateur
    personneFacade.create(personne);
    login.setStatut("email");
    personne.addLogin(login);
    // Repousser à la confirmation par email
//    login.addGroupe(groupeFacade.findByNom("inscrit"));
    // Passe le mot de passe en SHA-256 au dernier moment, juste avant
    // d'enregistrer le login dans la base de données.
    login.setMotDePasse(passwordHash.generate(login.getMotDePasse()));
    loginFacade.create(login);

    // Envoi email de demande de confirmation
    envoiEmailDemandeConfirmation();
    // Envoi sur une page d'accueil explicative avec menu.
    return "/apresInscription";
  }

  private void envoiEmailDemandeConfirmation() {
    try {
      /* Génération code et ajout à l'entité login.
       * Ce code ajoute à la sécurité : 
       * <ul>
       * <li>il est enregistré dans l'entité Login de l'utilisateur</li>
       * <li>il est envoyé avec l'URL du lien de l'email. Quand l'utilisateur
       * cliquera sur ce lien, la requête contiendra un paramètre 
       * qui aura ce code comme valeur.</li>
       * <li>à l'arrivée de la requête, la valeur de ce paramètre sera comparé
       * au code contenu dans l'entité Login.</li>
       * <li>la requête ne sera acceptée que si les 2 valeurs sont égales.</li>
       * </ul>
       */
      Random random = new Random(System.currentTimeMillis());
      long n = random.nextLong();
      String code = "" + n;
      int tailleCode = code.length();
      int max = 15;
      int tailleMax = tailleCode > max ? max : tailleCode;
      code = code.substring(0, tailleMax);
      // Envoi de l'email
      String lienConfirmation
              = "http://localhost:8080" 
              + externalContext.getApplicationContextPath() 
              + "/faces/confirmation.xhtml?"
              + "id=" + login.getLogin()
              + "&email=" + login.getEmail()
              + "&cle=" + code;
      EnvoyeurEmail envoyeurEmail = new EnvoyeurEmail(NOM_JNDI_EMAIL);
      String texteEmail = "Hy " + personne.getPrenom() + ","
              + "\nYou recently entered a new contact email address into OpenFormations."
              + "\nTo confirm your contact email, follow the link below:\n"
              + lienConfirmation
              + "\nCheers,"
              + "\nThe OpenFormations Team";
      String sujetEmail = "Confirm your registration";
      envoyeurEmail.envoyer(texteEmail, sujetEmail, login.getEmail());
      login.setCode(code);
      loginFacade.edit(login);
    } catch (AddressException ex) {
      Logger.getLogger(AccueilController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MessagingException ex) {
      Logger.getLogger(AccueilController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NamingException ex) {
      // Ajouter un message d'erreur JSF
      // TODO
      Logger.getLogger(AccueilController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Vérifie que la confirmation est correcte. Méthode écouteur de type
   * PrerenderView de la page confirmation.xhtml.
   *
   * Méthode appelée lorsque l'utilisateur clique sur le lien de l'email qu'on
   * lui a envoyé pour confirmer son inscription. Si son inscription est
   * onnfirmée, on reste sur la page confirmation (qui est la page qui demande
   * un login et un mot de passe pour entrer dans l'application). Si elle ne
   * l'est pas, on renvoie vers une page avec un message d'erreur. Autre
   * solution : on reste sur la page confirmation (car elle permet aussi de
   * créer un nouveau compte), mais avec un message d'erreur.
   *
   * @param event obligé pour un écouteur d'événement système...
   */
//  public void verifierConfirmation(ComponentSystemEvent event) {
//    System.out.println("===========Valeurs reçues :");
//    System.out.println("login = " + loginConfirm);
//    System.out.println("email = " + emailConfirm);
//    System.out.println("code = " + codeConfirm);
//    System.out.println("loginFacade=" + loginFacade);
//
//    Login login2 = loginFacade.find(loginConfirm);
//    System.out.println("login2=" + login2);
////    String statutLogin = login.getStatut();
//    if (codeConfirm.equals(login2.getCode())
//            && loginConfirm.equals(login2.getLogin())
//            && emailConfirm.equals(login2.getEmail())
//            && login2.getStatut().equals("email")) {
//      System.out.println("*****Confirmation OK !!!!");
//      login2.setStatut("ok");
//      // Tout est OK, on peut le mettre dans le groupe des inscrits
//      Groupe groupeInscrits;
//      try {
//        groupeInscrits = groupeFacade.findByNom("inscrit");
//      } catch (EJBException e) {
//        Throwable cause = e.getCause();
//        if (cause instanceof NoResultException) {
//          // Ajouter le groupe s'il n'existe pas déjà
//          groupeInscrits = new Groupe("inscrit");
//          groupeFacade.create(groupeInscrits);
//        } else {
//          throw e;
//        }
//      }
//      login2.addGroupe(groupeInscrits);
//      loginFacade.edit(login2);
//      login = login2;
//    } else {
//      // Anomalie à enregistrer dans les logs de l'application
//      // et message à l'utilisateur (?)
//      System.out.println("*****Confirmation PAS OK !!!!");
//    }
//  }
  
  public String verifierConfirmation() {
    System.out.println("===========Valeurs reçues dans verifierConfirmation() :");
    System.out.println("login = " + loginConfirm);
    System.out.println("email = " + emailConfirm);
    System.out.println("code = " + codeConfirm);
    System.out.println("loginFacade=" + loginFacade);

    Login login2 = loginFacade.find(loginConfirm);
    System.out.println("login2=" + login2);
//    String statutLogin = login.getStatut();
    if (codeConfirm.equals(login2.getCode())
            && loginConfirm.equals(login2.getLogin())
            && emailConfirm.equals(login2.getEmail())
            && login2.getStatut().equals("email")) {
      System.out.println("*****Confirmation OK !!!!");
      login2.setStatut("ok");
      // Tout est OK, on peut le mettre dans le groupe des inscrits
      Groupe groupeInscrits;
      try {
        groupeInscrits = groupeFacade.findByNom("inscrit");
      } catch (EJBException e) {
        Throwable cause = e.getCause();
        if (cause instanceof NoResultException) {
          // Ajouter le groupe s'il n'existe pas déjà
          groupeInscrits = new Groupe("inscrit");
          groupeFacade.create(groupeInscrits);
        } else {
          throw e;
        }
      }
      login2.addGroupe(groupeInscrits);
      loginFacade.edit(login2);
      login = login2;
      return "/index";
    } else {
      // Anomalie à enregistrer dans les logs de l'application
      // et message à l'utilisateur (?)
      System.out.println("ERREUR !!");
      System.out.println("login2 : " + login2.getCode() + ";" + login2.getEmail() + ";" + login2.getStatut());
      System.out.println("*****Confirmation PAS OK pour " + loginConfirm + " !!!!");
      return "/confirmation-error";
    }
  }

  /**
   * Récupère les informations sur l'utilisateur et les range dans les variables
   * d'instance de la classe.
   */
  public void informationsSurUtilisateur(ComponentSystemEvent event) {
    if (login != null) {
      // On a déjà les infos
      return;
    }
    String loginUtilisateur
            = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
//            ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteUser();
//    System.out.println("Utilisateur remote = " + loginUtilisateur);
    if (loginUtilisateur != null) {
      login = loginFacade.find(loginUtilisateur);
    }
  }

  public String fin() {
    this.login = null;
    this.personne = null;
    try {
      httpServletRequest.logout();
//      System.out.println("logout fait***************");
      httpServletRequest.getSession().invalidate();
//      System.out.println("session invalidée***************====");
//      ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).logout();
    } catch (ServletException ex) {
      Logger.getLogger(AccueilController.class.getName()).log(Level.SEVERE, null, ex);
    }
    return "/index?faces-redirect=true";
  }

  public void validateMdp1(FacesContext context, UIComponent component,
          Object mdp1) {
    this.mdp1 = (String) mdp1;
  }

  /**
   * Vérifie que les 2 mots de passe sont bien les mêmes. Méthode listener de
   * l'événement postValidate de la page JSF inscription.xhtml
   *
   * @param event
   */
  public void validateMdps(ComponentSystemEvent event) {
//    System.out.println("***************Exécution méthode validateMdps");
    UIComponent source = event.getComponent();
    UIInput mdp1 = (UIInput) source.findComponent("motDePasse");
    UIInput mdp2 = (UIInput) source.findComponent("motDePasse2");

    String v1 = null, v2 = null;
    Object o1 = mdp1.getLocalValue();
    Object o2 = mdp2.getLocalValue();
    System.out.println("mdp1=" + o1 + ";mdp2=" + o2 + ".");
    if (o1 != null) {
      v1 = o1.toString();
    }
    if (o2 != null) {
      v2 = o2.toString();
    }
    if (v1 != null && !v1.equals(v2)) {
      String message = bundle.getString("mdpsDifferents");
      // Ne marche pas ; dommage !
//      throw new ValidatorException(new FacesMessage(message));
      FacesContext context = FacesContext.getCurrentInstance();
      FacesMessage fm = new FacesMessage(message);
      fm.setSeverity(FacesMessage.SEVERITY_ERROR);
      context.addMessage(source.getClientId(), fm);
      context.renderResponse();
    }
  }

  public void validateMotsDePasse(FacesContext context,
          UIComponent composant, Object valeur) {
    UIInput composantMdp1 = (UIInput) composant.findComponent("motDePasse");
    System.out.println("naming container:" + composantMdp1.getNamingContainer());
    System.out.println("Valeur soumise=" + composantMdp1.getSubmittedValue());
    Object o1 = composantMdp1.getLocalValue();
    System.out.println("mdp1=" + o1);
    if (o1 != null && o1.equals(valeur)) {
      // Tout va bien
      return;
    }
    // Problème !
    FacesMessage message
            = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Mots de passes différents",
                    " Vous n'avez pas tapé 2 fois le même mot de passe !");
    throw new ValidatorException(message);
  }

  public void validateLogin(FacesContext context,
          UIComponent composant, Object valeur) {
    String nomLogin = (String) valeur;
    // Ne valide pas si le login a déjà été choisi par un autre utilisateur
    Login login = loginFacade.find(nomLogin);
    if (login != null) {
      FacesMessage message
              = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                      "Login déjà choisi",
                      nomLogin + " a déjà été choisi par un autre utilisateur");
      throw new ValidatorException(message);
    }
  }
}
