package com.axa.server.base.response;

import com.google.gson.annotations.Expose;

public class Link {

    public static enum Method {
        GET, POST, PUT, DELETE;
    };

    @Expose private String rel;
    @Expose private String URI;
    @Expose private String method = Method.GET.toString();

    public Link() {
    }

    
    public Link(String rel, String uRI) {
		super();
		this.rel = rel;
		URI = uRI;
	}


	public Link(String rel, String uRI, String method) {
		super();
		this.rel = rel;
		URI = uRI;
		this.method = method;
	}


	/**
     *
     * @return
     * The rel
     */
    public String getRel() {
        return rel;
    }

    /**
     *
     * @param rel
     * The rel
     */
    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     *
     * @return
     * The URI
     */
    public String getURI() {
        return URI;
    }

    /**
     *
     * @param URI
     * The URI
     */
    public void setURI(String URI) {
        this.URI = URI;
    }

    /**
     *
     * @return
     * The method
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @param method
     * The method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Link{");
        sb.append("rel='").append(rel).append('\'');
        sb.append(", URI='").append(URI).append('\'');
        sb.append(", method=").append(method);
        sb.append('}');
        return sb.toString();
    }
}
