
    INSERT INTO crw.potal_source_tb (
        id,
        site_Nm,
        article_Category,
        crw_Cycle,
        use_Yn,
        crw_Status,
        crw_Status_Msg,
        start_url,
        home_Headline_Img_Link_Xpth,
        home_Headline_List_Link_Xpth,
        home_Politics_Img_Link_Xpth,
        home_Politics_List_Link_Xpth,
        home_Social_Img_Link_Xpth,
        home_Social_List_Link_Xpth,
        cate_Headline_List_Xpath,
        cate_Paging_List_Xpath,
        cate_Paging_List_Date_Xpath,
        article_cate_xpth,
        article_title_xpth,
        article_write_dt_xpth,
        article_writer_xpth,
        article_cont_xpth,
        article_img_cont_xpth,
        article_media_nm_xpth,
        reg_Dt,
        up_Dt
    )   VALUES (
        1,
        'NAVER',
        'HEADLINE',
        '10,20,30,40,50,00 * * * *',
        'Y',
        'NOTHING',
        '',
        'https://news.naver.com/main/home.nhn',
        '//div[@class=\'hdline_flick_item\']/a[1]',
        '//div[@class=\'hdline_article_tit\']/a',
        '//div[@id=\'section_politics\']/div[@class=\'com_list\']/dl[@class=\'mtype_img\']/dt/a',
        '//div[@id=\'section_politics\']/div[@class=\'com_list\']/div[@class=\'mtype_list_wide\']/ul/li/a',
        '//div[@id=\'section_society\']/div[@class=\'com_list\']/dl[@class=\'mtype_img\']/dt/a',
        '//div[@id=\'section_society\']/div[@class=\'com_list\']/div[@class=\'mtype_list_wide\']/ul/li/a',
        '',
        '',
        '',
        'me2:category2',
        'og:title',
        't11',
        'me2:post_tag',
        '_article_body_contents',
        'img_desc',
        'me2:category1',
        now(),
        now()
    );


    INSERT INTO crw.potal_source_tb (
        id,
        site_Nm,
        article_Category,
        crw_Cycle,
        use_Yn,
        crw_Status,
        crw_Status_Msg,
        start_url,
        home_Headline_Img_Link_Xpth,
        home_Headline_List_Link_Xpth,
        home_Politics_Img_Link_Xpth,
        home_Politics_List_Link_Xpth,
        home_Social_Img_Link_Xpth,
        home_Social_List_Link_Xpth,
        cate_Headline_List_Xpath,
        cate_Paging_List_Xpath,
        cate_Paging_List_Date_Xpath,
        article_cate_xpth,
        article_title_xpth,
        article_write_dt_xpth,
        article_writer_xpth,
        article_cont_xpth,
        article_img_cont_xpth,
        article_media_nm_xpth,
        reg_Dt,
        up_Dt
    )   VALUES (
        2,
        'NAVER',
        'POLITICS',
        '0 0 0 * *',
        'Y',
        'NOTHING',
        '',
        'https://news.naver.com/main/main.nhn?mode=LSD&mid=shm&sid1=100#&date=%2000:00:00&page=',
        '',
        '',
        '',
        '',
        '',
        '',
        '//div[@class=\'cluster_text\']/a',
        '//*[@id=\'section_body\']/ul/li/dl/dt[2]/a',
        '//*[@id=\'section_body\']/ul/li/dl/dd/span[3]',
        'me2:category2',
        'og:title',
        't11',
        'me2:post_tag',
        '_article_body_contents',
        'img_desc',
        'me2:category1',
        now(),
        now()
    );

    INSERT INTO crw.potal_source_tb (
        id,
        site_Nm,
        article_Category,
        crw_Cycle,
        use_Yn,
        crw_Status,
        crw_Status_Msg,
        start_url,
        home_Headline_Img_Link_Xpth,
        home_Headline_List_Link_Xpth,
        home_Politics_Img_Link_Xpth,
        home_Politics_List_Link_Xpth,
        home_Social_Img_Link_Xpth,
        home_Social_List_Link_Xpth,
        cate_Headline_List_Xpath,
        cate_Paging_List_Xpath,
        cate_Paging_List_Date_Xpath,
        article_cate_xpth,
        article_title_xpth,
        article_write_dt_xpth,
        article_writer_xpth,
        article_cont_xpth,
        article_img_cont_xpth,
        article_media_nm_xpth,
        reg_Dt,
        up_Dt
    )   VALUES (
        3,
        'NAVER',
        'SOCIAL',
        '0 0 0 * *',
        'Y',
        'NOTHING',
        '',
        'https://news.naver.com/main/main.nhn?mode=LSD&mid=shm&sid1=102#&date=%2000:00:00&page=',
        '',
        '',
        '',
        '',
        '',
        '',
        '//div[@class=\'cluster_text\']/a',
        '//*[@id=\'section_body\']/ul/li/dl/dt[2]/a',
        '//*[@id=\'section_body\']/ul/li/dl/dd/span[3]',
        'me2:category2',
        'og:title',
        't11',
        'me2:post_tag',
        '_article_body_contents',
        'img_desc',
        'me2:category1',
        now(),
        now()
    );

    INSERT INTO crw.potal_source_tb (
        id,
        site_Nm,
        article_Category,
        crw_Cycle,
        use_Yn,
        crw_Status,
        crw_Status_Msg,
        start_url,
        home_Headline_Img_Link_Xpth,
        home_Headline_List_Link_Xpth,
        home_Politics_Img_Link_Xpth,
        home_Politics_List_Link_Xpth,
        home_Social_Img_Link_Xpth,
        home_Social_List_Link_Xpth,
        cate_Headline_List_Xpath,
        cate_Paging_List_Xpath,
        cate_Paging_List_Date_Xpath,
        article_cate_xpth,
        article_title_xpth,
        article_write_dt_xpth,
        article_writer_xpth,
        article_cont_xpth,
        article_img_cont_xpth,
        article_media_nm_xpth,
        reg_Dt,
        up_Dt
    )   VALUES (
        4,
        'NAVER',
        'OPINION',
        '0 0 0 * *',
        'Y',
        'NOTHING',
        '',
        'https://news.naver.com/main/list.nhn?mode=LSD&mid=shm&sid1=110&page=',
        '',
        '',
        '',
        '',
        '',
        '',
        '',
        '//*[@id=\'main_content\']/div[2]/ul/li/dl/dt[1]/a',
        '',
        'me2:category2',
        'og:title',
        't11',
        'me2:post_tag',
        '_article_body_contents',
        'img_desc',
        'me2:category1',
        now(),
        now()
    );