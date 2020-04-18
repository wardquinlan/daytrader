package dtrader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DTrader {
  private static Log log = LogFactory.getFactory().getInstance(DTrader.class);
  private static String version = "0.70";
  
  public DTrader(List<String> args) {
    loadProperties();
    dispatch(args);
  }

  private void dispatch(List<String> argList) {
    if (argList.size() == 0) {
      usage();
    } else if ("chart".equals((String) argList.get(0))) {
      argList.remove(0);
      chart(argList);
    } else {
      usage();
    }
  }

  private void chart(List<String> argList) {
    String chartName = null;
    if (argList.size() == 0) {
      log.error("no arguments");
      usage();
    }
    String param = argList.remove(0);
    if ("--chart-name".equals(param)) {
      if (argList.size() == 0) {
        log.error("missing --chart-name argument");
        usage();
      }
      chartName = argList.remove(0);
      if (argList.size() == 0) {
        log.error("missing file");
        usage();
      }
      param = argList.remove(0);
    }
    if (argList.size() > 0) {
      log.error("too many arguments");
      usage();
    }
    String fileName = param;
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
      //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);          
      }
    });    
  }
  
  private void usage() {
    System.out.println("dtrader version " + version);
    System.out.println("-------------------------------------------\n");
    System.out.println("usage:\n");
    System.out.println("dtrader chart [--chart-name name] config-file.dt");
    System.out.println("  loads dtrader in gaphical mode and renders 'name' (defaults to first chart found)\n");
    System.exit(1);
  }
  
  private void loadProperties() {
    ClassLoader cl = ClassLoader.getSystemClassLoader();  
    try {
      InputStream is = cl.getResourceAsStream("dtrader.properties");
      if (is == null) {
        log.error("cannot load properties");
        System.exit(1);
      }
      System.getProperties().load(is);    
    } catch(IOException e) {
      log.error("cannot load properties", e);
      System.exit(1);
    }
  }
  
  public static void main(String[] args) {
    ArrayList<String> argList = new ArrayList<String>(args.length);
    for (int i = 0; i < args.length; i++) {
      argList.add(args[i]);
    }
    new DTrader(argList);

    /*
    try {
      Tokenizer tokenizer = new Tokenizer("samples/test1.dt");
      List<Token> tokens = tokenizer.tokenize();
      Map<String, Object> symbolTable = new HashMap<String, Object>();
      Parser parser = new Parser(symbolTable);
      parser.parse(tokens.iterator());
      int n = 5;
    } catch(Exception e) {
      log.error(e);
      System.err.println(e.getMessage());
    }
    */
  }
}
