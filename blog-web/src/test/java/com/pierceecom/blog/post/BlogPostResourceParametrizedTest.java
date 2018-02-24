package com.pierceecom.blog.post;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class BlogPostResourceParametrizedTest {

    private BlogPostResource sut;

    @Before
    public void setUp() throws Exception {
        sut = new BlogPostResource(null);
    }

    @Test
    @Parameters({",,",
            "12345,,",
            "12345,title,",
            "12345,,content",
            ",,content",
            ",title,",
            ",title,content"})
    public void shouldReturnMethodNotAllowedOnCreateInvalidPost(String id, String title, String content) throws PostAlreadyExistsException {
        //given
        //when
        Response response = sut.addPost(new Post(id, title, content), null);
        //then
        assertThat(response.getStatus(), equalTo(Status.METHOD_NOT_ALLOWED.getStatusCode()));
    }

    @Test
    @Parameters({",,",
            "12345,,",
            "12345,title,",
            "12345,,content",
            ",,content",
            ",title,",
            ",title,content"})
    public void shouldReturnMethodNotAllowedOnUpdateNonExistingPost(String id, String title, String content) throws PostNotFoundException {
        //given
        //when
        Response response = sut.updatePost(new Post(id, title, content), null);
        //then
        assertThat(response.getStatus(), equalTo(Status.METHOD_NOT_ALLOWED.getStatusCode()));
    }
}