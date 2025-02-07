package com.example.Twizzy.Controllers;

import com.example.Twizzy.Entities.Comment;
import com.example.Twizzy.Services.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/comments")
@Tag(name = "Comments Management")

public class CommentController {

    @Autowired
    private CommentService commentService;

    // Create a comment on a post

    @PostMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable String postId,
            @RequestBody Map<String, String> requestBody) {

        String content = requestBody.get("content");
        String authorId = requestBody.get("authorId");


        if (content == null || content.isBlank() || authorId == null || authorId.isBlank()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Content and authorId are required!");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Comment comment = commentService.createComment(postId, content, authorId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment successfully created!");
        response.put("comment", comment);

        return ResponseEntity.ok(response);
    }


    // Get all comments for a post
    @GetMapping("/post/{postId}")
    public ResponseEntity<Set<Comment>> getCommentsForPost(@PathVariable String postId) {
        Set<Comment> comments = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    // Update a comment
    @PutMapping("/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable String commentId,
            @RequestBody Map<String, String> requestBody) {

        String newContent = requestBody.get("content");

        if (newContent == null || newContent.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Content cannot be empty!"));
        }

        Comment updatedComment = commentService.updateComment(commentId, newContent);

        if (updatedComment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "message", "Comment updated successfully!",
                "comment", updatedComment
        ));
    }


    // Delete a comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
