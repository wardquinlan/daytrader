package dtrader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Parser {
  private static Log log = LogFactory.getFactory().getInstance(Parser.class);
  private static FunctionCaller funcCaller = new FunctionCaller();
  private Map<String, Object> symbolTable;
  
  public Parser(Map<String, Object> symbolTable) {
    this.symbolTable = symbolTable;
  }
  
  public void parse(Iterator<Token> itr) throws Exception {
    while (itr.hasNext()) {
      parseStatement(itr);
    }
  }
  
  public void parseStatement(Iterator<Token> itr) throws Exception {
    List<Token> statement = new ArrayList<Token>();
    while (true) {
      if (!itr.hasNext()) {
        throw new Exception("unexpected end of file");
      }
      Token tk = itr.next();
      if (tk.getType() == Token.SEMI) {
        break;
      }
      statement.add(tk);
    }
    TokenIterator itr2 = new TokenIterator(statement);
    if (!itr2.hasNext()) {
      // empty statement
      return;
    }
    Token tk = itr2.next();
    expression(tk, itr2);
    if (itr2.hasNext()) {
      log.error("unexpected symbol at end of statement");
      throw new Exception("syntax error");
    }
  }
  
  private Object expression(Token tk, TokenIterator itr) throws Exception {
    Object val1 = term(tk, itr);
    while (true) {
      if (!itr.hasNext()) {
        break;
      }
      if (itr.peek().getType() == Token.PLUS) {
        tk = itr.next();
        Object val2 = term(tk, itr);
        if (val1 instanceof String) {
          val1 = val1 + val2.toString();
        } else if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 + (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          val1 = new Double((Integer) val1 + (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          val1 = new Double((Double) val1 + (Integer) val2);
        } else {
          val1 = new Double((Double) val1 + (Double) val2);
        }
      } else if (itr.peek().getType() == Token.MINUS) {
        tk = itr.next();
        Object val2 = term(tk, itr);
        if (val1 instanceof String) {
          throw new Exception("unsupported string operation: " + val1);
        } else if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 - (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          val1 = new Double((Integer) val1 - (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          val1 = new Double((Double) val1 - (Integer) val2);
        } else {
          val1 = new Double((Double) val1 - (Double) val2);
        }
      } else {
        break;
      }
    }
    return val1;
  }
  
  private Object term(Token tk, TokenIterator itr) throws Exception {
    Object val1 = primary(tk, itr);
    while (true) {
      if (!itr.hasNext()) {
        break;
      }
      if (itr.peek().getType() == Token.MULT) {
        tk = itr.next();
        Object val2 = primary(tk, itr);
        if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 * (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          val1 = new Double((Integer) val1 * (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          val1 = new Double((Double) val1 * (Integer) val2);
        } else {
          val1 = new Double((Double) val1 * (Double) val2);
        }
      }
      else if (itr.peek().getType() == Token.DIV) {
        tk = itr.next();
        Object val2 = primary(tk, itr);
        if (val1 instanceof Integer && val2 instanceof Integer) {
          if ((Integer) val2 == 0) {
            throw new Exception("divide by 0 error");
          }
          if ((Integer) val1 % (Integer) val2 == 0) {
            val1 = new Integer((Integer) val1 / (Integer) val2);
          } else {
            val1 = new Double(((Integer) val1).doubleValue() / ((Integer) val2).doubleValue());
          }
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          if ((Double) val2 == 0d) {
            throw new Exception("divide by 0 error");
          }
          val1 = new Double((Integer) val1 / (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          if ((Integer) val2 == 0) {
            throw new Exception("divide by 0 error");
          }
          val1 = new Double((Double) val1 / (Integer) val2);
        } else {
          val1 = new Double((Double) val1 / (Double) val2);
          if ((Double) val2 == 0d) {
            throw new Exception("divide by 0 error");
          }
        }
      } else {
        break;
      }
    }
    return val1;
  }
  
  private Object primary(Token tk, TokenIterator itr) throws Exception {
    if (tk.getType() == Token.INTEGER || tk.getType() == Token.REAL || tk.getType() == Token.STRING) {
      return tk.getValue();
    }
    if (tk.getType() == Token.SYMBOL) {
      if (itr.hasNext() && itr.peek().getType() == Token.ASSIGN) {
        tk = itr.next();
        Object val = expression(tk, itr);
        symbolTable.put((String) tk.getValue(), val);
      }
      Object val = symbolTable.get(tk.getValue());
      if (val == null) {
        throw new Exception("uninitialized symbol: " + tk.getValue());
      }
      return val;
    }
    if (tk.getType() == Token.FUNC) {
      String funcName = (String) tk.getValue();
      if (!itr.hasNext()) {
        log.error("unexpected end of input: " + funcName);
        throw new Exception("syntax error: " + funcName);
      }
      tk = itr.next();
      if (tk.getType() != Token.LPAREN) {
        log.error("expecting left parenthesis: " + funcName);
        throw new Exception("syntax error: " + funcName);
      }
      tk = itr.next();
      List<Object> params = new ArrayList<Object>();
      while (tk.getType() != Token.RPAREN) {
        Object val = expression(tk, itr);
        params.add(val);
        if (!itr.hasNext()) {
          log.error("unexpected end of input: " + funcName);
          throw new Exception("syntax error: " + funcName);
        }
        tk = itr.next();
        if (tk.getType() == Token.COMMA) {
          if (!itr.hasNext()) {
            log.error("unexpected end of input: " + funcName);
            throw new Exception("syntax error: " + funcName);
          }
          tk = itr.next();
          if (tk.getType() == Token.RPAREN) {
            log.error("unexpected comma: " + funcName);
            throw new Exception("syntax error: " + funcName);
          }
        }
      }
      return funcCaller.invokeFunction(funcName, params);
    }
    throw new Exception("unsupported primary expression: " + tk);
  }
}
