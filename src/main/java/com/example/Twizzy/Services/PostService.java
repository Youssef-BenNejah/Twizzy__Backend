package com.example.Twizzy.Services;

import com.example.Twizzy.Entities.Post;
import com.example.Twizzy.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Supposons que l'ID utilisateur est stocké ici
        }
        return null;
    }

    public Post createPost(String content) {
        String userId = getCurrentUserId();
        if (userId == null) throw new IllegalArgumentException("Utilisateur non authentifié");
        Post post = new Post(content, userId);
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    public Post updatePost(String id, String content) {
        String userId = getCurrentUserId();
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));

        if (!post.getAuthorId().equals(userId)) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce post");
        }

        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void deletePost(String id) {
        String userId = getCurrentUserId();
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));

        if (!post.getAuthorId().equals(userId)) {
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer ce post");
        }

        postRepository.deleteById(id);
    }

    public Post likePost(String id) {
        String userId = getCurrentUserId();
        if (userId == null) throw new IllegalArgumentException("Utilisateur non authentifié");

        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));

        if (post.getLikedBy().contains(userId)) {
            post.getLikedBy().remove(userId);
            post.setLikes(post.getLikes() - 1);
        } else {
            post.getLikedBy().add(userId);
            post.setLikes(post.getLikes() + 1);

            if (post.getDislikedBy().contains(userId)) {
                post.getDislikedBy().remove(userId);
                post.setDislikes(post.getDislikes() - 1);
            }
        }

        return postRepository.save(post);
    }

    public Post dislikePost(String id) {
        String userId = getCurrentUserId();
        if (userId == null) throw new IllegalArgumentException("Utilisateur non authentifié");

        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));

        if (post.getDislikedBy().contains(userId)) {
            post.getDislikedBy().remove(userId);
            post.setDislikes(post.getDislikes() - 1);
        } else {
            post.getDislikedBy().add(userId);
            post.setDislikes(post.getDislikes() + 1);

            if (post.getLikedBy().contains(userId)) {
                post.getLikedBy().remove(userId);
                post.setLikes(post.getLikes() - 1);
            }
        }

        return postRepository.save(post);
    }
}
