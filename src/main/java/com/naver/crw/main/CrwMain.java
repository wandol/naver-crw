package com.naver.crw.main;


import com.naver.crw.domain.Source;
import com.naver.crw.dto.ArticleCate;
import com.naver.crw.dto.ErrorDto;
import com.naver.crw.dto.SiteName;
import com.naver.crw.exception.CrwErrorException;
import com.naver.crw.module.GroovyModule;
import com.naver.crw.service.HeadLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CrwMain {

    @Autowired
    private HeadLineService headLineService;

    @Autowired
    private GroovyModule groovyModule;

    /**
     * headline 수집
     * 1. 헤드라인 영역
     *  -   이미지 영역
     *  -   목록 영역
     * 2. 정치 영역
     *  -   이미지 영역
     *  -   목록 영역
     * 3. 사회 영역
     *  -   이미지 영역
     *  -   목록 영역
     */
    public void startHeadLineCrw() throws CrwErrorException {

        //  TODO 수집원 설정 값 DB에서 가져오기
        Source src = headLineService.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.NEWSHOME.toString(),"Y");

        if (src == null) {
        	ErrorDto dto = ErrorDto.builder()
	            .errorArticleCate(ArticleCate.NEWSHOME.name())
	            .errorMsg("수집원을 찾지 못했습니다.")
	            .errorSiteNm(SiteName.NAVER.name())
	            .detailErrorMsg("수집원을 찾지 못했습니다.")
	            .build();
        	headLineService.updateErrorValue(dto.getErrorSiteNm(),dto.getErrorArticleCate(),dto.getErrorMsg());
        	throw new CrwErrorException(dto.getDetailErrorMsg());
        }
        log.info("NAVER HEADLINE SOURCE INFO :: {}" , src);
        
        //  수집시작 update
        headLineService.updateStartCrw(SiteName.NAVER.name(),src.getArticleCategory());

        //	groovy 활용하여 수집.
        List<Map<String,Object>> saveData = groovyModule.getCrwData(src);
        
        saveData.forEach(data -> log.debug("naver news home headline data list :: {}",data));
        log.info("naver home headline data list size :: {}", saveData.size());
        
        //	중복체크
        //	게시시간 체크 update
        //	save data
        groovyModule.checkAndSaveData(src,saveData);
        
        //  정상완료되면 해당 수집원에 정상 처리 update
        headLineService.updateSuccessComplate(src.getSiteNm(),src.getArticleCategory());

    }

    /**
     *  정치 홈 페이징 부분 수집.
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    public void startPolPageCrw() throws CrwErrorException {

        //  TODO 수집원 설정 값 DB에서 가져오기
        Source src = headLineService.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.POLITICS.toString(), "Y");

        if (src == null) {
        	ErrorDto dto = ErrorDto.builder()
    	            .errorArticleCate(ArticleCate.POLITICS.name())
    	            .errorMsg("수집원을 찾지 못했습니다.")
    	            .errorSiteNm(SiteName.NAVER.name())
    	            .detailErrorMsg("수집원을 찾지 못했습니다.")
    	            .build();
            	headLineService.updateErrorValue(dto.getErrorSiteNm(),dto.getErrorArticleCate(),dto.getErrorMsg());
            	throw new CrwErrorException(dto.getDetailErrorMsg());
        }
        log.info("NAVER POLITICS SOURCE INFO :: {}" , src.toString());

        //  수집시작 update
        headLineService.updateStartCrw(src.getSiteNm(),src.getArticleCategory());

        // 	groovy 활용하여 수집.
        List<Map<String,Object>> saveData = groovyModule.getCrwData(src);
        
        saveData.forEach(data -> log.debug("naver politics home paging url list :: {}",data));
        log.info("naver politics url list size :: {}", saveData.size());
        
        //	중복체크
        //	게시시간 체크 update
        //	save data
        groovyModule.checkAndSaveData(src,saveData);
        
        //  정상완료되면 해당 수집원에 정상 처리 update
        headLineService.updateSuccessComplate(src.getSiteNm(),src.getArticleCategory());

    }

    /**
     *  사회 홈 페이징 부분 수집 .
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    public void startSocPageCrw() throws CrwErrorException {

        //  TODO 수집원 설정 값 DB에서 가져오기
        Source src = headLineService.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.SOCIAL.toString(), "Y");

        if (src == null) {
        	ErrorDto dto = ErrorDto.builder()
    	            .errorArticleCate(ArticleCate.SOCIAL.name())
    	            .errorMsg("수집원을 찾지 못했습니다.")
    	            .errorSiteNm(SiteName.NAVER.name())
    	            .detailErrorMsg("수집원을 찾지 못했습니다.")
    	            .build();
            	headLineService.updateErrorValue(dto.getErrorSiteNm(),dto.getErrorArticleCate(),dto.getErrorMsg());
            	throw new CrwErrorException(dto.getDetailErrorMsg());
        }
        log.info("NAVER SOCIAL SOURCE INFO :: {}" , src.toString());

        //  수집시작 update
        headLineService.updateStartCrw(src.getSiteNm(),src.getArticleCategory());

        // 	groovy 활용하여 수집.
        List<Map<String,Object>> saveData = groovyModule.getCrwData(src);
        
        saveData.forEach(data -> log.debug("naver social home paging url list :: {}",data));
        log.info("naver social url list size :: {}", saveData.size());
        
        //	중복체크
        //	게시시간 체크 update
        //	save data
        groovyModule.checkAndSaveData(src,saveData);
        
        //  정상완료되면 해당 수집원에 정상 처리 update
        headLineService.updateSuccessComplate(src.getSiteNm(),src.getArticleCategory());
    }

    /**
     *  오피니언 -> 속보  페이징 부분 수집 .
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    public void startOpiPageCrw() throws CrwErrorException {

        //  TODO 수집원 설정 값 DB에서 가져오기
        Source src = headLineService.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.OPINION.toString(), "Y");

        if (src == null) {
        	ErrorDto dto = ErrorDto.builder()
    	            .errorArticleCate(ArticleCate.OPINION.name())
    	            .errorMsg("수집원을 찾지 못했습니다.")
    	            .errorSiteNm(SiteName.NAVER.name())
    	            .detailErrorMsg("수집원을 찾지 못했습니다.")
    	            .build();
            	headLineService.updateErrorValue(dto.getErrorSiteNm(),dto.getErrorArticleCate(),dto.getErrorMsg());
            	throw new CrwErrorException(dto.getDetailErrorMsg());
        }
        log.info("NAVER OPINION SOURCE INFO :: {}" , src.toString());

        //  수집시작 update
        headLineService.updateStartCrw(src.getSiteNm(),src.getArticleCategory());

        // 	groovy 활용하여 수집.
        List<Map<String,Object>> saveData = groovyModule.getCrwData(src);
        
        saveData.forEach(data -> log.debug("naver opinion home paging url list :: {}",data));
        log.info("naver opinion url list size :: {}", saveData.size());
        
        //	중복체크
        //	게시시간 체크 update
        //	save data
        groovyModule.checkAndSaveData(src,saveData);
        
        //  정상완료되면 해당 수집원에 정상 처리 update
        headLineService.updateSuccessComplate(src.getSiteNm(),src.getArticleCategory());
    }
}
