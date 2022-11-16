package engiutils;

public class EngiAdminRawResponse extends EngiAdminAbstractResponse {
  public EngiAdminRawResponse(String message) {
    super(message);
  }
  
  protected String toXml() {
    String out = "<raw><![CDATA[";
    out = String.valueOf(out) + this.message;
    out = String.valueOf(out) + "]]></raw>";
    return out;
  }
}
