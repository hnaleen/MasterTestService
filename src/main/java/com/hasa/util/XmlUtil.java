package com.hasa.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class XmlUtil
{
  private static XmlUtil instance;

  public String moveTestSuiteXmlToTempLocation(InputStream xmlFileAsStream)
  {
    String tempXmlPath = null;
    try
    {
      Document doc = parseInputXmlStream(xmlFileAsStream);
      removeExistingListeners(doc);
      tempXmlPath = writeToTempXml(doc);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Error Parsing TestNG XML Suite File", e);
    }
    return tempXmlPath;
  }

  private String writeToTempXml(Document doc) throws IOException, TransformerException
  {
    File tempFile = File.createTempFile("TestSuite", ".xml");
    Transformer t = TransformerFactory.newInstance().newTransformer();
    t.transform(new DOMSource(doc), new StreamResult(tempFile));
    return tempFile.getPath();
  }

  private void removeExistingListeners(Document doc)
  {
    if (doc.getElementsByTagName("listeners").getLength() > 0)
    {
      Element listeners = (Element) doc.getElementsByTagName("listeners").item(0);
      listeners.getParentNode().removeChild(listeners);
    }
  }

  private Document parseInputXmlStream(InputStream xmlFileAsStream)
      throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    return dBuilder.parse(xmlFileAsStream);
  }

  public static XmlUtil getInstance()
  {
    if (instance == null)
    {
      instance = new XmlUtil();
    }
    return instance;
  }

  private XmlUtil()
  {
  }
}
