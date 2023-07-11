package com.social.server.services.implement;

import com.social.server.dtos.*;
import com.social.server.entities.Post.Posts;
import com.social.server.exceptions.ResourceNotFoundException;
import com.social.server.exceptions.SocialAppException;
import com.social.server.repositories.Post.PostRepository;
import com.social.server.services.PostImageService;
import com.social.server.services.PostService;
import com.social.server.services.PostTaggedUserService;
import com.social.server.utils.EntityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostImageService postImageService;
    private final PostTaggedUserService postTaggedUserService;




    @Override
    public Page<PostDTO> getPostsByUserIdWithPagination(String userId, int offset, int limit, String field) {
        Pageable pageable;
        if(StringUtils.hasText(field)){
            pageable = PageRequest.of(offset,limit,Sort.by(Sort.Direction.ASC, field));
        }else{
             pageable = PageRequest.of(offset,limit,Sort.by(Sort.Direction.ASC, "posted_at"));
        }
        List<PostDTO> postDTOS = postRepository.findAll(pageable)
                .stream()
                .map(item -> EntityMapper.mapToDto(item,PostDTO.class))
                .toList();
        return new PageImpl<>(postDTOS);
    }

    @Override
    public Page<PostDTO> getPostsByUserIdWithSorting(String userId, String field) {
       List<PostDTO> postDTOS =  postRepository.findAll(Sort.by(Sort.Direction.ASC,field))
               .stream()
               .map(item -> EntityMapper.mapToDto(item,PostDTO.class))
               .toList();
        return new PageImpl<>(postDTOS);
    }

    @Override
    public Posts getPostById(String userId, String postId) {
        Optional.ofNullable(postId).orElseThrow(() -> new SocialAppException(HttpStatus.BAD_REQUEST, "PostId cannot be null"));
        Optional.ofNullable(userId).orElseThrow(() -> new SocialAppException(HttpStatus.BAD_REQUEST, "UserId cannot be null"));
        return postRepository.getPostById(userId,postId).orElseThrow(()->new ResourceNotFoundException("Post not found","id",postId));
    }

    @Override
    @Transactional
    public PostResponseDTO createPost(PostRequestCreateDTO postRequestCreateDTO) {
         //insert a new post
       PostDTO postDTO = insertPost(postRequestCreateDTO.getNewPost());
        // insert images of the post
        List<PostImageDTO> imageURLs = postImageService.createImage(postRequestCreateDTO.getPostImages(), postDTO.getId());
       // insert users tagged in post
        List<PostTaggedUserDTO> taggedUsers = postTaggedUserService.createTaggedUsers(postRequestCreateDTO.getPostTaggedUsers(),postDTO.getId());

        return PostResponseDTO.builder()
                .newPost(postDTO)
                .postImages(imageURLs)
                .postTaggedUsers(taggedUsers)
                .build();

    }

    @Override
    public PostDTO insertPost(PostDTO newPost) {
        Posts post = Posts.builder()
                .id(newPost.getId())
                .content(newPost.getContent())
                .postOwner(newPost.getPostOwner())
                .privacyStatus(newPost.getPrivacyStatus())
                .postedAt(Timestamp.valueOf(LocalDateTime.now()))
                .isDeleted(false)
                .build();
        return EntityMapper.mapToDto(postRepository.save(post),PostDTO.class);
    }

    @Override
    public PostResponseDTO updatePost(PostRequestUpdateDTO updateDTO) {
        String postId = updateDTO.getUpdatePost().getId();
        PostDTO postToUpdate = updateDTO.getUpdatePost();
        List<PostTaggedUserDTO> taggedUsersToUpdate = updateDTO.getPostTaggedUsers();
        List<PostImageDTO> imagesToDelete = updateDTO.getPostImagesToDelete();
        List<PostImageDTO> imagesToUpdate = updateDTO.getPostImagesToUpdate();

        // Check if postId is present
        Optional.ofNullable(postId).orElseThrow(() -> new SocialAppException(HttpStatus.BAD_REQUEST,"Missing PostId to update"));
        PostDTO updatedPostDTO = editPost(postToUpdate);

        //handle images updates
        if(!imagesToDelete.isEmpty() && !imagesToUpdate.isEmpty()){
            // Delete images first, then update (by creating new images)
            postImageService.deleteImage(imagesToDelete,postId);
            List<PostImageDTO> updatedImages =  postImageService.updateImage(imagesToUpdate,postId);
            updateDTO.setPostImagesToUpdate(updatedImages);
        }else if(!imagesToDelete.isEmpty()){
            // Only delete images
            postImageService.deleteImage(imagesToDelete,postId);
        }else{
            // Only update images
            List<PostImageDTO> updatedImages =  postImageService.updateImage(imagesToUpdate,postId);
            updateDTO.setPostImagesToUpdate(updatedImages);
        }

        //update user tagged
        List<PostTaggedUserDTO> updatedTaggedUsers = null;
        if(!taggedUsersToUpdate.isEmpty()){
            updatedTaggedUsers =  postTaggedUserService.updateTaggedUsers(updateDTO.getPostTaggedUsers());
        }
        return PostResponseDTO.builder()
                .newPost(updatedPostDTO)
                .postImages(updateDTO.getPostImagesToUpdate())
                .postTaggedUsers(updatedTaggedUsers)
                .build();
    }

//        String postId = Optional.ofNullable(updatePost.getId()).orElseThrow(() -> new SocialAppException(HttpStatus.BAD_REQUEST, "UserId cannot be null"));
//        String postId = Optional.ofNullable(updatePost.getId()).orElseThrow(() -> new SocialAppException(HttpStatus.BAD_REQUEST, "UserId cannot be null"));

    @Override
    public PostDTO editPost(PostDTO updatePost) {
        String userId = updatePost.getPostOwner();
        String postId = updatePost.getId();

        Posts post = getPostById(userId, postId);
        String content = updatePost.getContent() == null ? post.getContent() : updatePost.getContent();
        String privacyStatus =  updatePost.getPrivacyStatus() == null ? post.getPrivacyStatus() : updatePost.getPrivacyStatus();
        Boolean isDeleted =  updatePost.getIsDeleted() == null ? post.getIsDeleted() : updatePost.getIsDeleted();
        post.setContent(content);
        post.setPrivacyStatus(privacyStatus);
        post.setIsDeleted(isDeleted);
       return EntityMapper.mapToDto(postRepository.save(post),PostDTO.class);
    }


    @Override
    public boolean deletePost(String postId) {
        postRepository.deleteById(postId);
        return true;
    }
}
