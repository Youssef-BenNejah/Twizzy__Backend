package com.example.Twizzy.Services;

import com.example.Twizzy.Entities.Post;
import com.example.Twizzy.Entities.User;
import com.example.Twizzy.Repository.PostRepository;
import com.example.Twizzy.Repository.UserRepository;
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
    @Autowired
    private UserRepository userRepository;

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();

            // 🔥 Fetch user ID from database using username
            return userRepository.findByUsername(username)
                    .map(User::getId) // Assuming User entity has an getId() method
                    .orElse(null);
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

        post.setTrending(post.getLikes() > 3);

        return postRepository.save(post);
    }
    public List<Post> getTrendingPosts() {
        return postRepository.findByIsTrending(true);
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
    public Post signalPost(String id) {
        String userId = getCurrentUserId();
        if (userId == null) throw new IllegalArgumentException("Utilisateur non authentifié");

        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));

        // Check if the user has already signaled the post
        if (post.getSignaledBy().contains(userId)) {
            throw new IllegalArgumentException("Vous avez déjà signalé ce post");
        }

        // Add the user to the signaledBy set
        post.getSignaledBy().add(userId);

        // If the post gets 3 signals, mark it as signaled
        if (post.getSignaledBy().size() >= 3) {
            post.setSignaled(true);
        }

        return postRepository.save(post);
    }
    public List<Post> getSignaledPosts() {
        return postRepository.findByIsSignaled(true);
    }

}
