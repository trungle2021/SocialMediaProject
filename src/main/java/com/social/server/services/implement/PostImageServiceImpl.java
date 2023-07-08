package com.social.server.services.implement;

import com.social.server.dtos.PostImageDTO;
import com.social.server.entities.PostImages;
import com.social.server.entities.Posts;
import com.social.server.repositories.Post.PostImageRepository;
import com.social.server.services.PostImageService;
import com.social.server.services.StorageService;
import com.social.server.utils.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PostImageServiceImpl implements PostImageService {
    private final PostImageRepository postImageRepository;
    private final StorageService storageService;
    @Override
    public PostImages getImagesByPostId(String postId) {
        return null;
    }

    @Override
    public PostImages getImage(String postId, String imageId) {
        return null;
    }

    @Override
    public List<PostImageDTO> createImage(List<PostImageDTO> data, String postId) {
        List<PostImages> postImages = new ArrayList<>();
        for (var item : data) {
            PostImages postImage = PostImages.builder()
                    .imageUrl(item.getImageUrl())
                    .postId(postId)
                    .isDeleted(false)
                    .build();
            postImages.add(postImage);
        }
        List<PostImages> listImagesAfterCreate = postImageRepository.saveAll(postImages);
        return listImagesAfterCreate.stream().map(item -> EntityMapper.mapToDto(item,PostImageDTO.class)).toList();
    }

    @Override
    public List<PostImageDTO> updateImage(List<PostImageDTO> imagesToUpdate, String postId) {
        List<PostImages> postsList = imagesToUpdate.stream().map(item -> EntityMapper.mapToEntity(item,PostImages.class)).toList();
        return postImageRepository.saveAll(postsList).stream().map(item->EntityMapper.mapToDto(item,PostImageDTO.class)).toList();
    }

    @Override
    public boolean deleteImage(List<PostImageDTO> imagesToDelete, String id) {
        List<PostImages> postsList = imagesToDelete.stream().map(item -> EntityMapper.mapToEntity(item,PostImages.class)).toList();
         postImageRepository.deleteAll(postsList);
        return true;
    }
}