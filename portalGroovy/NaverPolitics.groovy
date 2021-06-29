
import com.naver.crw.domain.Source;
import com.naver.crw.dto.ArticleArea;
import com.naver.crw.dto.ErrorDto;
import com.naver.crw.dto.SiteName;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

class NaverPoliticsClass {

    private static final Logger log = LoggerFactory.getLogger(NaverPoliticsClass.class);

    public List<Map<String,Object>> CrwMain(Map<String,Object> src) throws CrwErrorException, InterruptedException{
        
        List<String> linkresult = new ArrayList<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> resultMap = null;
        Document doc;

        System.setProperty("webdriver.chrome.driver", src.get("chromeDriverPath") );
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        
        WebDriver wb = null;
        
        try {
            log.info("=================================================== groovy in =============================================================");
            log.info("groovy 수집 url :: {}" , src.get("startUrl"));
            
            // ================================================================================================================
            //  headline 쪽.
            wb = new ChromeDriver(options);
            wb.manage().window().maximize();
            wb.navigate().to(src.get("startUrl"));
            WebDriverWait wait;

            By container = By.cssSelector("#wrap");
            wait = new WebDriverWait(wb, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(container));

            //  더보기 클릭.
            wb.findElement(By.className("cluster_more_inner")).click();
            Thread.sleep(1000);

            //  더보기 클릭
            wb.findElement(By.className("cluster_more_inner")).click();
            Thread.sleep(1000);
            
            try {
                wb.findElement(By.className("cluster_more_inner")).click();
            }catch(Exception e) {
                log.info("더보기 클릭 후 없으면 . " ,e.getMessage());
            }
            
            Thread.sleep(1000);

            //  더보기 클릭하여 모든 화면 영역을 노출후 리스트가 있는 elements를 가져온다.
            WebElement textElement = wb.findElement(By.className("list_body"));
            wait.until(ExpectedConditions.visibilityOf(textElement));
            List<WebElement> elementList = textElement.findElements(By.xpath(src.get("cateHeadlineListXpath")));

            linkresult = elementList.stream().map({v -> v.getAttribute("href")}).collect(Collectors.toList());
            
            if(wb != null)	wb.quit()

            log.info("=============================== headline done ===============================");
            log.info("=============================== paging start ===============================");
            //  ================================================================================================
            //  paging 쪽 
            String linkX = src.get("catePagingListXpath");
            String dateX = src.get("catePagingListDateXpath");
            
            //  today에 해당 되는 것만 수집하기 위한 flag
            boolean stopFlag = true;
            int page = 1;

            //
            while(stopFlag){
                String startUrl = src.get("startUrl") + page;
                wb = new ChromeDriver(options);
                wb.navigate().to(startUrl);

                List<WebElement> pageLinkList = wb.findElements(By.xpath(linkX));
                List<WebElement> pageDateList = wb.findElements(By.xpath(dateX));
                //  위 두개의 list는 사이즈가 같아야 한다.
                //  for문 돌며 날짜 체크 하여 2일전 텍스트를 만나면 종료 한다.
                for (int i = 0; i < pageLinkList.size(); i++) {
                    //  수집 목록 경과시간 수집한 텍스트.
                    String checkDate = pageDateList.get(i).getText().trim();
                    //  1일전 데이터만 수집한다.
                    if("1일전".equals(checkDate)){
                        stopFlag = false;
                    }else{
                        linkresult.add(pageLinkList.get(i).getAttribute("href"));
                    }
                }

                //  page 증가.
                page++;
                //  2초간 페이징 이동 간격
                Thread.sleep(2000);

                //  브라우져 종료.
                wb.quit();
            }

			
			if(wb != null)	wb.quit()
			log.info("=============================== paging done ===============================");
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
