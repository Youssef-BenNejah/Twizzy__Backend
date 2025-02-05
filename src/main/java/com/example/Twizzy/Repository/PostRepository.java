package com.example.Twizzy.Repository;

import com.example.Twizzy.Entities.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
    // Custom queries can go here, e.g., finding posts by user
}
