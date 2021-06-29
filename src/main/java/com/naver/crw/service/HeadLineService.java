package com.naver.crw.service;

import com.naver.crw.domain.Contents;
import com.naver.crw.domain.Source;

import java.util.List;

public interface HeadLineService {

	int updatePostEndDt(List<String> pks,String articleArea, String siteNm);

    List<Contents> saveAll(List<Contents> contentsList);

    Source findBySiteNmAndArticleCategoryAndUseYn(String sitem, String toString1, String y);

    Contents findFirstBySiteNmAndArticlePkAndDelYn(String toString, String pk_v, String n);

    void updateSuccessComplate(String name, String name1);

	void updateStartCrw(String name, String articleCategory);

	void updateErrorValue(String errorSiteNm, String errorArticleCate, String errorMsg);

}
