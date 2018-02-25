package com.pierceecom.blog.post;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResponseInfoDto {

    private String message;

    public ResponseInfoDto() {
    }

    ResponseInfoDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
