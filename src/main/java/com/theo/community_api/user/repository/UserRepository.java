package com.theo.community_api.user.repository;

import com.theo.community_api.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Long, User> userRepository = new HashMap<>();
    private long sequenceIndex = 1L;

    // 새로운 유저 추가 (회원가입 로직에 사용)
    public User save(String email, String password, String nickname, String profileImage){
        Long userId = sequenceIndex++;
        User newUser = new User(userId, email, password, nickname, profileImage);
        userRepository.put(userId,newUser);
        return newUser;
    }

    // userId로 유저 조회
    public Optional<User> findById(Long userId){
        return Optional.ofNullable(userRepository.get(userId));
    }

    // email로 유저 조회
    public Optional<User> findByEmail(String email){
        for(User user : userRepository.values()){
            if(user.getEmail().equals(email)){
                return Optional.of(user); // 절대 null이 아니므로
            }
        }
        return Optional.empty(); // 아예 값이 없음
    }

    // email 중복여부 확인
    public boolean existsByEmail(String email){
        for(User user : userRepository.values()){
            if(user.getEmail().equals(email)){
                return true;
            }
        }
        return false;
    }

    // nickname 중복여부 확인
    public boolean existsByNickname(String nickname){
        for(User user : userRepository.values()){
            if(user.getNickname().equals(nickname)){
                return true;
            }
        }
        return false;
    }

}
