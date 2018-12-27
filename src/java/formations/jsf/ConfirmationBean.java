package formations.jsf;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
@Named(value = "confirmationBean")
@RequestScoped
public class ConfirmationBean {

  @Inject
  private SecurityContext securityContext;
  @Inject
  private FacesContext facesContext;
  @Inject 
  private ExternalContext externalContext;

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

  public String login() {
    Credential credential = 
            new UsernamePasswordCredential(nom, new Password(motDePasse));
    AuthenticationStatus status = securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            withParams().credential(credential));
    if (status.equals(SEND_CONTINUE)) { // ??**??
      facesContext.responseComplete();
      System.out.println("VA RETOURNER /index!!!!");
      return "/index";
    } else if (status.equals(SEND_FAILURE)) {
      addError(facesContext, "Authentication failed");
    }
    return null;
  }

  /**
   * Ajoute une erreur à afficher dans la page de login.
   * @param facesContext
   * @param authentication_failed 
   */
  private void addError(FacesContext facesContext, String message) {
    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
  }

}
