package com.pierceecom.blog.post.client;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class ResponseInfo {

	String message;

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
