package ejb;

import fr.unice.formations.entite.Groupe;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author richard
 */
@Stateless
public class GroupeFacade extends AbstractFacade<Groupe> {
  @PersistenceContext(unitName = "GestionUtilisateursPU")
  private EntityManager em;

  protected EntityManager getEntityManager() {
    return em;
  }

  public GroupeFacade() {
    super(Groupe.class);
  }
  
  public Groupe findByNom(String nom) {
    Query query = em.createNamedQuery("Groupe.findByNom");
    query.setParameter("nomGroupe", nom);
    return (Groupe)query.getSingleResult();
  }
  
  public Groupe findGroupeInscrit() {
    return findByNom("inscrit");
  }
  
}
