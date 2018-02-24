package com.pierceecom.blog.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class HashMapPostDaoImplTest {

    private static String ID_POST_1 = "1";
    private static Post POST_1 = new Post(ID_POST_1, "1st title", "1st content");
    private static String ID_POST_2 = "2";
    private static Post POST_2 = new Post(ID_POST_2, "1st title", "1st content");

    private HashMapPostDaoImpl sut;

    @Before
    public void setUp() throws Exception {
        sut = new HashMapPostDaoImpl();
    }

    @Test
    public void shouldAddPost() throws PostAlreadyExistsException {
        //given
        Map<String, Post> postStore = new HashMap<>();
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        sut.add(POST_1);
        //then
        assertThat(postStore.get(ID_POST_1), equalTo(POST_1));
    }

    @Test(expected = PostAlreadyExistsException.class)
    public void shouldThrowExceptionOnAddDuplicatePost() throws PostAlreadyExistsException {
        //given
        Map<String, Post> postStore = new HashMap<>();
        postStore.put(ID_POST_1, new Post());
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        sut.add(POST_1);
        //then
        assertThat(postStore.get(ID_POST_1), equalTo(POST_1));
    }

    @Test
    public void shouldRemovePost() throws PostNotFoundException {
        //given
        Map<String, Post> postStore = new HashMap<>();
        postStore.put(ID_POST_1, new Post());
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        sut.remove(ID_POST_1);
        //then
        assertThat(postStore.get(ID_POST_1), nullValue());
    }

    @Test(expected = PostNotFoundException.class)
    public void shouldThrowExceptionOnRemoveNonExistingPost() throws PostNotFoundException {
        //given
        Map<String, Post> postStore = new HashMap<>();
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        sut.remove(ID_POST_1);
        //then
    }

    @Test
    public void shouldUpdatePost() throws PostNotFoundException {
        //given
        Map<String, Post> postStore = new HashMap<>();
        postStore.put(ID_POST_1, new Post());
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        sut.update(POST_1);
        //then
        assertThat(postStore.get(ID_POST_1), equalTo(POST_1));
    }

    @Test(expected = PostNotFoundException.class)
    public void shouldThrowExceptionOnUpdateNonExistingPost() throws PostNotFoundException {
        //given
        Map<String, Post> postStore = new HashMap<>();
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        sut.update(POST_1);
        //then
    }

    @Test
    public void shouldFindPost() {
        //given
        Map<String, Post> postStore = new HashMap<>();
        postStore.put(ID_POST_1, POST_1);
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        Optional<Post> optionalResult = sut.findById(ID_POST_1);
        //then
        assertThat(optionalResult.get(), equalTo(POST_1));
    }

    @Test
    public void shouldReturnOptionalEmptyOnFindNonExistingPost() {
        //given
        Map<String, Post> postStore = new HashMap<>();
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        Optional<Post> optionalResult = sut.findById(ID_POST_1);
        //then
        assertThat(optionalResult.isPresent(), equalTo(false));
    }

    @Test
    public void shouldFindAllPosts() {
        //given
        Map<String, Post> postStore = new HashMap<>();
        postStore.put(ID_POST_1, POST_1);
        postStore.put(ID_POST_2, POST_2);
        Whitebox.setInternalState(sut, "postStore", postStore);
        //when
        List<Post> result = sut.findAll();
        //then
        assertThat(result, hasItems(POST_1, POST_2));
    }
}