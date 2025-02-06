package com.example.Twizzy.Services;

import com.example.Twizzy.Entities.Comment;
import com.example.Twizzy.Entities.Post;

import com.example.Twizzy.Repository.CommentRepository;
import com.example.Twizzy.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    // Create a comment on a post
    public Comment createComment(String postId, String content, String authorId) {
        Optional<Post> postOpt = postRepository.findById(postId);

        if (postOpt.isPresent()) {
            Post post = postOpt.get();

            Comment comment = new Comment(content, authorId, post);
            Comment savedComment = commentRepository.save(comment);

            post.getComments().add(savedComment);
            postRepository.save(post);

            return savedComment;
        }

        throw new RuntimeException("Post not found");
    }


    // Get comments for a post
    public Set<Comment> getCommentsForPost(String postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            // Use the latest data from the database for comments
            return new HashSet<>(commentRepository.findByPostId(postId)); // Ensure it fetches fresh data
        }
        return null; // Post not found
    }


    // Update a comment
    public Comment updateComment(String commentId, String content) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setContent(content); // Update the content
            comment.setUpdatedAt(LocalDateTime.now()); // Update the timestamp
            return commentRepository.save(comment);
        }
        return null; // Comment not found
    }


    // Delete a comment
    public void deleteComment(String commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);

        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            Post post = comment.getPost(); // Get the post to which the comment is linked

            post.getComments().remove(comment); // Remove comment from the post's list
            postRepository.save(post); // Save the post to update the comment list

            commentRepository.deleteById(commentId); // Delete the comment
        }
    }

}
