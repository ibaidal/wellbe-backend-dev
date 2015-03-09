package com.axa.server.base.response;

import com.google.gson.annotations.Expose;

public class Status {

	@Expose private int code;
	@Expose private String message;
	@Expose private String description;

    public Status() {
    }
   

    public Status(int code, String message, String description) {
		super();
		this.code = code;
		this.message = message;
		this.description = description;
	}


	/**
     *
     * @return
     * The code
     */
    public int getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Status{");
        sb.append("code=").append(code);
        sb.append(", message='").append(message).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
