package fr.unice.formations.entite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Une formation.
 * @author richard
 */
@Entity
@NamedQueries({
  // Les formations où un utilisateur avec un certain login s'est inscrit
  @NamedQuery(name = "Formation.findByLogin",
  query = "select f from Formation f join f.inscriptions i where i.login = :login"),
  @NamedQuery(name = "Formation.findByNomLogin",
  query = "select f from Formation f join f.inscriptions i where i.login.login = :nomLogin")
})
public class Formation implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  /**
   * Nom de la formation.
   */
  private String nom;
  private String pays;
  
  // cascade pour pouvoir cacher Inscription et ne pas avoir de problème à la persisstance
  @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL)
  private List<Inscription> inscriptions = new ArrayList<Inscription>();

  public Formation() {
  }

  public Formation(String nom, String pays) {
    this.nom = nom;
    this.pays = pays;
  }
  
  

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Renvoie le nom de la formation.
   * @return 
   */
  public String getNom() {
    return nom;
  }

  public String getPays() {
    return pays;
  }

  public void setPays(String pays) {
    this.pays = pays;
  }

  public List<Inscription> getInscriptions() {
    return inscriptions;
  }

  public void addInscription(Inscription inscription) {
    inscriptions.add(inscription);
  }
  
  public void addNouvellesInscription(List<Inscription> nouvellesInscriptions) {
    inscriptions.addAll(nouvellesInscriptions);
  }
  
  public void removeInscriptions(List<Inscription> inscriptions) {
    inscriptions.removeAll(inscriptions);
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
    if (!(object instanceof Formation)) {
      return false;
    }
    Formation other = (Formation) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "fr.unice.formations.entite.Formation[ id=" + id + "; nom=" + nom + "]";
  }

  void supprimer(Inscription inscription) {
    inscriptions.remove(inscription);
  }
}
