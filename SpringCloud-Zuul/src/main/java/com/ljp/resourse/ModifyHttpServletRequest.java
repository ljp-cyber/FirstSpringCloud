package com.ljp.resourse;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

public class ModifyHttpServletRequest extends MyRequestWrapper {
	 
    private Map<String, String> customHeaders;
 
    public ModifyHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }
 
    public void putHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }
 
    /*
     * Enumeration已经被更丰富的iterator代替了,Enumeration不允许修改，只可以用于Vector和HashTable
     */
    @Override
	public Enumeration<String> getHeaders(String name) {
    	Vector<String> vector = new Vector<String>();
    	Enumeration<String> oldHeaders = super.getHeaders(name);
    	while(oldHeaders.hasMoreElements()) {
    		vector.add(oldHeaders.nextElement());
    	}
    	String value= getHeader(name);
    	if(value!=null) {
    		vector.add(value);
    	}
		return vector.elements();
	}

	public String getHeader(String name) {
        String value = this.customHeaders.get(name);
        if (value != null) {
            return value;
        }
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }
 
    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<>(customHeaders.keySet());
        Enumeration<String> enumeration = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            set.add(name);
        }
        return Collections.enumeration(set);
    }
 
}
