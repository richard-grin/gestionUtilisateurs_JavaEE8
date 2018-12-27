/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import fr.unice.formations.entite.Inscription;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richard
 */
@Stateless
public class InscriptionFacade extends AbstractFacade<Inscription> {

  @PersistenceContext(unitName = "GestionUtilisateursPU")
  private EntityManager em;

  protected EntityManager getEntityManager() {
    return em;
  }

  public InscriptionFacade() {
    super(Inscription.class);
  }

  /**
   * Utilitaire pour résoudre mon problème de id null sur les inscriptions !!
   * @param inscriptions 
   */
  public void create(Collection<Inscription> inscriptions) {
    System.out.println("**create inscriptions pour leur donner des id");
    for (Inscription inscription : inscriptions) {
      create(inscription);
    }
    // Vérification :
    for (Inscription inscription : inscriptions) {
      System.out.println("id de l'inscription :" + inscription.getId());
    }
  }

}
