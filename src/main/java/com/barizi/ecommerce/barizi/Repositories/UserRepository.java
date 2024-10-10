package com.barizi.ecommerce.barizi.Repositories;

import com.barizi.ecommerce.barizi.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(nativeQuery = true, value = "select * from users where user_name = :username and deleted = false")
    Optional<UserEntity> findByUsername(String username);

    @Query(nativeQuery = true, value = "select * from users where email = :email and deleted = false ")
    Optional<UserEntity> findByEmail(String email);


    @Query(nativeQuery = true, value = "select email from users where user_name = :username and deleted = false")
    String findEmail(String username);
}
