package ejb;

import fr.unice.formations.entite.Personne;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richard
 */
@Stateless
public class PersonneFacade extends AbstractFacade<Personne> {
  @PersistenceContext(unitName = "GestionUtilisateursPU")
  private EntityManager em;

  protected EntityManager getEntityManager() {
    return em;
  }

  public PersonneFacade() {
    super(Personne.class);
  }
  
}
