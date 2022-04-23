package turcobardi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GraphBrowserRenderer {
	private File svgFile = null;
	private String path = null;
	
	
	public GraphBrowserRenderer(String filename) {
		File folder = new File("graph\\" );
		this.path="graph\\"+filename+".svg";
		folder.mkdir();
		svgFile = new File("graph\\"+filename+".svg");
		//indexFile.createNewFile();
	}
	
	public void modifySVG() {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			Document document = domBuilder.parse(svgFile);
			NodeList nodes = document.getElementsByTagName("a");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element elem =  (Element) nodes.item(i);
				//elem.setAttribute("onclick", "console.log('prova')");
				elem.setAttribute("onclick", "renderLabelOnTheSide('prova')");
				//( (Element)nodes.item(i) ).setAttribute("attributeNameToAdd", "attributeStringValueToAdd");
				
				//Element curr = (Element)nodes.item(i);
				//curr.setAttribute("onClick", "prova");
				
			    /*
			    // Remove existing content (if any)
			    while (rowNode.getFirstChild() != null)
			        rowNode.removeChild(rowNode.getFirstChild());

			    // Add text content
			    rowNode.appendChild(document.createTextNode("some value"));
			    */
			}
			//document.toString();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			FileWriter writer = new FileWriter(new File("output.svg"));
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
		
		}
		 catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	// write doc to output stream
	
  
}
