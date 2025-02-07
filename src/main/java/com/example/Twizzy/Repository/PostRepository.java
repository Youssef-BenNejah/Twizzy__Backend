package com.example.Twizzy.Repository;

import com.example.Twizzy.Entities.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByIsTrending(boolean isTrending);
    List<Post> findByIsSignaled(boolean isSignaled);
}
