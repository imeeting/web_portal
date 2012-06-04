package com.richitec.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TextMessageResponse {

	private int httpStatusCode;

	private int code;

	public TextMessageResponse(int status, InputStream content)
			throws ParserConfigurationException, SAXException, IOException {

		this.httpStatusCode = status;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(content);
		NodeList list = document.getElementsByTagName("code");

		this.code = Integer.parseInt(list.item(0).getFirstChild().getNodeValue());
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public int getCode() {
		return code;
	}

}
