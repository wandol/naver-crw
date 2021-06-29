
import com.naver.crw.dto.ArticleArea;
import com.naver.crw.dto.ArticleCate;
import com.naver.crw.dto.ErrorDto;
import com.naver.crw.dto.SiteName;
import com.naver.crw.exception.CrwErrorException;
import com.naver.crw.service.HeadLineService;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class NaverHeadLineGroovyClass {

    private static final Logger log = LoggerFactory.getLogger(NaverHeadLineGroovyClass.class)

    public List<Map<String,Object>> CrwMain(Map<String,Object> src) {
        
        List<String> linkresult = new ArrayList<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> resultMap = null;
        Document doc;

        //    크롬 드라이버 설정.
        System.setProperty('webdriver.chrome.driver', src.get("chromeDriverPath"))
        // 크롬 브라우져 열기.
        ChromeOptions options = new ChromeOptions()
        options.addArguments('headless')

        WebDriver wb = new ChromeDriver(options)
        wb.navigate().to(src.get("startUrl"))

        try {
            log.info("=================================================== groovy in =============================================================");
            log.info("groovy 수집 url :: {}" , src.get("startUrl"));

            //	네이버 -> 뉴스 홈 -> 헤드라인 뉴스 이미지. -> 2개 인것 히든꺼까지 수집.
            List<WebElement> headLineImgLink = wb.findElements(By.xpath(src.get("homeHeadlineImgLinkXpth")));
            for (WebElement v : headLineImgLink) {
                linkresult.add(v.getAttribute("href"));
            }

            //	네이버 -> 뉴스 홈 -> 헤드라인 뉴스 -> 이미지 이외의 기사들 5개 수집.
            List<WebElement> headLineNomalLink = wb.findElements(By.xpath(src.get("homeHeadlineListLinkXpth")));
            for (WebElement v : headLineNomalLink) {
                linkresult.add(v.getAttribute("href"));
            }

            //	네이버 -> 뉴스 홈 -> 정치 뉴스 이미지 수집.
            List<WebElement> politicImgLink = wb.findElements(By.xpath(src.get("homePoliticsImgLinkXpth")));
            for (WebElement v : politicImgLink) {
                linkresult.add(v.getAttribute("href"));
            }

            // 네이버 -> 뉴스 홈 -> 정치 뉴스 -> 이미지 이외의 기사들 5개 수집.
            List<WebElement> politicNomalLink = wb.findElements(By.xpath(src.get("homePoliticsListLinkXpth")));
            for (WebElement v : politicNomalLink) {
                linkresult.add(v.getAttribute("href"));
            }

            // 네이버 -> 정치 홈 -> 사회 뉴스 이미지 수집.
            List<WebElement> societyImgLink = wb.findElements(By.xpath(src.get("homeSocialImgLinkXpth")));
            for (WebElement v : societyImgLink) {
                linkresult.add(v.getAttribute("href"));
            }

            // 네이버 -> 정치 홈 -> 사회 뉴스 -> 이미지 이외의 기사들 5개 수집.
            List<WebElement> societyNomalLink = wb.findElements(By.xpath(src.get("homeSocialListLinkXpth")));
            for (WebElement v : societyNomalLink) {
                linkresult.add(v.getAttribute("href"));
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
            wb.quit()
            log.info("=================================================== groovy out =============================================================");
        }

        return resultList;
    }
}
