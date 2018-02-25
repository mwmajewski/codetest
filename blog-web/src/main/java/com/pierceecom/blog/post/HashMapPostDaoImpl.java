package com.pierceecom.blog.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

@ApplicationScoped
public class HashMapPostDaoImpl implements PostDao {

    private Map<String, Post> postStore = new HashMap<>();

    @Override
    public void add(@NotNull Post post) throws PostAlreadyExistsException {
        synchronized(postStore) {
            checkPostNotExists(post);
            postStore.put(post.getId(), post);
        }
    }

    private void checkPostNotExists(@NotNull Post post) throws PostAlreadyExistsException {
        if (postStore.containsKey(post.getId())) {
            throw new PostAlreadyExistsException(String.format("A post with id: [%s] already exists", post.getId()));
        }
    }

    @Override
    public void remove(@NotNull String id) throws PostNotFoundException {
        synchronized(postStore) {
            checkPostExists(id);
            postStore.remove(id);
        }
    }

    @Override
    public void update(@NotNull Post post) throws PostNotFoundException {
        synchronized(postStore) {
            String id = post.getId();
            checkPostExists(id);
            postStore.put(id, post);
        }
    }

    private void checkPostExists(String id) throws PostNotFoundException {
        if (!postStore.containsKey(id)) {
            throw new PostNotFoundException((String.format("A post with id: [%s] does not exist", id)));
        }
    }

    @Override
    public Optional<Post> findById(@NotNull String id) {
        return Optional.ofNullable(postStore.get(id));
    }

    @Override
    public List<Post> findAll() {
        return new ArrayList<>(postStore.values());
    }
}
