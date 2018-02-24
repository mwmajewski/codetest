package com.pierceecom.blog.post;

public class PostAlreadyExistsException extends Exception {

    public PostAlreadyExistsException(String message) {
        super(message);
    }
}
