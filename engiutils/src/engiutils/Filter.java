package engiutils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Filter {
   private int matchedLength;
   private final String match;
   private final FilterComparator comparator;

   public Filter(List cfg) {
      char algorithm = ((String)cfg.get(0)).toUpperCase().charAt(0);
      this.matchedLength = Integer.valueOf((String)cfg.get(1));
      this.match = (String)cfg.get(2);
      switch(algorithm) {
      case 'B':
         this.comparator = new FilterComparator() {
            public boolean match(String content) {
               return content.startsWith(Filter.this.match);
            }
         };
         break;
      case 'M':
         Pattern pattern = Pattern.compile(this.escape(this.match));
         Matcher matcher = pattern.matcher("");
         this.comparator = new FilterComparator(matcher) {
             private final Matcher m;

             {
                this.m = var2;
             }

//             public boolean match(String content) {
//                this.m.reset(content);
//                return this.m.matches();
//             }
          };
         break;
      case 'N':
         this.comparator = new FilterComparator() {
            public boolean match(String content) {
               return true;
            }
         };
         break;
      default:
         this.comparator = new FilterComparator() {
            public boolean match(String content) {
               return false;
            }
         };
      }

   }

   private String escape(String pattern) {
      pattern = pattern.replaceAll("\\.", "\\\\.");
      pattern = pattern.replaceAll("\\*", ".*");
      pattern = pattern.replaceAll("\\?", ".?");
      return pattern;
   }

   public String getMatchedData(String value) {
      return value.length() < this.matchedLength ? value : value.substring(0, this.matchedLength);
   }

   public boolean match(String content) {
      return this.comparator.match(content);
   }
}


