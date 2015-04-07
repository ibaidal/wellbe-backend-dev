package com.axa.server.base.response;

import com.google.gson.annotations.Expose;

/**
 * Created by rrodriguez on 17/02/2015.
 */
public class WellBeResponse<T> {

    @Expose private Status status;
    @Expose private T data;

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

	@Override
	public String toString() {
		return "WellBeResponse [status=" + status + ", data=" + data + "]";
	}

}