package com.naver.crw.module;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naver.crw.domain.Contents;
import com.naver.crw.domain.Source;
import com.naver.crw.dto.ErrorDto;
import com.naver.crw.exception.CrwErrorException;
import com.naver.crw.service.HeadLineService;

import groovy.util.GroovyScriptEngine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GroovyModule {

	@Value("${groovy.path}")
    private String groovyPath;
	
	@Value("${chrome.driver.path}")
    private String chromeDirverPath;
	
	@Autowired
    private HeadLineService headLineService;
	

	/**
	 *  groovy 와 연동하여 수집한 결과를 가져온다.
	 *  
	 *  link 수집과 contents 수집 동시.
	 *  
	 * @param src
	 * @return
	 * @throws CrwErrorException 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getCrwData(Source src) throws CrwErrorException {
		
		List<Map<String,Object>> resultList = null;
		
		try {
			
			// groovy 넘기기위해 map 형식으로 변경.
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String,Object> paramData = objectMapper.convertValue(src, HashMap.class);
			
			// chromedriver 경로 추가.
			// groovy에서 사용.
			log.debug("chromeDirverPath :: {}", chromeDirverPath);
			paramData.put("chromeDriverPath",chromeDirverPath);
			
			GroovyScriptEngine engine = new GroovyScriptEngine(groovyPath);
			
			Object instance = engine.loadScriptByName(src.getGroovyFileNm()).newInstance();
			
			resultList = (List<Map<String,Object>>) InvokerHelper.invokeMethod(instance, "CrwMain", paramData);
			 
		} catch (Exception e) {
			ErrorDto dto = ErrorDto.builder()
		            .errorArticleCate(src.getArticleCategory())
		            .errorMsg(String.format("%s 사이트 %s 수집원 수집시 에러가 발생했습니다. %s을 체크하세요.",src.getSiteNm(),src.getArticleCategory() ,src.getGroovyFileNm()))
		            .errorSiteNm(src.getSiteNm())
		            .detailErrorMsg(e.getMessage())
		            .build();
			headLineService.updateErrorValue(dto.getErrorSiteNm(),dto.getErrorArticleCate(),dto.getErrorMsg());
        	throw new CrwErrorException(dto.getDetailErrorMsg());
		}
        
		return resultList;
	}

	/**
	 * 	실제 수집된 리스트를 디비에 저장.
	 * 	중복체크
	 * 	게시시간 체크 ( headline )
	 * @param src 
	 * 	@param saveData
	 * @throws CrwErrorException 
	 */
	public void checkAndSaveData(Source src, List<Map<String, Object>> saveData) throws CrwErrorException {
		
		List<String> pks = new ArrayList<>();
		List<Contents> result = new ArrayList<>();
		
		try {
			for (Map<String, Object> map : saveData) {			
				
				String pk_v = map.get("articlePk").toString();
				//	pk 리스트 저장. 게 	시시간 update에 활
				if("NEWSHOME".equals(src.getArticleCategory())) {
					pks.add(pk_v);
				}
				
				//  중복체크 및 게시 시간을 구하기 위해. 해당 pk로 먼저 수집원 데이터 체크.
				Contents contents = null;
				if("NEWSHOME".equals(src.getArticleCategory())) {
					contents = headLineService.findFirstBySiteNmAndArticlePkAndDelYn(src.getSiteNm(), pk_v,"N");
				}
				
				if(contents == null) {
					
					Contents cont = Contents.builder()
	                        .articleCategory(map.get("articleCategory").toString())
	                        .articleContents(map.get("articleContents").toString())
	                        .articleImgCaption(map.get("articleImgCaption").toString())
	                        .articleMediaNm(map.get("articleMediaNm").toString())
	                        .articlePk(pk_v)
	                        .articleTitle(map.get("articleTitle").toString())
	                        .articleUrl(map.get("articleUrl").toString())
	                        .articleWriteDt((LocalDateTime) map.get("articleWriteDt"))
	                        .articleWriter(map.get("articleWriter").toString())
	                        .siteNm(map.get("siteNm").toString())
	                        .srcType(map.get("srcType").toString())
	                        .delYn(map.get("delYn").toString())
	                        .articlePostStartDt((LocalDateTime) map.get("articlePostStartDt"))
	                        .articleCrwDt((LocalDateTime) map.get("articleCrwDt"))
	                        .upDt((LocalDateTime) map.get("upDt"))
	                        .build();

	                result.add(cont);
				}
			}
	      
			//  상세 기사 데이터 DB 저장.
			if(result.size() > 0){
				List<Contents> saveCont = headLineService.saveAll(result);
				log.info("save Contents cnt :: {}" , saveCont.size());
			}

			//  todo 해당 기사 게시 체크 ( 게시 되어 있는지 내려갔는지 체크 하는 로직. )
			//  한 헤드라인 수집 사이클에서 가져온 pks 들을 가지고  기존 contents_tb 에서  not in  pks   ..  and post_end_dt 가 null 인 데이터에
			//  post_end_dt 칼럼에 현재시각을 update 한다.
			if(pks.size() > 0 ){
				int updatePostEndDt  = headLineService.updatePostEndDt(pks,src.getArticleCategory(),src.getSiteNm());
	          	log.info("update count end date :: {}" , updatePostEndDt);
			}
		} catch (Exception e) {
			ErrorDto dto = ErrorDto.builder()
		            .errorArticleCate(src.getArticleCategory())
		            .errorMsg(String.format("%s 사이트 %s 수집데이터 저장시 에러가 발생했습니다. %s을 체크하세요.",src.getSiteNm(),src.getArticleCategory() ,src.getGroovyFileNm()))
		            .errorSiteNm(src.getSiteNm())
		            .detailErrorMsg(e.getMessage())
		            .build();
			headLineService.updateErrorValue(dto.getErrorSiteNm(),dto.getErrorArticleCate(),dto.getErrorMsg());
        	throw new CrwErrorException(dto.getDetailErrorMsg());
		}
	}

}
