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

import static com.pierceecom.blog.post.PostDto.toPostDto;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RequestScoped
@Path("posts")
public class BlogPostResource {

    private static final String POST_NOT_FOUND = "Post not found";
    private static final String INVALID_INPUT = "Invalid input";

    private PostDao dao;

    BlogPostResource(){}

    @Inject
    BlogPostResource(PostDao dao) {
        this.dao = dao;
    }

    @Path("/{postId}")
    @GET
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public Response findPost(@PathParam("postId") String postId) {
        Optional<Post> optionalResult = dao.findById(postId);
        return optionalResult.map(post -> Response.ok(toPostDto(post)).build())
                             .orElseGet(this::buildNotFoundResponse);
    }

    @GET
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public Response getAllPosts() {
        List<PostDto> postDtoList = dao.findAll().stream()
                                                 .map(PostDto::toPostDto)
                                                 .collect(toList());
        return Response.ok(new GenericEntity<List<PostDto>>(postDtoList){}).build();
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
    public Response addPost(PostDto postDto, @Context UriInfo uriInfo) {

        if (isInvalidPostFromRequest(postDto)){
            return buildMethodNotAllowedResponse();
        }

        try {
            dao.add(postDto.toPost());
            return buildCreatedResponse(postDto, uriInfo);
        } catch (PostAlreadyExistsException e) {
            return buildMethodNotAllowedResponse();
        }
    }

    @PUT
    @Consumes({APPLICATION_JSON, APPLICATION_XML})
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Response updatePost(PostDto postDto, @Context UriInfo uriInfo) {

        if (isInvalidPostFromRequest(postDto)){
            return buildMethodNotAllowedResponse();
        }

        try {
            dao.update(postDto.toPost());
            return buildCreatedResponse(postDto, uriInfo);
        } catch (PostNotFoundException e) {
            return buildNotFoundResponse();
        }
    }

    private boolean isInvalidPostFromRequest(PostDto post) {
        return Objects.isNull(post) ||
                isBlank(post.getContent()) ||
                isBlank(post.getTitle()) ||
                isBlank(post.getId());
    }

    private Response buildCreatedResponse(PostDto postDto, UriInfo uriInfo) {
        return Response.created(getSinglePostLocation(postDto, uriInfo))
                        .entity(postDto)
                        .build();
    }

    private URI getSinglePostLocation(PostDto postDto, UriInfo uriInfo) {
        String location = new StringBuilder().append(uriInfo.getPath())
                                            .append("/")
                                            .append(postDto.getId())
                                            .toString();
        return URI.create(location);
    }

    private Response buildNotFoundResponse() {
        return Response.status(NOT_FOUND)
                        .entity(new ResponseInfoDto(POST_NOT_FOUND))
                        .build();
    }

    /**
     * TODO consider switching from METHOD_NOT_ALLOWED to BAD_REQUEST as it seems more suitable here
     */
    private Response buildMethodNotAllowedResponse() {
        return Response.status(METHOD_NOT_ALLOWED)
                        .entity(new ResponseInfoDto(INVALID_INPUT))
                        .build();
    }

}
