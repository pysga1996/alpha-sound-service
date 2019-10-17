package com.lambda.repository;

import com.lambda.model.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findBySongIdAndUserId(Long songId, Long UserId);
}
