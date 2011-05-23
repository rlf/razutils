/*
 * XmlReaderUtil.java
 *
 * Created on 5. oktober 2007, 16:32
 *
 */

package dk.lockfuglsang.rasmus.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class XmlReaderUtil {
  
  /** Creates a new instance of XmlReaderUtil */
  public XmlReaderUtil() {
  }
  
  /** Returns the document located at the specific URL.
   * @throw IllegalArgumentException if an error occurs. */
  static public Document getDocument(URL url) {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = null;
    try {
      db = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException ex) {
      throw new IllegalArgumentException(ex);
    }
    Document doc = null;
    try {
      doc = db.parse(url.toString());
    } catch (SAXException ex) {
      throw new IllegalArgumentException(ex);
    } catch (IOException ex) {
      throw new IllegalArgumentException(ex);
    }
    return doc;
  }
  static public void toXml(Document xmldoc, OutputStream os) throws TransformerException {
    StreamResult out = new StreamResult(os);
    DOMSource domSource = new DOMSource(xmldoc);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty
       (OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
    transformer.setOutputProperty
       ("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(domSource, out);
  }
  static public void toHtml(Document xmldoc, OutputStream os) throws TransformerException {
    StreamResult out = new StreamResult(os);
    DOMSource domSource = new DOMSource(xmldoc);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty
       (OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "html");
    transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
    transformer.setOutputProperty
       ("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(domSource, out);
  }
  static public String toHtml(Document xmldoc) throws TransformerException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    toHtml(xmldoc, baos);
    return baos.toString();
  }
}
