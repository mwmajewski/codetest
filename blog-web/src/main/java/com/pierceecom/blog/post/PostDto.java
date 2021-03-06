package com.pierceecom.blog.post;

import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * When a List<PostDto> is used as the response entity it should be wrapped with GenericEntity
 * example: Response.ok(new GenericEntity<List<PostDto>>(postDtoList){}).build();
 * In such case it's required that all properties that should be (un)marshalled have their setters in place
 */
@XmlRootElement
@XmlType(propOrder = {"id","title","content"})
public class PostDto {

    private String id;
    private String title;
    private String content;

    PostDto() {
    }

    PostDto(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    static PostDto toPostDto(Post post) {
        return new PostDto(post.getId(), post.getTitle(), post.getContent());
    }

    Post toPost() {
        return new Post(getId(), getTitle(), getContent());
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PostDto post = (PostDto) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(title, post.title) &&
                Objects.equals(content, post.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Post{");
        sb.append("id='").append(id).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
