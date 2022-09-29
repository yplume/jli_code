/*
 * @copyright(disclaimer)
 * 
 * DISCLAIMER OF WARRANTIES. The following IBM Content Manager Enterprise Edition 
 * code is sample code created by IBM Corporation. IBM grants you a nonexclusive 
 * copyright license to use this sample code example to generate similar function 
 * tailored to your own specific needs. This sample code is not part of any standard 
 * IBM product and is provided to you solely for the purpose of assisting you in the 
 * development of your applications. This example has not been thoroughly tested 
 * under all conditions. IBM, therefore cannot guarantee nor may you imply 
 * reliability, serviceability, or function of these programs. The code is provided 
 * "AS IS", without warranty of any kind. IBM shall not be liable for any damages 
 * arising out of your or any other parties use of the sample code, even if IBM has 
 * been advised of the possibility of such damages. If you do not agree with these 
 * terms, do not use the sample code.
 * 
 * Licensed Materials - Property of IBM
 * 5724-B19 / 5697-H60
 * Copyright IBM Corp. 1994, 2011 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted 
 * by GSA ADP Schedule Contract with IBM Corp.
 * 
 * @endCopyright
 */

package com.ibm.ecm.simpleplugin;

/**
 * Attribute
 *
 * A class for storing the name value attribute pairs of a CM document
 * 
 */
public class Attribute {
	
	private String name;
	private String value;

	/**
	 * getName() returns the name of the attribute
	 * 
	 * @return String which is the name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * getValue() returns the value of the attribute
	 * @return String which is the value of the attribute
	 */
	public String getValue() {
		return value;
	}

	/**
	 * setName() sets the name of the attribute
	 * @param string
	 * 
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * setValue() sets the value of the attribute
	 * @param string
	 */
	public void setValue(String string) {
		value = string;
	}

}
