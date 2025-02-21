package com.example.demo;

import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.mockito.stubbing.Stubber;

@UtilityClass
public class TestUtils {

  public static Stubber doThrow(Class<? extends Throwable> toBeThrown, int times) {
    final Stubber stubber = Mockito.doThrow(toBeThrown);
    for (int i = 0; i < times - 1; i++) {
      stubber.doThrow(toBeThrown);
    }
    return stubber;
  }
}
