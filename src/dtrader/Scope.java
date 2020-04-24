package dtrader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Scope {
  private Scope parent;
  private Map<String, Symbol> symbolTable = new HashMap<String, Symbol>();
  private Map<String, Object> properties = new HashMap<String, Object>();
  private List<Statement> statements = new ArrayList<Statement>();
  private List<Chart> charts = new ArrayList<Chart>();
  
  public Scope() {
    this.parent = null;
  }
  
  public Scope(Scope parent) {
    this.parent = parent;
  }
  
  public Symbol getSymbol(String name) {
    if (symbolTable.get(name) == null) {
      return parent.getSymbol(name);
    }
    return symbolTable.get(name);
  }

  public void putSymbol(String name, Symbol symbol) {
    if (parent.getSymbol(name) != null) {
      parent.putSymbol(name, symbol);
    } else {
      symbolTable.put(name, symbol);
    }
  }
  
  public Object getProperty(String name) {
    if (properties.get(name) == null) {
      return parent.getProperty(name);
    }
    return properties.get(name);
  }
  
  public void putProperty(String name, Object value) {
    if (parent.getProperty(name) != null) {
      parent.putProperty(name, value);
    } else {
      properties.put(name, value);
    }
  }
  
  public List<Statement> getStatements() {
    return statements;
  }
  
  public List<Chart> getCharts() {
    return charts;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("SCOPE.symbolTable=[\n");
    Iterator<String> itr = symbolTable.keySet().iterator();
    while (itr.hasNext()) {
      String key = itr.next();
      Symbol symbol = symbolTable.get(key);
      sb.append(key + "=>" + symbol + "\n");
    }
    sb.append("]\n");
    sb.append("SCOPE.properties=[\n");
    itr = properties.keySet().iterator();
    while (itr.hasNext()) {
      String key = itr.next();
      Object property = properties.get(key);
      sb.append(key + "=>" + property + "\n");
    }
    sb.append("]\n");
    sb.append("SCOPE.statements=[\n");
    for(Statement statement: statements) {
      sb.append(statement + "\n");
    }
    sb.append("]\n");
    sb.append("SCOPE.charts=[\n");
    for (Chart chart: charts) {
      sb.append(chart + "\n");
    }
    sb.append("]\n");
    return sb.toString();
  }
}