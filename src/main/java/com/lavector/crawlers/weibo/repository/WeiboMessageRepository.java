package com.lavector.crawlers.weibo.repository;


import com.lavector.crawlers.weibo.entity.WeiboMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WeiboMessageRepository extends JpaRepository<WeiboMessage, Long> {

    @Query("select w from WeiboMessage w where w.weiboId = ?1")
    List<WeiboMessage> findByWeiboId(String id);

    @Query(value = "select * from weibo_message w where w.project_id = ?1 order by w.title_time desc limit 1", nativeQuery = true)
    WeiboMessage findMessagebyProjectIdAndOrderByTitleTimeDesc(Long id);
}
