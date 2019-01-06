package formations.jsf;

import ejb.LoginFacade;
import fr.unice.formations.entite.Login;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.annotation.ManagedProperty;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.security.enterprise.AuthenticationStatus.SEND_CONTINUE;
import static javax.security.enterprise.AuthenticationStatus.SEND_FAILURE;
import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;

/**
 * Backing bean pour la page de login avec un formulaire personnalisé (custom).
 *
 * @author grin
 */
@Named(value = "loginBeanAvecNew")
@ViewScoped
public class LoginBeanAvecNew implements Serializable {

  @Inject
  @ManagedProperty("#{param.new}")
  private boolean isNew;
  @Inject
  private Flash flash;

  @Inject
  private SecurityContext securityContext;
  @Inject
  private FacesContext facesContext;
  @Inject
  private ExternalContext externalContext;
  @Inject
  private AccueilController accueilController;
  @Inject
  private LoginFacade loginFacade;

  private String nom;
  private String motDePasse;

  /**
   * Get the value of motDePasse
   *
   * @return the value of motDePasse
   */
  public String getMotDePasse() {
    return motDePasse;
  }

  /**
   * Set the value of motDePasse
   *
   * @param motDePasse new value of motDePasse
   */
  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  /**
   * Get the value of nom
   *
   * @return the value of nom
   */
  public String getNom() {
    return nom;
  }

  /**
   * Set the value of nom
   *
   * @param nom new value of nom
   */
  public void setNom(String nom) {
    this.nom = nom;
  }

  public void login() throws IOException {
//    Credential credential
//            = new UsernamePasswordCredential(nom, new Password(motDePasse));
//    AuthenticationStatus status = securityContext.authenticate(
//            (HttpServletRequest) externalContext.getRequest(),
//            (HttpServletResponse) externalContext.getResponse(),
//            withParams()
//                    .newAuthentication(isNew)
//                    .credential(credential).rememberMe(true));
    switch (continueAuthentication()) {
      case SEND_CONTINUE:
        facesContext.responseComplete();
        break;
      case SEND_FAILURE:
        addError("Nom / mot de passe incorrects");
        break;
      case SUCCESS:
        flash.setKeepMessages(true);
        addInfo("Login réussi");
        externalContext.redirect(
                externalContext.getRequestContextPath()
                + "/index.xhtml");

        break;
    }

//    if (status.equals(SEND_CONTINUE)) { // ??**??
//      // Récupère les infos sur le login et le range dans le bean de porté
//      // session accueilController.
////      String nomUtilisateur = nom;
////              = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
//      // Récupère le login et la personne associée (mode eager pour
//      // les associations N-1).
////      if (nomUtilisateur != null) {
//
////      Login login = loginFacade.find(nom);
////      accueilController.setLogin(login);
//      facesContext.responseComplete();
//    } else if (status.equals(SEND_FAILURE)) {
//      addError("Nom / mot de passe incorrects");
//    }
  }

  private AuthenticationStatus continueAuthentication() {
    Credential credential
            = new UsernamePasswordCredential(nom, new Password(motDePasse));
    return securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            withParams()
                    .newAuthentication(isNew)
                    .credential(credential));
  }

  /**
   * Ajoute une erreur à afficher dans la page de login.
   *
   * @param facesContext
   * @param authentication_failed
   */
  private void addError(String message) {
    facesContext.addMessage(
            null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    message,
                    null));
  }

  private void addInfo(String message) {
    facesContext.addMessage(
            null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                    message,
                    null));
  }

}
