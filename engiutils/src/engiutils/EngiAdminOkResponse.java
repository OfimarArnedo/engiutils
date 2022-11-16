package engiutils;

public class EngiAdminOkResponse extends EngiAdminAbstractResponse {
  public EngiAdminOkResponse() {
    this.message = null;
  }
  
  public EngiAdminOkResponse(String message) {
    super(message);
  }
  
  protected String toXml() {
    String out = "<ok";
    if (this.message != null) {
      out = String.valueOf(out) + " msg=\"";
      out = String.valueOf(out) + this.message;
      out = String.valueOf(out) + "\"";
    } 
    out = String.valueOf(out) + " />";
    return out;
  }
}
