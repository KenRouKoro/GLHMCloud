package com.foxapplication.glhmcloud.controller.pub;

import com.foxapplication.glhmcloud.dao.ImageDao;
import com.foxapplication.glhmcloud.entity.ImageEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import com.foxapplication.glhmcloud.param.ImageProjection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Tag(name = "图片管理")
@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageDao imageDao;

    @Autowired
    public ImageController(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Operation(summary = "获取图片")
    @GetMapping(value = "/get",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @Transactional
    public ResponseEntity<Object> getImage(@Parameter(description = "图片ID") @RequestParam("id") String id) {
        ImageEntity imageEntity = imageDao.findById(UUID.fromString(id)).orElse(null);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        if (imageEntity != null) {
            return new ResponseEntity<>(imageEntity.getImage(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(BaseResponse.fail("未找到图像"), HttpStatus.OK);
    }

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    @Transactional
    public BaseResponse<String> uploadImage(@Parameter(description = "图片文件") @RequestParam("fileField") MultipartFile image) throws IOException {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(image.getOriginalFilename());
        imageEntity.setImage(image.getBytes());
        imageDao.save(imageEntity);
        return BaseResponse.success(imageEntity.getId().toString());
    }

    @Operation(summary = "更新图片")
    @PostMapping("/update")
    @Transactional
    public BaseResponse<String> updateImage(
            @Parameter(description = "图片文件") @RequestParam("fileField") MultipartFile image,
            @Parameter(description = "图片ID") @RequestParam("id") String id) throws IOException {
        ImageEntity imageEntity = imageDao.findById(UUID.fromString(id)).orElse(null);
        if (imageEntity != null) {
            imageEntity.setName(image.getOriginalFilename());
            imageEntity.setImage(image.getBytes());
            imageDao.save(imageEntity);
            return BaseResponse.success(imageEntity.getId().toString());
        }
        return BaseResponse.fail();
    }

    @Operation(summary = "删除图片")
    @PostMapping("/delete")
    @Transactional
    public BaseResponse<String> deleteImage(@Parameter(description = "图片ID") @RequestParam("id") String id) {
        imageDao.deleteById(UUID.fromString(id));
        return BaseResponse.success();
    }

    @Operation(summary = "获取图片列表")
    @GetMapping("/list")
    @Transactional
    public BaseResponse<List<ImageProjection>> list() {
        return BaseResponse.success(imageDao.findAllIdAndName());
    }
}