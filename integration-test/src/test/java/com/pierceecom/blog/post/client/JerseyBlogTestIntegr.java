package com.pierceecom.blog.post.client;

import java.util.List;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.pierceecom.blog.post.client.Post;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnitParamsRunner.class)
public class JerseyBlogTestIntegr {

    private static final Post POST_1 = new Post("1", "First title", "First content");
    private static final Post POST_2 = new Post("2", "Second title", "Second content");

    private static final String POSTS_URI = "http://localhost:8080/blog-web/posts/";

    private WebTarget webTarget;

    public JerseyBlogTestIntegr() {
    }

    @Before
    public void setUp() {
        webTarget = ClientBuilder.newClient().target(POSTS_URI);
    }

    @Test
    public void test_01_BlogWithoutPosts() {
        Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        List<Post> responseEntity = response.readEntity(new GenericType<List<Post>>(){});
        assertTrue(responseEntity.isEmpty());
    }

    @Test
    public void test_02_AddPosts() {
        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(POST_1));
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(POSTS_URI + POST_1.getId(), response.getHeaderString(HttpHeaders.LOCATION));

        response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(POST_2));
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(POSTS_URI + POST_2.getId(), response.getHeaderString(HttpHeaders.LOCATION));
    }

    @Test
    public void test_03_AddDuplicatePost() {
        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(POST_1));
        assertEquals(Status.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
    }

    @Test
    public void test_04_GetPost() {
        Response response = webTarget.path("/" + POST_1.getId()).request(MediaType.APPLICATION_JSON).get();
        Post responseEntity = response.readEntity(Post.class);
        assertTrue(POST_1.equals(responseEntity));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        response = webTarget.path("/" + POST_2.getId()).request(MediaType.APPLICATION_JSON).get();
        responseEntity = response.readEntity(Post.class);
        assertTrue(POST_2.equals(responseEntity));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void test_05_UpdatePost() {
        Post updatedPost = new Post(POST_1.getId(), "updated title", "updated content");
        Response response = webTarget.request(MediaType.APPLICATION_JSON).put(Entity.json(updatedPost));
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(POSTS_URI + POST_1.getId(), response.getHeaderString(HttpHeaders.LOCATION));

        response = webTarget.path("/" + POST_1.getId()).request(MediaType.APPLICATION_JSON).get();
        Post responseEntity = response.readEntity(Post.class);
        assertTrue(updatedPost.equals(responseEntity));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        //restore previous values of POST_1
        response = webTarget.request(MediaType.APPLICATION_JSON).put(Entity.json(POST_1));
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void test_06_GetAllPosts() {
        Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        List<Post> responseEntity = response.readEntity(new GenericType<List<Post>>(){});
        assertTrue(responseEntity.contains(POST_1));
        assertTrue(responseEntity.contains(POST_2));
    }
    
    @Test
    public void test_07_DeletePosts() {
        Response response = webTarget.path("/" + POST_1.getId()).request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Status.ACCEPTED.getStatusCode(), response.getStatus());
        // Should now be gone
        response = webTarget.path("/" + POST_1.getId()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());

        response = webTarget.path("/" + POST_2.getId()).request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Status.ACCEPTED.getStatusCode(), response.getStatus());
        // Should now be gone
        response = webTarget.path("/" + POST_2.getId()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }


    @Test
    public void test_08_GetAllPostsShouldNowBeEmpty() {
        test_01_BlogWithoutPosts();
    }

    @Test
    public void test_09_DeleteNonExistingPosts() {
        Response response = webTarget.path("/1234").request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void test_10_GetNonExistingPosts() {
        Response response = webTarget.path("/1234").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    @Parameters({",,",
                "12345,,",
                "12345,title,",
                "12345,,content",
                ",,content",
                ",title,",
                ",title,content"})
    public void test_11_AddInvalidPost(String id, String title, String content) {
        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(new Post(id, title, content)));
        assertEquals(Status.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
    }

    @Test
    @Parameters({",,",
            "12345,,",
            "12345,title,",
            "12345,,content",
            ",,content",
            ",title,",
            ",title,content"})
    public void test_12_UpdateInvalidPost(String id, String title, String content) {
        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(new Post(id, title, content)));
        assertEquals(Status.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
    }
}
