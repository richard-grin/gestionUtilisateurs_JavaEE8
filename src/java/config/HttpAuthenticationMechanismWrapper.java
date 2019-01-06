package config;

import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author grin
 */
//@Alternative
//@Priority(500)
public class HttpAuthenticationMechanismWrapper { // implements HttpAuthenticationMechanism {

  private HttpAuthenticationMechanism wrapped;

  public HttpAuthenticationMechanismWrapper() {
//
  }

  public HttpAuthenticationMechanismWrapper(HttpAuthenticationMechanism httpAuthenticationMechanism) {
    this.wrapped = httpAuthenticationMechanism;
  }

  public HttpAuthenticationMechanism getWrapped() {
    return wrapped;
  }

//  @Override
//  public AuthenticationStatus validateRequest(
//          HttpServletRequest request,
//          HttpServletResponse response,
//          HttpMessageContext context) throws AuthenticationException {
//    return getWrapped().validateRequest(request, response, context);
//  }
//
//  @Override
//  public AuthenticationStatus secureResponse(
//          HttpServletRequest request,
//          HttpServletResponse response,
//          HttpMessageContext context) throws AuthenticationException {
//    return getWrapped().secureResponse(request, response, context);
//  }
//
//  @Override
//  public void cleanSubject(
//          HttpServletRequest request,
//          HttpServletResponse response,
//          HttpMessageContext context) {
//    getWrapped().cleanSubject(request, response, context);
//  }

}
