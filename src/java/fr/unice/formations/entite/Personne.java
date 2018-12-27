package fr.unice.formations.entite;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Une personne.
 * Elle peut être enregistrée dans une application.
 * En ce cas elle a un login (avec un mot de passe).
 * Elle peut avoir plusieurs logins.
 * @author richard
 */
@Entity
public class Personne implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(length=30)
  private String nom;
  @Column(length=30)
  private String prenom;
  @OneToMany(mappedBy="personne")
  private Set<Login> logins = new HashSet<Login>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getPrenom() {
    return prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }
  
  public void addLogin(Login login) {
    logins.add(login);
    login.setPersonne(this);
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Personne)) {
      return false;
    }
    Personne other = (Personne) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "fr.unice.formations.entite.Personne[ id=" + id + " ]";
  }
  
}
