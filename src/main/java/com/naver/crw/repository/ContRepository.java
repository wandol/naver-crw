package com.naver.crw.repository;

import com.naver.crw.domain.Contents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * User: wandol<br/>
 * Date: 2020/10/27<br/>
 * Time: 9:21 오후<br/>
 * Desc:
 */
@Repository
public interface ContRepository extends JpaRepository<Contents,String> {

	@Transactional
    @Modifying
    @Query(value=" UPDATE potal_contents_tb "
            + " SET article_post_end_dt =  now(), up_Dt = now()"
            + " WHERE article_pk NOT IN (:pks) and article_post_end_dt is null and  src_type = :articleArea and site_nm = :siteNm ", nativeQuery = true)
    int updatePostEndDt(@Param("pks") List<String> pks, @Param("articleArea") String articleArea, @Param("siteNm") String siteNm);

    Contents findFirstByArticlePkAndDelYn(String pk_v, String n);

    Contents findFirstBySiteNmAndArticlePkAndDelYn(String sitem, String pk_v, String n);
}
