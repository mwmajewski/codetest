package com.pierceecom.blog.post;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResponseInfo {

    private String message;

    public ResponseInfo() {
    }

    public ResponseInfo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
