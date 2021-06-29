package com.naver.crw.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrwErrorException extends Throwable {
	
	private static final long serialVersionUID = 1L;


	public CrwErrorException(String msg) {
		log.error("detail error msg {}", msg);
	}

	public CrwErrorException() {
	}

}
