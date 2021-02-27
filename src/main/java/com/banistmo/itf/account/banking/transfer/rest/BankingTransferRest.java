package com.banistmo.itf.account.banking.transfer.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tinylog.Logger;

import com.banistmo.commons.bso.exceptions.BadGatewayException;
import com.banistmo.commons.bso.exceptions.BadRequestException;
import com.banistmo.commons.bso.exceptions.GatewayTimeoutException;
import com.banistmo.commons.bso.exceptions.NotFoundException;
import com.banistmo.commons.bso.flow.RequestProcessor;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.resources.Status;
import com.banistmo.commons.bso.util.AppUtil;
import com.banistmo.commons.bso.util.StatusUtil;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferAddRS;
import com.banistmo.itf.account.banking.transfer.flow.TransferRequestProcessor;
import com.banistmo.logging.handler.LoggerHandler;
import com.banistmo.logging.util.Constants;

@RestController
@RequestMapping(value="/api/v1", produces = "application/json")
public class BankingTransferRest {
	
//	private static CtgRequestProcessor<XferAddRQ, XferAddRS> processor;
	TransferRequestProcessor requestProcessor;

    static {
//        processor = new CtgRequestProcessor<>(createRequestProcessor());
    }

    @ResponseBody
    @PostMapping(value="/apply-transfer", produces = "application/json")
    public XferAddRS applyTransfer(@RequestBody Request<XferAddRQ> request) {
        if (request == null || request.isAwakenFunction()) {
            return null;
        }
        Status statusError;
        Throwable ex;
        String rqUID = null;
        LoggerHandler loggerHandler = null;
        try {
        	rqUID = AppUtil.getRqUID(request);
	//        System.out.println(context.getAwsRequestId());
	//        ImplementContext context  = new ImplementContext();
	//        return processor.process(request);
	        RestContext restContext = createContext();
	        loggerHandler = LoggerHandler.getInstance(Constants.TIER_COMPOSITION, request, restContext);
	        requestProcessor = (TransferRequestProcessor) createRequestProcessor();
	        return requestProcessor.process(request, loggerHandler);
	    } catch (BadRequestException bre) {
	    	System.out.println("Exception 1" + bre);
	        ex = bre;
	        statusError = StatusUtil.buildStatusBadRequest(bre);
	    } catch (NotFoundException nfe) {
	    	System.out.println("Exception 2" + nfe);
	        ex = nfe;
	        statusError = StatusUtil.buildStatusNotFound(nfe);
	    } catch (BadGatewayException bge) {
	    	System.out.println("Exception 3" + bge);
	        ex = bge;
	        statusError = StatusUtil.buildStatusBadGateway(bge);
	    } catch (GatewayTimeoutException gte) {
	    	System.out.println("Exception 4" + gte);
	        ex = gte;
	        statusError = StatusUtil.buildStatusGatewayTimeout(gte);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Exception 5" + e);
	        ex = e;
	        statusError = StatusUtil.buildStatusInternalServerError();
	    }
	
        requestProcessor.getLogError().log(loggerHandler, ex);
        return null;
//	    String errorResponseString = createErrorResponse(rqUID, statusError);
//	    throw new LambdaApplicationException(errorResponseString);
    }

    private static RequestProcessor<XferAddRQ, XferAddRS> createRequestProcessor() {
        RequestProcessorComponent component = DaggerRequestProcessorComponent.create();
        return component.makeRequestProcessor();
    }
	
	@ResponseBody
    @GetMapping("/health")
    public String health() {
    	Logger.info("Ingreso Ok");
    	return "OK";
    }
	
	private RestContext createContext() {
    	RestContext restContext = new RestContext();
    	restContext.setAwsRequestId("asdfasdfasdf78as98d7f");
    	restContext.setFunctionName("functionName");
    	restContext.setFunctionVersion("FunctionVersion");
    	restContext.setInvokedFunctionArn("invokedFunctionArn");
    	restContext.setLogGroupName("logGroupName");
    	restContext.setLogStreamName("logStreamName");
    	restContext.setMemoryLimitInMB("memoryLimitInMB");
    	
    	return restContext;
    }
}
