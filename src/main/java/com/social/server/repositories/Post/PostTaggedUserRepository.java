package com.social.server.repositories.Post;

import com.social.server.entities.Post.PostTaggedUsers;
import com.social.server.entities.User.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostTaggedUserRepository extends JpaRepository<PostTaggedUsers, String> {
@Query("SELECT u FROM Users u JOIN PostTaggedUsers p ON u.id = p.taggedUserId WHERE p.postId = :postId")
    List<Users> getUsersByPostId(String postId);
}
