package fr.unice.formations.entite;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Inscription à une formation.
 * @author richard
 */
@Entity
public class Inscription implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  /**
   * Inscription accepté ou pas.
   */
  private boolean accepte;
  /**
   * Montant demandé par l'utilisateur.
   */
  private String montantDemandeAide;
  /**
   * Login de l'utilisateur qui a fait l'inscription.
   */
  @ManyToOne
  private Login login;
  /**
   * Formation concernée par l'inscription.
   */
  @ManyToOne
  private Formation formation;

  public Inscription() {
  }

  public Inscription(Login login, Formation formation) {
    this.login = login;
    this.formation = formation;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Formation getFormation() {
    return formation;
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
    if (!(object instanceof Inscription)) {
      return false;
    }
    Inscription other = (Inscription) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    String s = "fr.unice.formations.entite.Inscription[ id=" + id + " ]";
    s += "; login=" + login;
    s += "; formation=" + formation;
    return s;
  }
  
}
