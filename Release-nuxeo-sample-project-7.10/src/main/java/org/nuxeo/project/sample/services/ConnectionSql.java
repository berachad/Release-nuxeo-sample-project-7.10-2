package org.nuxeo.project.sample.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream; 
import java.sql.DriverManager;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.mysql.jdbc.Connection;

public class ConnectionSql {
	public static ConnectionSql connection;

	public static ConnectionSql connection() {
		try {
			String driverName = "org.postgresql.Driver";
			Class.forName(driverName);

			InputStream input = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("../datasources-config.xml");

			try {
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(input));

				XPath xpath = XPathFactory.newInstance().newXPath();
				String url = (String) xpath.compile("//component//extension//datasource/@url").evaluate(document,
						XPathConstants.STRING);

				String username = (String) xpath.compile("//component//extension//datasource/@username")
						.evaluate(document, XPathConstants.STRING);

				String password = (String) xpath.compile("//component//extension//datasource/@password")
						.evaluate(document, XPathConstants.STRING);

				connection = (ConnectionSql) DriverManager.getConnection(url, username, password);

			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (ParserConfigurationException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {

			System.err.println("Could not find the database driver");
		} catch (Exception e) {

			System.err.println("Could not connect to the database");
		}
		return connection;
	}
	public ConnectionSql getConnection() {
		return this.connection;
	}

	public void setConnection(ConnectionSql connection) {
		this.connection = connection;
	}
	
}
