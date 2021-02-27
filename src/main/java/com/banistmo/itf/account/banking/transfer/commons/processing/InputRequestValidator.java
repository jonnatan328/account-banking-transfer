package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.exceptions.BadRequestException;
import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.commons.bso.pipe.PipeMessage;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.validators.*;

public class InputRequestValidator<T extends PipeMessage<? extends Request, ?, ?, ?>> implements Pipe<T> {

    private POSTRequestValidator postValidator;

    public InputRequestValidator(String schemaFileName) {
        postValidator = new POSTRequestValidator(schemaFileName);
    }

    public InputRequestValidator(POSTRequestValidator postValidator) {
        this.postValidator = postValidator;
    }

    @Override
    public T process(T pipeMessage) {
        if (!postValidator.isValid(pipeMessage.getRequest())) {
            throw new BadRequestException(postValidator.getMessage(), postValidator.getFailedFields());
        }

        return pipeMessage;
    }
}
