package com.pierceecom.blog;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.pierceecom.blog.post.BlogPostResource;

@ApplicationPath("/")
public class JAXRSConfiguration  extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(HelloPierceResource.class);
        classes.add(BlogPostResource.class);
        return classes;
    }
}
