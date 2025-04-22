package com.foxapplication.glhmcloud.controller.pub;

import com.foxapplication.glhmcloud.dao.ImageDao;
import com.foxapplication.glhmcloud.entity.ImageEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import com.foxapplication.glhmcloud.param.ImageProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageDao imageDao;

    @Autowired
    public ImageController(ImageDao imageDao) {
        this.imageDao = imageDao;
    }
    @GetMapping(value = "/",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImage(@RequestParam("id") String id) {
        ImageEntity imageEntity = imageDao.findById(UUID.fromString(id)).orElse(null);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        if (imageEntity != null) {
            return new ResponseEntity<>(imageEntity.getImage(),headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(null,headers, HttpStatus.NOT_FOUND);
    }
    @RequestMapping("/upload")
    public BaseResponse<String> uploadImage(@RequestParam("fileField") MultipartFile image) throws IOException {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(image.getOriginalFilename());
        imageEntity.setId(UUID.randomUUID());
        imageEntity.setImage(image.getBytes());
        imageDao.save(imageEntity);
        return BaseResponse.success(imageEntity.getId().toString());
    }
    @RequestMapping("/update")
    public BaseResponse<String> updateImage(@RequestParam("fileField") MultipartFile image, @RequestParam("id") String id) throws IOException {
        ImageEntity imageEntity = imageDao.findById(UUID.fromString(id)).orElse(null);
        if (imageEntity != null) {
            imageEntity.setName(image.getOriginalFilename());
            imageEntity.setImage(image.getBytes());
            imageDao.save(imageEntity);
            return BaseResponse.success(imageEntity.getId().toString());
        }
        return BaseResponse.fail();
    }
    @GetMapping("/delete")
    public BaseResponse<String> deleteImage(@RequestParam("id") String id) {
        imageDao.deleteById(UUID.fromString(id));
        return BaseResponse.success();
    }
    @GetMapping("/list")
    public BaseResponse<List<ImageProjection>> list() {
        return BaseResponse.success(imageDao.findAllIdAndName());
    }
}
