import com.naver.crw.exception.CrwErrorException;
import com.naver.crw.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

class NaverOpinionClass {

    private static final Logger log = LoggerFactory.getLogger(NaverOpinionClass.class)

    public List<Map<String,Object>> CrwMain(Map<String,Object> src) throws CrwErrorException, InterruptedException{
        
        List<String> linkresult = new ArrayList<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> resultMap = null;
        Document doc;

        System.setProperty("webdriver.chrome.driver", src.get("chromeDriverPath") );
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        
        WebDriver wb = null;
        
        //  어제 날짜 'yyyyMMdd' 형식
        String yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String addDateParam  = "&date=" + yesterday;
        String crwUrl = src.get("startUrl");

        try {
            log.info("=================================================== groovy in =============================================================");
            log.info("groovy 수집 url :: {}" , src.get("startUrl"));
            
            //  today에 해당 되는 것만 수집하기 위한 flag
            boolean stopFlag = true;
            int page = 1;

            while(stopFlag){
                //  수집 url make
                String startUrl = crwUrl + page + addDateParam;

                wb = new ChromeDriver(options);
                wb.navigate().to(startUrl);

                List<WebElement> pageLinkList = wb.findElements(By.xpath(src.get("catePagingListXpath")));

                //  pageLinkList  사이즈가 20 이 아니면 수집 종료
                if(pageLinkList.size() != 20) stopFlag = false;
                //  해당 url에 목록이 없으면 수집종료.
                if(pageLinkList.size() == 0) break;

                for (WebElement webElement : pageLinkList) {
                    linkresult.add(webElement.getAttribute("href"));
                }

                //  page 증가.
                page++;
                //  2초간 페이징 이동 간격
                Thread.sleep(2000);

                //  브라우져 종료.
                wb.quit();
            }
            
            if(wb != null)	wb.quit()

            log.info("=============================== headline done ===============================");
            log.info("=============================== contents start ===============================");
			//  구해진 link 로 jsoup을 이용 하여 컨텐츠 수집.
            for (String url : linkresult) {
                doc = Jsoup.connect(url).get();

                //  기사 작성일 parse
                //  이와 같은 형식. :: 2020.10.26. 오전 9:35
                //  디폴트 값을 설정해놓음. properties
                //  기사 작성일 간혹 수정일이 포함되어 옴. ( 이에 배열 첫번째 요소로 parse )
                DateFormat dateParser = new SimpleDateFormat("yyyy.MM.dd. a KK:mm", Locale.ENGLISH);
                Elements writeDtTag = doc.getElementsByClass(src.get("articleWriteDtXpth"));
                String parseWDt = LocalDateTime.now().toString();
                if(writeDtTag.size() > 0){
                    String writeDt = writeDtTag.get(0).text();
                    parseWDt = writeDt.contains("오전") ? writeDt.replace("오전","AM") : writeDt.replace("오후","PM") ;
                }

                //  PK 구하기
                String pk_v = new CommonUtil().getEncMD5(url + doc.getElementsByAttributeValue("property", src.get("articleTitleXpth")).attr("content"));

                resultMap = new HashMap<String,Object>();

                resultMap.put("articleCategory",doc.getElementsByAttributeValue("property",src.get("articleCateXpth")).attr("content"));
                resultMap.put("articleContents",doc.getElementsByClass(src.get("articleContXpth")).text());
                resultMap.put("articleImgCaption",doc.getElementsByClass(src.get("articleImgContXpth")).stream().map({v -> v.text()}).collect(Collectors.joining("|")));
                resultMap.put("articleMediaNm",doc.getElementsByAttributeValue("property",src.get("articleMediaNmXpth")).attr("content"));
                resultMap.put("articlePk",pk_v);
                resultMap.put("articleTitle",doc.getElementsByAttributeValue("property",src.get("articleTitleXpth")).attr("content"));
                resultMap.put("articleUrl",url);
                resultMap.put("articleWriteDt",LocalDateTime.ofInstant(dateParser.parse(parseWDt).toInstant(), ZoneId.of("Asia/Seoul")));
                resultMap.put("articleWriter",doc.getElementsByAttributeValue("property", src.get("articleWriterXpth")).attr("content"));
                resultMap.put("siteNm",src.get("siteNm"));
                resultMap.put("srcType",src.get("articleCategory"));
                resultMap.put("delYn","N");
                resultMap.put("articlePostStartDt",LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                resultMap.put("articleCrwDt",LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                resultMap.put("upDt",LocalDateTime.now(ZoneId.of("Asia/Seoul")));

                resultList.add(resultMap);                
            }
		    log.info("=============================== contents end ===============================");	
        }finally {
            wb.quit();
            log.info("=================================================== groovy out =============================================================");
        }

        return resultList;
    }   
}
