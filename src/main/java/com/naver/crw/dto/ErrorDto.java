package com.naver.crw.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ErrorDto {

    private String errorSiteNm;

    private String errorArticleCate;

    private String errorMsg;
    
    private String detailErrorMsg;

    @Builder
    public ErrorDto(String errorSiteNm, String errorArticleCate, String errorMsg,String detailErrorMsg) {
        this.errorSiteNm = errorSiteNm;
        this.errorArticleCate = errorArticleCate;
        this.errorMsg = errorMsg;
        this.detailErrorMsg = detailErrorMsg;
    }
}
