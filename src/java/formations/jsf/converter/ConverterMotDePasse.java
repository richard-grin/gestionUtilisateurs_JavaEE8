package formations.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import util.HashMdp;

/**
 * Convertisseur de mot de passe.
 * String <-> byte[]
 * TODO: Ne correspond pas au code !!!
 * @author richard
 */
@FacesConverter(value = "converterMotDePasse", managed = true)
public class ConverterMotDePasse implements javax.faces.convert.Converter<String> {
  @Inject
  private HashMdp passwordHash;

  @Override
  public String getAsObject(FacesContext context, UIComponent component, String value) {
    return passwordHash.generate(value);
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, String value) {
    return "";
  }
  
}
