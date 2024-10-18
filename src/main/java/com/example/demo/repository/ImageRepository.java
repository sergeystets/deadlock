package com.example.demo.repository;

import com.example.demo.entity.ImageEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ImageRepository {

  @Insert("INSERT INTO image(name, style_id) VALUES(#{name}, #{styleId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(final ImageEntity imageEntity);

}
