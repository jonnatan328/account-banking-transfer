package com.banistmo.itf.account.banking.transfer.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestContext {

	private String awsRequestId;
	private String functionName;
	private String functionVersion;
	private String invokedFunctionArn;
	private String logGroupName;
	private String logStreamName;
	private String memoryLimitInMB;
}
