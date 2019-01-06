package listener;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Web application lifecycle listener.
 * Pour tests....
 *
 * @author grin
 */
//@WebListener("Pour compter le nombre d'utilisateurs connectés")
public class CompteurSessionListener implements HttpSessionListener {
  @Inject
  private CompteurSessions compteurSessions;

  @Override
  public void sessionCreated(HttpSessionEvent se) {
    compteurSessions.incrementerNbSession();
    System.out.println("*************incrémente Nb sessions");
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    compteurSessions.decrementerNbSession();
    System.out.println("*************décrémente Nb sessions");
  }
}
