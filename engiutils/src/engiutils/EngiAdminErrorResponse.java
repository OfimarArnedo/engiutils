package engiutils;

public class EngiAdminErrorResponse extends EngiAdminAbstractResponse {
  public EngiAdminErrorResponse(String message) {
    super(message);
  }
  
  protected String toXml() {
    String out = "<err msg=\"";
    out = String.valueOf(out) + this.message;
    out = String.valueOf(out) + "\" />";
    return out;
  }
}
