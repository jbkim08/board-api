package com.example.board.mapper;

import com.example.board.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);
    // 리프레쉬 토큰으로 유저 찾기
    User findByRefreshToken(@Param("refreshToken") String refreshToken);

    boolean existsByUsername(@Param("username") String username);

    int insert(User user);
    // 리프레쉬 토큰 업데이트
    void updateRefreshToken(@Param("username") String username, @Param("refreshToken") String refreshToken);
}
