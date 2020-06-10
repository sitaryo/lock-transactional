package com.sendroids.tech.locktransactional.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.Optional;

public interface CoderRepository extends JpaRepository<Coder, Long> {

    /**
     * 基于for update 的悲观锁，锁行
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    not work in mysql
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    Optional<Coder> findById(long id);

    /**
     * 基于 version 的乐观锁，锁行
     */
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Coder> findByIdAndAge(long id, int age);

    /**
     * 基于for update 的悲观锁，锁表
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Collection<Coder> findBySex(int sex);
}
