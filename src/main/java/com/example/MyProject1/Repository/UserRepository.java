package com.example.MyProject1.Repository;

import com.example.MyProject1.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;


public interface UserRepository extends MongoRepository<User, Object> {

          User findByUserName (String username);
          User deleteByUserName(String username);
}
