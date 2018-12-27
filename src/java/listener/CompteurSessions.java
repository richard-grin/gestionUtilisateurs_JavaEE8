/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listener;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author grin
 */
@ApplicationScoped
@Named
public class CompteurSessions {
  private int nbSessions;

  public int getNbSessions() {
    return nbSessions;
  }
  
  public void incrementerNbSession() {
    nbSessions++;
    System.out.println("Nb sessions :" + nbSessions);
  }
  
  public void decrementerNbSession() {
    nbSessions--;
    System.out.println("Nb sessions :" + nbSessions);
  }
  
}
