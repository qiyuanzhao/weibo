package com.lavector.crawlers.weibo.repository;


import com.lavector.crawlers.weibo.entity.CrawlerTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<CrawlerTask, String> {

//    @Override
//    @Query("select * from crawler_task  where id = ?1")
//    Optional<CrawlerTask> findById(String id);

    @Override
    @Query(value = "select * from crawler_task where status = 'CREATED'", nativeQuery = true)
    Page<CrawlerTask> findAll(Pageable var1);


}
