package com.naver.crw.repository;

import com.naver.crw.domain.Source;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SrcRepository extends JpaRepository<Source,String> {

    Source findBySiteNmAndArticleCategoryAndUseYn(String naver, String headline, String y);

    @Transactional
    @Modifying
    @Query(value=" UPDATE potal_source_tb s"
            + " SET s.crw_status_msg = :errorMsg, s.crw_status ='ERROR', up_Dt = now()"
            + " WHERE s.article_category = :errorArticleCate and s.site_nm = :errorSiteNm ", nativeQuery = true)
    void updateErrorValue(@Param("errorSiteNm") String errorSiteNm, @Param("errorArticleCate") String errorArticleCate, @Param("errorMsg") String errorMsg);

    @Transactional
    @Modifying
    @Query(value=" UPDATE potal_source_tb s"
            + " SET s.crw_status ='SUCCESS', s.up_dt = now(), s.crw_status_msg='end crawling success' "
            + " WHERE s.article_category = :cateName and s.site_nm = :siteNm ", nativeQuery = true)
    void updateSuccessComplate(@Param("siteNm") String siteNm, @Param("cateName") String cateName);

    @Transactional
    @Modifying
    @Query(value=" UPDATE potal_source_tb s"
            + " SET s.crw_status ='START', s.up_dt = now(), s.crw_status_msg='start crawling' "
            + " WHERE s.article_category = :articleCategory and s.site_nm = :name ", nativeQuery = true)
	void updateStartCrw(String name, String articleCategory);

}