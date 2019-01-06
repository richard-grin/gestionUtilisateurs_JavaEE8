package formations.jsf;

import ejb.FormationFacade;
import ejb.InscriptionFacade;
import ejb.LoginFacade;
import fr.unice.formations.entite.Formation;
import fr.unice.formations.entite.Inscription;
import fr.unice.formations.entite.Login;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;

/**
 * Backing bean pour la page formations.xhtml.
 *
 * @author richard
 */
@Named(value = "formationController")
//@RequestScoped
@ViewScoped
public class FormationController implements Serializable {

  @Inject
  private AccueilController accueilController;
  @Inject 
  private SecurityContext securityContext;
  
  @EJB
  private FormationFacade formationFacade;
  @EJB
  private LoginFacade loginFacade;
  @EJB
  private InscriptionFacade inscriptionFacade;

  private Login login;

  /**
   * Les formations auxquelles le login s'est déjà inscrit.
   */
  private List<Formation> formationsDuLogin;
  /**
   * Toutes les formations.
   */
  private List<Formation> formations;
  /**
   * Les nouvelles formations auxquelles le login veut s'inscrire. Valeurs liées
   * au composant liste déroulante de formations.xhtml.
   */
  private List<Formation> nouvellesFormations;

  /**
   * Les formations à supprimer dans la liste des formations suivies.
   */
  private List<Formation> suppressedFormations;

  /**
   * Creates a new instance of FormationController
   */
  public FormationController() {
  }

  //  private Login getLogin() {
//    return loginFacade.find(accueilController.getLogin().getLogin());
//  }
  
  /**
   * Retourne le login lié à l'utilisateur connecté.
   */
  private void getLogin() {
    if (login == null) {
      login = loginFacade.find(securityContext.getCallerPrincipal().getName());
//      System.out.println("**********Nom de l'utilisateur : " + login.getLogin());
      login = loginFacade.find(accueilController.getLogin().getLogin());
    }
  }

  /**
   * Récupère toutes les formations.
   *
   * @return
   */
  public List<Formation> getFormations() {
    if (formations == null) {
      formations = formationFacade.findAll();
    }
    return formations;
  }

  /**
   * Supprimer les formations cochées pour la suppression, qui sont enregistrées
   * dans la propriété suppressedFormations.
   */
  public void supprimerInscriptions() {
    System.out.println("DEBUT supprimerInscriptions !!!!!!!!!");
//    Login login = accueilController.getLogin();
//    Login login = getLogin();
    getLogin();
    System.out.println("*********Les formations inscrites avant la suppression :");
    afficheInscriptions(login.getInscriptions());
//    System.out.println(login.getInscriptions());
    login.supprimerInscriptions(getSuppressedFormations());
    System.out.println("*********Les formations inscrites APRES la suppression :");
    afficheInscriptions(login.getInscriptions());
//    System.out.println(login.getInscriptions());
//    for (Formation formation : getSuppressedFormations()) {
//      login.supprimerInscription(formation);
//    }
    loginFacade.edit(login);
  }

  private void afficheInscriptions(Map<Formation, Inscription> inscriptions) {
    for (Map.Entry<Formation, Inscription> entry : inscriptions.entrySet()) {
      System.out.println("**Entrée :" + entry);
      System.out.println("Inscription : id=" + entry.getValue().getId());
      System.out.println("id=" + entry.getKey().getId() + "; nom=" + entry.getKey().getNom());
    }
  }

  /**
   * Retourne les formations auxquelles le login courant n'est pas inscrit.
   *
   * @return
   */
  public List<Formation> getAutresFormations() {
    /*
     * Pour trouver les formations auxquelles le login courant ne s'est pas
     * inscrit, on va prendre toutes les formations et on enlève les
     * formations auxquelles il est déjà inscrit.
     * Il faut travailler sur une copie de formations sinon on va modifier
     * formations qui sert par ailleurs, d'où le new ArrayList.
     */
    // Récupère toutes les formations
    List<Formation> autresFormations = new ArrayList(getFormations());
//    if (autresFormations == null) {
//      return null;
//    }
//    System.out.println("1. autresformations=" + autresFormations);
    Set<Formation> formationsActuelles = getInscriptionsDuLogin().keySet();
    if (formationsActuelles.isEmpty()) {
//      System.out.println("2. autresformations=" + autresFormations);
      return autresFormations;
    }
    // Et enlève les formations auxquelles le login est déjà inscrit
    autresFormations.removeAll(formationsActuelles);
//    System.out.println("3. autresformations=" + autresFormations);
    return autresFormations;
  }

  public List<Formation> getSuppressedFormations() {
    return suppressedFormations;
  }

  public void setSuppressedFormations(List<Formation> suppressedFormations) {
    this.suppressedFormations = suppressedFormations;
  }

  /**
   * Retourne les formations auxquelles le login courant est inscrit. NE MARCHE
   * PAS ! En effet, si on a un attribut "selection", la valeur retournée pour
   * <p:datatable> de PrimeFaees doit être une iiste.
   *
   * @return
   */
  public Map<Formation, Inscription> getInscriptionsDuLogin() {
//    Login login = accueilController.getLogin();
    getLogin();
    // Puisque l'association entre login et inscriptions est eager.
    return login.getInscriptions();
  }

  /**
   * Récupère les formations auxquel le login (l'utilisateur) s'est inscrit.
   *
   * @return
   */
  public List<Formation> getFormationsDuLogin() {
//    Login login = accueilController.getLogin();
    getLogin();
    return login.getFormations();
  }
//  public List<Formation> getInscriptionsDuLogin() {
//    System.out.println("======Début getFormationsLogin=======");
//    if (formationsDuLogin == null) {
////      String nomLogin =
////              FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
////      // Récupère le login et la personne associée (mode eager pour
////      // les associations N-1).
////      if (nomLogin != null) {
////        formationsDuLogin = formationFacade.findFormationsNomLogin(nomLogin);
////        System.out.println("Les formations de getFormationLogin juste après la requête JPA :" + formationsDuLogin);
////      Login login = accueilController.getLogin();
//      String nomUtilisateur
//              = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
//      // Récupère le login et la personne associée (mode eager pour
//      // les associations N:1).
//      Login login = null;
//      if (nomUtilisateur != null) {
//        login = loginFacade.find(nomUtilisateur);
//      }
//      List<Inscription> inscriptionsLogin = login.getInscriptions();
//      formationsDuLogin = new ArrayList<>();
//      for (Inscription inscription : inscriptionsLogin) {
//        formationsDuLogin.add(inscription.getFormation());
//      }
//      // Provoque une erreur car login n'est pas géré par JPA
////        login = loginFacade.merge(login);
////        loginFacade.refresh(login);
//      System.out.println("Les inscriptions à la fin de getFormationsLogin :" + login.getInscriptions());
//      System.out.println("======Fin getFormationsLogin=======");
//    }
//    return formationsDuLogin;
//  }

  public List<Formation> getNouvellesFormations() {
    return nouvellesFormations;
  }

  public void setNouvellesFormations(List<Formation> nouvellesFormations) {
    this.nouvellesFormations = nouvellesFormations;
    System.out.println("************Nouvelles formations : " + nouvellesFormations);
  }

  /**
   * Ajouter des nouvelles formations.
   *
   * @return "formations" pour faire afficher cette page.
   */
  public String ajouterNouvellesFormations() {
//    System.out.println("======Début ajouterNouvellesFormations=======");
////    Login login = accueilController.getLogin();
//    String nomUtilisateur
//            = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
//    // Récupère le login et la personne associée (mode eager pour
//    // les associations N-1).
//    Login login = null;
//    if (nomUtilisateur != null) {
//      login = loginFacade.find(nomUtilisateur);
//    }
//    login = loginFacade.merge(login);
//    Login login = accueilController.getLogin();
    // Pour voir.....
//    login = loginFacade.find(login.getLogin());
    getLogin();
//    System.out.println("**===Les inscriptions avant l'ajout des nouvelles formations :" + login.getInscriptions());

    System.out.println("**===Les inscriptions avant l'ajout des nouvelles formations :");
    afficheInscriptions(login.getInscriptions());
    System.out.println("NouvellesFormations à ajouter=" + nouvellesFormations);

    login.addInscriptions(nouvellesFormations);
//     Met des id aux nouvelles inscriptions
//    inscriptionFacade.create(nouvellesFormations);
//    System.out.println("les inscriptions après l'ajout des nouvelles formations :" + login.getInscriptions());
    // Fait un merge du login pour tenir compte des nouvelles inscriptions
    loginFacade.edit(login);

    System.out.println("=======Les inscriptions après l'ajout :");
    afficheInscriptions(login.getInscriptions());

//    accueilController.setLogin(login);
    // Il dit que le login n'est pas géré. Oui car il y a eu un merge et 
    // login n'est pas l'objet qui a été mergé.
//    loginFacade.refresh(login);
    nouvellesFormations.clear(); // Ca ne sert à rien !!!
    nouvellesFormations = null;
    formationsDuLogin = null;
    System.out.println("======Fin ajouterNouvellesFormations=======");
    return "formations";
//    return "formations?redirect='true'";

  }

  @FacesConverter(value = "formationConverter", managed = true)
  public static class FormationConverter implements Converter<Formation> {

    @Inject
    private FormationFacade formationFacade;

    @Override
    public Formation getAsObject(FacesContext facesContext, UIComponent component, String value) {
      System.out.println("------------Convertir " + value + " en Formation");
      if (value == null || value.length() == 0 || value.equals("0")) {
        return null;
      }
      System.out.println("Début conversion");
//      // Ca semble ne pas marcher mais je ne sais pas pourquoi la façade est null.
//      // Elle n'a pas été injectée dans le controller.
//      FormationController controller = (FormationController) facesContext.getApplication().getELResolver().
//              getValue(facesContext.getELContext(), null, "formationController");
//      System.out.println("Controller = " + controller);
//      FormationFacade facade2 = controller.formationFacade;
//      System.out.println("facade = " + facade2);
//      if (facade2 != null) {
//        return controller.FormationFacade.find(new Long(value));
//      }
      // Cette façon là marche
//      try {
//        InitialContext ic = new InitialContext();
//        FormationFacade facade3
//                = (FormationFacade) ic.lookup("java:app/GestionUtilisateurs/FormationFacade");
//        System.out.println("Facade3 = " + facade3);
//        System.out.println("Value=" + value);
//        Formation formation = facade3.find(new Long(value));
//        System.out.println("Formation convertie :" + formation);
//        return formation;
//      } catch (NamingException e) {
//        e.printStackTrace();
//        throw new ConverterException("Problème de nommage");
//      }
      // Et celle là aussi depuis Java EE 8 (JSF 2.3) :
      return formationFacade.find(new Long(value));
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Formation formation) {
      Formation o = (Formation) formation;
      return o.getId().toString();
    }
  }
}
