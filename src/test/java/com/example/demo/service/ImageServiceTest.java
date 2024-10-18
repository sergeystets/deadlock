package com.example.demo.service;

import com.example.demo.DemoApplication;
import com.example.demo.dto.ImageDto;
import com.example.demo.entity.ImageEntity;
import com.example.demo.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.PessimisticLockingFailureException;

import static com.example.demo.TestUtils.doThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class) // we actually do not need DB layer here
@SpringBootTest(classes = DemoApplication.class)
class ImageServiceTest {

  @Autowired
  private ImageService imageService;
  @MockBean
  private ImageRepository imageRepository;

  @Test
  void save_whenDeadlock_shouldSuccessfullyRetry() {
    // given
    doThrow(PessimisticLockingFailureException.class, 3) // emulate deadlock for each insert operation for 3 times
        .doNothing() // then allow the insert
        .when(imageRepository).insert(any());

    // when we save the image
    imageService.save(image());

    // then we expect 3 unlucky and a single lucky calls (4 calls in total) to a repository
    verify(imageRepository, times(4)).insert(imageEntity());
  }

  @Test
  void save_whenDeadlockAndAllTriesExhausted_shouldGiveUpTryingAndRethrowException() {
    // given
    doThrow(PessimisticLockingFailureException.class, 4)    // 1, 2, 3, 4 unlucky calls
        .doThrow(PessimisticLockingFailureException.class) // 5. unlucky (last re-try will be here)
        .doThrow(PessimisticLockingFailureException.class) // 6. unlucky
        .doNothing() // 7. lucky (because we exhausted all 5 attempts, this lucky call does not really matter)
        .when(imageRepository).insert(any());

    // when we call 'save' we expect PessimisticLockingFailureException to be re-thrown, since we exhausted all tries
    assertThrows(PessimisticLockingFailureException.class, () -> imageService.save(image()));

    // then we also expect 5 unlucky re-tries
    verify(imageRepository, times(5)).insert(imageEntity());
  }

  @Test
  void save_willNotRetryNonDeadlockRelatedException() {
    // given
    Mockito.doThrow(RuntimeException.class) // 1. unlucky, but we throw NON-deadlock related exception
        .doNothing() // 2. lucky
        .when(imageRepository).insert(any());

    // when we call 'save' we expect RuntimeException to be re-thrown immediately and never re-tried
    assertThrows(RuntimeException.class, () -> imageService.save(image()));

    // then we also expect a single unlucky call (without re-tries)
    verify(imageRepository, times(1)).insert(imageEntity());
  }

  private static ImageEntity imageEntity() {
    final ImageEntity imageEntity = new ImageEntity();
    imageEntity.setName("test-image");
    imageEntity.setStyleId(1);
    return imageEntity;
  }

  private static ImageDto image() {
    return new ImageDto("test-image", 1);
  }
}

