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

/**
 * Class for storing the base part information for a document returned from Content Manager.
 */
package com.ibm.ecm.simpleplugin;


public class BasePart {
	
	private String url = null;
	private String pid = null;
	private String id = null;
    private ResourceObject resourceObject = null;

	/**
	 * returns the content id or label associated with a base part
	 * we use this value to match with the attachments we receive or send
	 * @return String 
	 */
	public String getId() {
		return id;
	}

	/**
	 * returns the pid of the base part
	 * @return String
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * returns the resource manager url of the base part
	 * @return String
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * sets the id of the base part. This id is used to match the id of the attachment with the base part information passed in the xml
	 * @param string
	 */
	public void setId(String string) {
		id = string;
	}

	/**
	 * sets the pid of the base part
	 * @param string
	 */
	public void setPid(String string) {
		pid = string;
	}

	/**
	 * sets the url of the base part
	 * @param string
	 */
	public void setUrl(String string) {
		url = string;
	}

    /**
     * Get resource object.
     * 
     * @return resource object
     */
    public ResourceObject getResourceObject() {
        return resourceObject;
    }

    /**
     * Set resource object.
     * 
     * @param resourceObject resource object
     */
    public void setResourceObject(ResourceObject resourceObject) {
        this.resourceObject = resourceObject;
    }
}
