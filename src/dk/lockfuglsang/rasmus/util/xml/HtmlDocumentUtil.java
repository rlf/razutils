/*
 * HtmlDocumentUtil.java
 *
 * Created on 5. oktober 2007, 17:45
 *
 */

package dk.lockfuglsang.rasmus.util.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class HtmlDocumentUtil {
  
  /** Creates a new instance of HtmlDocumentUtil */
  private HtmlDocumentUtil() {
    // Not possible to instantiate
  }
  public static HTMLDocument getDocument(URL url) throws IOException {
    URLConnection connection = url.openConnection();
    InputStream is = connection.getInputStream();
    return getDocument(is);
  }
  public static HTMLDocument getDocument(InputStream is) throws IOException {
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    
    HTMLEditorKit htmlKit = new HTMLEditorKit();
    HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
    HTMLEditorKit.Parser parser = new ParserDelegator();
    HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);
    parser.parse(br, callback, true);
    
    return htmlDoc;
  }
}
