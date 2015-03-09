package com.axa.server.base.response;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rrodriguez on 17/02/2015.
 */
public class WellBeResponse<T> {

    @Expose private Status status;
    @Expose private T data;
    @Expose private List<Link> links = new ArrayList<Link>();

    public WellBeResponse() {

    }

    /**
     *
     * @return
     * The status
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The data
     */
    public T getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     *
     * @return
     * The links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

}