package com.pierceecom.blog.post;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequestScoped
@Path("posts")
public class BlogPostResource {

    public static final String POST_NOT_FOUND = "Post not found";
    public static final String INVALID_INPUT = "Invalid input";

    private PostDao dao;

    BlogPostResource(){}

    @Inject
    public BlogPostResource(PostDao dao) {
        this.dao = dao;
    }

    @Path("/{postId}")
    @GET
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public Response findPost(@PathParam("postId") String postId) {
        Optional<Post> optionalResult = dao.findById(postId);
        return optionalResult.map(post -> buildOkResponse(post)).orElseGet(() -> buildNotFoundResponse());
    }

    @GET
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public Response getAllPosts() {
        List<Post> posts = dao.findAll();
        return buildOkResponse(new GenericEntity<List<Post>>(posts){});
    }

    private Response buildOkResponse(Object entity) {
        return Response.ok(entity).build();
    }

    @Path("/{postId}")
    @DELETE
    @Produces({APPLICATION_JSON})
    public Response deletePost(@PathParam("postId") String postId) {
        try {
            dao.remove(postId);
            return Response.accepted().build();
        } catch (PostNotFoundException e) {
            return buildNotFoundResponse();
        }
    }

    @POST
    @Consumes({APPLICATION_JSON, APPLICATION_XML})
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Response addPost(Post post, @Context UriInfo uriInfo) {

        if (!isValidPostFromRequest(post)){
            return buildMethodNotAllowedResponse();
        }

        try {
            dao.add(post);
            return buildCreatedResponse(post, uriInfo);
        } catch (PostAlreadyExistsException e) {
            return buildMethodNotAllowedResponse();
        }
    }

    @PUT
    @Consumes({APPLICATION_JSON, APPLICATION_XML})
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Response updatePost(Post post, @Context UriInfo uriInfo) {

        if (!isValidPostFromRequest(post)){
            return buildMethodNotAllowedResponse();
        }

        try {
            dao.update(post);
            return buildCreatedResponse(post, uriInfo);
        } catch (PostNotFoundException e) {
            return buildNotFoundResponse();
        }
    }

    private boolean isValidPostFromRequest(Post post) {
        return Objects.nonNull(post) &&
                isNotBlank(post.getContent()) &&
                isNotBlank(post.getTitle()) &&
                isNotBlank(post.getId());
    }

    private Response buildCreatedResponse(Post post, UriInfo uriInfo) {
        return Response.created(getSinglePostLocation(post, uriInfo))
                        .entity(post)
                        .build();
    }

    private URI getSinglePostLocation(Post post, UriInfo uriInfo) {
        String location = new StringBuilder().append(uriInfo.getPath())
                                            .append("/")
                                            .append(post.getId())
                                            .toString();
        return URI.create(location);
    }

    private Response buildNotFoundResponse() {
        return Response.status(NOT_FOUND)
                        .entity(new ResponseInfo(POST_NOT_FOUND))
                        .build();
    }

    private Response buildMethodNotAllowedResponse() {
        return Response.status(METHOD_NOT_ALLOWED)
                        .entity(new ResponseInfo(INVALID_INPUT))
                        .build();
    }

}
