package com.example.sunnymarketbackend.dao;

import com.example.sunnymarketbackend.entity.OpenAi;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OpenAiDao {

    void createAiRepondUserQusent(OpenAi openAi);

}
