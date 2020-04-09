import java.util.*;
import java.io.*;

public class Parser {

   private Lexer lex;

   public Parser( Lexer lexer ) {
      lex = lexer;
   }

   public Node parsePath() {
      System.out.println("-----> parsing <path>:");
      // insert your code for Problem 4 here:
      Node first = parseAction();
      Token token = lex.getNextToken();
      if (token.matches("]","")) {
          lex.putBackToken(token);
          return new Node("path", first, null, null);
      }
      else if (token.isKind("eof")) {
          return new Node("path", first, null, null);
      }
      else {
          lex.putBackToken(token);
          Node second = parsePath();
          return new Node("path", first, second, null);
      }
   }// parsePath

   public Node parseAction() {
      System.out.println("-----> parsing <action>:");

      Token token = lex.getNextToken();

      if ( token.isKind("num") ) {
         String number = token.getDetails();  // remember the specific number

         token = lex.getNextToken();

         if ( token.matches("m","") || token.matches("l","") ||
              token.matches("r","") ) {// number followed by simple action
            return new Node( token.getKind(), number, null, null, null);
         }
         else if ( token.matches("[","") ) {
            Node first = parsePath();
            token = lex.getNextToken();
            errorCheck( token, "]" );
            return new Node( "num", number, first, null, null );
         }
         else {
            System.out.println("illegal token " + token );
            System.exit(1);
            return null;
         }
      }
      else {// simple action---use the action as the node kind
         if ( token.matches("m","") || token.matches("l","") ||
              token.matches("r","") || token.matches("u","") ||
              token.matches("d","")
            ) {
            return new Node( token.getKind(), "1", null, null, null );
         }
         else {
            System.out.println("illegal token " + token );
            System.exit(1);
            return null;
         }
      }// simple action
   }

  // check whether token is correct kind
  private void errorCheck( Token token, String kind ) {
    if( ! token.isKind( kind ) ) {
      System.out.println("Error:  expected " + token +
                         " to be of kind " + kind );
      System.exit(1);
    }
  }

  // check whether token is correct kind and details
  private void errorCheck( Token token, String kind, String details ) {
    if( ! token.isKind( kind ) ||
        ! token.getDetails().equals( details ) ) {
      System.out.println("Error:  expected " + token +
                          " to be kind= " + kind +
                          " and details= " + details );
      System.exit(1);
    }
  }

}
