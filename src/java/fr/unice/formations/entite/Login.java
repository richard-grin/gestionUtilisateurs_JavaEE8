package fr.unice.formations.entite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;

/**
 * Informations pour entrer dans l'application.
 *
 * @author richard
 */
@Entity
@NamedQuery(name = "Login.findByEmail",
        query = "SELECT l FROM Login l where l.email = :email")
@NamedQuery(name = "Login.findByNom",
        query = "SELECT l FROM Login l where l.login = :nom")
public class Login implements Serializable {

  @Id
  @Column(length = 50)
  private String login;
  /**
   * email pour ce login. Il doit être unique : 2 login ne peuvent avoir le même
   * email. 
   * 
   * Pour faire les tests plus facilement j'ai enlevé l'unicité.
   */
//  @Column(unique = true)
//  @Pattern(regexp = "[a-zA-Z0-9\\.]+@[a-zA-Z0-9\\.]+\\.[a-zA-Z0-9]+",
//          message = "Email is invalid")
  @Email
  private String email;
  // Le mot de passe en SHA256, codé en Base64 (attention, pas Hex)
  //  @Column(name="mot_de_passe", length = 32)
  //  private byte[] motDePasse;
  @Column(name = "mot_de_passe", length = 160)
  private String motDePasse;
  /**
   * Statut du login. "email" après l'envoi d'un email de demande de
   * confirmation ; "valide" seulement après le clic de l'utilisateur sur le
   * lien de l'email qu'il a reçu.
   */
  @Column(length = 10)
  private String statut;
  /**
   * Code aléatoire envoyé envoyé avec l'email.
   */
  private String code;
  
  // Une personne peut avoir plusieurs logins
  @ManyToOne
  private Personne personne;
  
  // Unidirectionnel ; bien ? EAGER.
  @ManyToMany(fetch = FetchType.EAGER)
  // On ne veut pas que la colonne vers le groupe s'appelle GROUPES_ID
  @JoinTable(name = "LOGIN_GROUPE",
          joinColumns
          = @JoinColumn(name = "LOGIN_LOGIN"),
          inverseJoinColumns
          = @JoinColumn(name = "GROUPE_ID"))
  private Set<Groupe> groupes = new HashSet<>();

  /*
   * TODO: Il vaudrait mieux isoler login et groupe pour être réutilisables
   * pour d'autres applications.
   * En ce cas, l'inscription à une école se ferait plutôt entre
   * Personne et Formation plutôt qu'entre Login et Formation.
   */
  // Utilisation d'un Map pour pouvoir supprimer rapidement et simplement
  // une inscription à une formation
  @OneToMany(mappedBy = "login", cascade = CascadeType.ALL, fetch = FetchType.EAGER,
          orphanRemoval = true)
  @MapKey(name = "formation")
  private Map<Formation, Inscription> inscriptions;
//  private List<Inscription> inscriptions = new ArrayList<>();

  public Map<Formation, Inscription> getInscriptions() {
    return inscriptions;
  }

  /**
   * Retuurne les formations auxquelles le login s'est inscrit. Ca doit être une
   * liste car utilisé par &lt;p:dataTable> avec un attribut selection (ne peut
   * pas être une Map).
   *
   * @return
   */
  public List<Formation> getFormations() {
    return new ArrayList<>(inscriptions.keySet());
  }

  public Login(String login, String email) {
    this.login = login;
    this.email = email;
  }

  public Login() {
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getMotDePasse() {
    return motDePasse;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public Personne getPersonne() {
    return personne;
  }

  // pas public ; utiliser méthode addLogin de Personne
  // pour ajouter un login à une personne.
  void setPersonne(Personne personne) {
    this.personne = personne;
  }

  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }

  public void addGroupe(Groupe groupe) {
    this.groupes.add(groupe);
  }

  /**
   * Ajoute une inscription à la formation passée en paramètre. Version avec
   * liste
   *
   * @param formation
   */
//  public void addInscription2(Formation formation) {
//    // Crée une nouvelle inscription à la formation
//    Inscription inscription = new Inscription(this, formation);
//    System.out.println("Ajoute l'inscription " + inscription);
//    inscriptions.add(inscription);
//    formation.addInscription(inscription);
//  }
  /**
   * Ajoute une inscription à la formation passée en paramètre. Version avec
   * map.
   *
   * @param formation
   */
  public void addInscription(Formation formation) {
    // Crée une nouvelle inscription à la formation avec les liens vers login
    // et formation.
    Inscription inscription = new Inscription(this, formation);
//    System.out.println("Ajoute l'inscription " + inscription);
    inscriptions.put(formation, inscription);
    // Ajoute à l'autre bout de l'association
    formation.addInscription(inscription);
  }

  /**
   * Ajoute des inscriptions à toutes les formations passées en paramètre.
   *
   * @param formations liste des formations où il faut inscrire le login.
   */
  public void addInscriptions(List<Formation> formations) {
    for (Formation formation : formations) {
      System.out.println("Ajoute la formation " + formation);
      this.addInscription(formation);
    }
  }

  /**
   * Supprimer une inscription à une formation.
   *
   * @param formation
   */
  public void supprimerInscription(Formation formation) {
    // On supprime l'entrée de la Map liée à l'inscriptoin de ce login 
    // à la formation.
    Inscription inscription = inscriptions.remove(formation);
    // Supprimer l'autre bout
    formation.supprimer(inscription);
    // Ne marche pas si pas de cascade remove sur inscriptions.
    // Comment faire si on ne veut pas de cascade delete ??**??
  }

  public void supprimerInscriptions(List<Formation> formations) {
    for (Formation formation : formations) {
      supprimerInscription(formation);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Login other = (Login) obj;
    if ((this.login == null) ? (other.login != null) : !this.login.equals(other.login)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 37 * hash + (this.login != null ? this.login.hashCode() : 0);
    return hash;
  }

//  @Override
//  public String toString() {
//    return login;
//  }
  @Override
  public String toString() {
    return "Login{" + "adresse mémoire" + super.toString() + ",login=" + login + '}';
  }

}
