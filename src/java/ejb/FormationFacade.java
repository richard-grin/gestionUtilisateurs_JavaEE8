package ejb;

import fr.unice.formations.entite.Formation;
import fr.unice.formations.entite.Login;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author richard
 */
@Stateless
public class FormationFacade extends AbstractFacade<Formation> {
  @PersistenceContext(unitName = "GestionUtilisateursPU")
  private EntityManager em;

  protected EntityManager getEntityManager() {
    return em;
  }

  public FormationFacade() {
    super(Formation.class);
  }
  
  public List<Formation> findFormationsLogin(Login login) {
    Query query = em.createNamedQuery("Formation.findByLogin");
    query.setParameter("login", login);
    return query.getResultList();
  }
  
  public List<Formation> findFormationsNomLogin(String nomLogin) {
    Query query = em.createNamedQuery("Formation.findByNomLogin");
    query.setParameter("nomLogin", nomLogin);
    return query.getResultList();
  }
  
}
