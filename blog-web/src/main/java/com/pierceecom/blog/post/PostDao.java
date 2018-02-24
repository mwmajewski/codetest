package com.pierceecom.blog.post;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

public interface PostDao {

    void add(@NotNull Post post) throws PostAlreadyExistsException;
    void remove(@NotNull String id) throws PostNotFoundException;
    void update(@NotNull Post post) throws PostNotFoundException;

    Optional<Post> findById(@NotNull String id);
    List<Post> findAll();
}
