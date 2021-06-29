package com.naver.crw.schedule;

import com.naver.crw.domain.Source;
import com.naver.crw.dto.ArticleCate;
import com.naver.crw.dto.SiteName;
import com.naver.crw.exception.CrwErrorException;
import com.naver.crw.main.CrwMain;
import com.naver.crw.repository.SrcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * User: wandol<br/>
 * Date: 2020/11/10<br/>
 * Time: 9:08 오후<br/>
 * Desc:    스케줄이 실행 될때 실행될 메소드 설정 및 다음 스케줄 주기 변경된 부분 체크 및 적용.
 */
@Component
@Slf4j
public class SchedulePolitics implements SchedulerInterface{

    @Autowired
    private SrcRepository srcRepository;

    private ScheduledFuture<?> future;

    private TaskScheduler scheduler;
    
    @Autowired
    public SchedulePolitics(@Lazy TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Autowired
    private CrwMain crwMain;

    @Override
    public void start() {
        future = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    /**
                     *  네이버 정치 홈 페이징 부분 하루치 기사 수집
                     *  00:00 시에 시작하여 목록 부분에 '1일전' 이라는 글씨를 만나기 전까지 수집.
                     */
                    log.info("NAVER POLITICS PAGING AREA schedule START =================================================================");
                    long beforeTime = System.currentTimeMillis();
                    crwMain.startPolPageCrw();
                    long afterTime = System.currentTimeMillis();
                    log.info("POLITICS 소요 시간  :: {} 초" , (afterTime - beforeTime)/1000 );
                    log.info("NAVER POLITICS PAGING AREA schedule END =================================================================");
                } catch (CrwErrorException e) {
                    e.printStackTrace();
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
            	log.info("SELECT HEADLINE SCHEDULE VALUE =================================================================");
                Source src  = srcRepository.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.name(), ArticleCate.POLITICS.name(), "Y");
                if(src != null){
                    String cron = src.getCrwCycle();
                    log.info("update HEADLINE cron value :: {}" , cron);
                    log.info("========================================================================================");
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }else{
                    log.info("========================================================================================");
                    return null;
                }
            }
        });
    }

    @Override
    public void stop() {
        future.cancel(false);
    }


}
