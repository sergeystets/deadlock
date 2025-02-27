package com.example.demo.service;

import com.example.demo.dto.ImageDto;
import com.example.demo.entity.ImageEntity;
import com.example.demo.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository imageRepository;

  @Transactional
  public void save(final ImageDto image) {
    log.info("Saving image... {}", image);
    final ImageEntity imageEntity = new ImageEntity();
    imageEntity.setName(image.name());
    imageEntity.setStyleId(image.styleId());
    imageRepository.insert(imageEntity);
    log.info("Image successfully saved {}", imageEntity);
  }
}
