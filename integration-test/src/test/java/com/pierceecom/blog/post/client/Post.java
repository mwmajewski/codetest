package com.pierceecom.blog.post.client;

import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"id","title","content"})
class Post {

	private String id;
	private String title;
	private String content;

	public Post() {
	}

	public Post(String id, String title, String content) {
		this.id = id;
		this.title = title;
		this.content = content;
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

		Post post = (Post) o;
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
