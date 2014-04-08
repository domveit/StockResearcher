package org.djv.stockresearcher;

import java.awt.List;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AEGenerator {

	public static void main(String... args) {

		try {

			InputStream is = AEGenerator.class.getClassLoader()
					.getResourceAsStream("test.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getFirstChild().getChildNodes();
			processSubNode(nList, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processSubNode(NodeList nList, String s) {
//		System.out.println("Current Element :" + s);
		boolean hasNonTextChild = false;
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
//			System.out.println("Child :" +  nNode.getNodeName() + " " + nNode.getNodeType());
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				hasNonTextChild = true;
				processSubNode(nNode.getChildNodes(), s + nNode.getNodeName());
			}
		}
		if (!hasNonTextChild){
			System.out.println("field: " + s);
		}
	}

}
