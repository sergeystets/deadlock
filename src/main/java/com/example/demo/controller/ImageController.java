package com.example.demo.controller;

import com.example.demo.dto.ImageDto;
import com.example.demo.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/image")
public class ImageController {

  private final ImageService imageService;

  @PostMapping
  public void upload(@RequestParam(value = "style_id") final int styleId,
                     @RequestParam(value = "file") final MultipartFile imageFile) {
    final ImageDto image = new ImageDto(imageFile.getOriginalFilename(), styleId);
    imageService.save(image);
  }
}
