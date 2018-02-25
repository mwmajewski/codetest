package com.pierceecom.blog.post;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BlogPostResourceTest {

    private static final String RESOURCE_URL = "http://localhost:8080/blog";
    private static String ID_POST_1 = "1";
    private static PostDto POST_DTO_1 = new PostDto(ID_POST_1, "1st title", "1st content");
    private static Post POST_1 = new Post(ID_POST_1, "1st title", "1st content");
    private static PostDto POST_DTO_2 = new PostDto("2", "2nd title", "2nd content");
    private static Post POST_2 = new Post("2", "2nd title", "2nd content");

    @Mock
    private PostDao dao;

    @Mock
    private UriInfo uriInfo;

    private BlogPostResource sut;

    @Before
    public void setUp() {
        sut = new BlogPostResource(dao);
    }

    @Test
    public void shouldFindPost(){
        //given
        given(dao.findById(ID_POST_1)).willReturn(Optional.of(POST_1));
        //when
        Response response = sut.findPost(ID_POST_1);
        //then
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));
        assertThat(response.getEntity(), equalTo(POST_DTO_1));
        verify(dao, times(1)).findById(ID_POST_1);
    }

    @Test
    public void shouldNotFindPost(){
        //given
        given(dao.findById(ID_POST_1)).willReturn(Optional.empty());
        //when
        Response response = sut.findPost(ID_POST_1);
        //then
        assertThat(response.getStatus(), equalTo(Status.NOT_FOUND.getStatusCode()));
        verify(dao, times(1)).findById(ID_POST_1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnAllPosts(){
        //given
        given(dao.findAll()).willReturn(Stream.of(POST_1, POST_2)
                                              .collect(Collectors.toList()));
        //when
        Response response = sut.getAllPosts();
        //then
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));
        assertThat((List<PostDto>)response.getEntity(), hasItems(POST_DTO_1, POST_DTO_2));
        verify(dao, times(1)).findAll();
    }

    @Test
    public void shouldReturnNotFoundOnDeleteNonExistingPost() throws PostNotFoundException {
        //given
        willThrow(PostNotFoundException.class).given(dao).remove(ID_POST_1);
        //when
        Response response = sut.deletePost(ID_POST_1);
        //then
        assertThat(response.getStatus(), equalTo(Status.NOT_FOUND.getStatusCode()));
        verify(dao, times(1)).remove(ID_POST_1);
    }

    @Test
    public void shouldDeletePost() throws PostNotFoundException {
        //given
        //when
        Response response = sut.deletePost(ID_POST_1);
        //then
        assertThat(response.getStatus(), equalTo(Status.ACCEPTED.getStatusCode()));
        verify(dao, times(1)).remove(ID_POST_1);
    }

    @Test
    public void shouldCreatePost() throws PostAlreadyExistsException {
        //given
        given(uriInfo.getPath()).willReturn(RESOURCE_URL);
        //when
        Response response = sut.addPost(POST_DTO_1, uriInfo);
        //then
        assertThat(response.getStatus(), equalTo(Status.CREATED.getStatusCode()));
        assertThat(response.getHeaderString(HttpHeaders.LOCATION), equalTo(RESOURCE_URL + "/" + ID_POST_1));
        verify(dao, times(1)).add(POST_1);
    }

    @Test
    public void shouldReturnMethodNotAllowedOnCreatePostWithDuplicateId() throws PostAlreadyExistsException {
        //given
        willThrow(PostAlreadyExistsException.class).given(dao).add(POST_1);
        //when
        Response response = sut.addPost(POST_DTO_1, uriInfo);
        //then
        assertThat(response.getStatus(), equalTo(Status.METHOD_NOT_ALLOWED.getStatusCode()));
        verify(dao, times(1)).add(POST_1);
    }

    @Test
    public void shouldUpdatePost() throws PostNotFoundException {
        //given
        given(uriInfo.getPath()).willReturn(RESOURCE_URL);
        //when
        Response response = sut.updatePost(POST_DTO_1, uriInfo);
        //then
        assertThat(response.getStatus(), equalTo(Status.CREATED.getStatusCode()));
        assertThat(response.getHeaderString(HttpHeaders.LOCATION), equalTo(RESOURCE_URL + "/" + ID_POST_1));
        verify(dao, times(1)).update(POST_1);
    }

    @Test
    public void shouldReturnMethodNotAllowedOnUpdateNonExistingPost() throws PostNotFoundException {
        //given
        willThrow(PostNotFoundException.class).given(dao).update(POST_1);
        //when
        Response response = sut.updatePost(POST_DTO_1, uriInfo);
        //then
        assertThat(response.getStatus(), equalTo(Status.NOT_FOUND.getStatusCode()));
        verify(dao, times(1)).update(POST_1);
    }
}