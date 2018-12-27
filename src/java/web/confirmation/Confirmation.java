package web.confirmation;

import ejb.LoginFacade;
import fr.unice.formations.entite.Login;
import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PAS UTILISE !! remplacé par une page JSF avec prerenderview (confirmation.xhtml)
 * Confirme l'inscription d'un utilisateur.
 * Lancé après clic de l'utilisateur dans l'email qu'on lui a envoyé.
 * @author richard
 */
@WebServlet(name = "Confirmation", urlPatterns = {"/confirm.html"})
public class Confirmation extends HttpServlet {
  @Inject
  private LoginFacade loginFacade;

  /** 
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
//    response.setContentType("text/html;charset=UTF-8");
//    PrintWriter out = response.getWriter();
    // Récupère l'email et le code et si c'est bon, change le statut à "ok".

    String email = request.getParameter("email");
    String code = request.getParameter("cle");
    Login login = loginFacade.findByEmail(email);
    String statut = login.getStatut();
    if (code.equals(login.getCode()) && statut.equals("email")) {
      login.setStatut("ok");
      loginFacade.edit(login);
    }
    else {
      // Anomalie à enregistrer dans les logs de l'application
      
    }
//    try {
      
      /* TODO output your page here
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet Confirmation</title>");  
      out.println("</head>");
      out.println("<body>");
      out.println("<h1>Servlet Confirmation at " + request.getContextPath () + "</h1>");
      out.println("</body>");
      out.println("</html>");
       */
//    } finally {      
//      out.close();
//    }
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /** 
   * Handles the HTTP <code>GET</code> method.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /** 
   * Handles the HTTP <code>POST</code> method.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /** 
   * Returns a short description of the servlet.
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>
}
