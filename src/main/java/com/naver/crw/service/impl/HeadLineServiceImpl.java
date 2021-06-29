package com.naver.crw.service.impl;

import com.naver.crw.domain.Contents;
import com.naver.crw.domain.Source;
import com.naver.crw.repository.ContRepository;
import com.naver.crw.repository.SrcRepository;
import com.naver.crw.service.HeadLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeadLineServiceImpl implements HeadLineService {

	@Autowired
    public SrcRepository lineSrcRepository;

    @Autowired
    public ContRepository contRepository;


    @Override
    public int updatePostEndDt(List<String> pks, String articleArea, String siteNm) {
        return contRepository.updatePostEndDt(pks,articleArea,siteNm);
    }

    @Override
    public List<Contents> saveAll(List<Contents> contentsList) {
        return contRepository.saveAll(contentsList);
    }

    @Override
    public Source findBySiteNmAndArticleCategoryAndUseYn(String sitem, String pk_v, String y) {
        return lineSrcRepository.findBySiteNmAndArticleCategoryAndUseYn(sitem,pk_v,y);
    }

    @Override
    public Contents findFirstBySiteNmAndArticlePkAndDelYn(String sitem, String pk_v, String n) {
        return contRepository.findFirstBySiteNmAndArticlePkAndDelYn(sitem,pk_v,n);
    }

    @Override
    public void updateSuccessComplate(String siteNm, String cateName) {
        lineSrcRepository.updateSuccessComplate(siteNm, cateName);
    }

	@Override
	public void updateStartCrw(String name, String articleCategory) {
		lineSrcRepository.updateStartCrw(name, articleCategory);		
	}

	@Override
	public void updateErrorValue(String errorSiteNm, String errorArticleCate, String errorMsg) {
		lineSrcRepository.updateErrorValue(errorSiteNm, errorArticleCate, errorMsg);
	}

}
