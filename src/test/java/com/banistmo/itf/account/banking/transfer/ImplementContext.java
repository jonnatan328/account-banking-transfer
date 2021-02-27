package com.banistmo.itf.account.banking.transfer;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class ImplementContext implements Context {

    @Override
    public String getAwsRequestId() {
        return "162fd37f-5fdf-4d4d-89b2-de5151a8c16d";
    }

    @Override
    public String getLogGroupName() {
        return "/aws/lambda/log-impl-dev-query-detail";
    }

    @Override
    public String getLogStreamName() {
        return "2019/05/13/[$LATEST]a5573dab816542808dbfee223d5ac0db";
    }

    @Override
    public String getFunctionName() {
        return "log-impl-dev-query-detail";
    }

    @Override
    public String getFunctionVersion() {
        return "$LATEST";
    }

    @Override
    public String getInvokedFunctionArn() {
        return "arn:aws:lambda:us-east-1:482627483434:function:log-impl-dev-query-detail";
    }

    @Override
    public CognitoIdentity getIdentity() {
        return new CognitoIdentity() {
            @Override
            public String getIdentityId() {
                return "";
            }

            @Override
            public String getIdentityPoolId() {
                return "";
            }
        };
    }

    @Override
    public ClientContext getClientContext() {
        return null;
    }

    @Override
    public int getRemainingTimeInMillis() {
        return 29703;
    }

    @Override
    public int getMemoryLimitInMB() {
        return 512;
    }

    @Override
    public LambdaLogger getLogger() {
        return new LambdaLogger() {
            @Override
            public void log(String s) {
                System.out.println(s);
            }

            @Override
            public void log(byte[] bytes) {
                System.out.println(bytes);
            }
        };
    }
}
