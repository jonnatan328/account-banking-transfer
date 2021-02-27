package com.banistmo.itf.account.banking.transfer.services;

import java.io.IOException;
import java.io.InputStream;

import com.banistmo.commons.bso.exceptions.ApplicationException;
import com.banistmo.commons.bso.services.BusinessServices;
import com.banistmo.commons.bso.services.db.CoreErrorTransactionsService;
import com.banistmo.commons.bso.services.db.CoreTransactionDetailsService;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.notify.NotifyService;
import com.banistmo.itf.account.banking.transfer.services.db.JsonFileCoreErrorTransactionsService;
import com.banistmo.itf.account.banking.transfer.services.db.JsonFileCoreTransactionDetailService;
import com.fasterxml.jackson.jr.ob.JSON;

public class FileBusinessService implements BusinessServices{
	
	protected CoreTransactionDetailsService coreTransactionDetailsService;
	protected CoreErrorTransactionsService coreErrorTransactionsService;
	
	private final InputStream inputStreamConfig;
	
	public FileBusinessService(InputStream inputStreamConfig) {
		this.inputStreamConfig = inputStreamConfig;
	}
	  
	@Override
	public CoreTransactionDetailsService getCoreTransactionDetailsService() {
		if (coreTransactionDetailsService == null) {
			try {
				coreTransactionDetailsService = 
						new JsonFileCoreTransactionDetailService(JSON.std.listOfFrom(CoreTransaction.class, inputStreamConfig));
			} catch (IOException e) {
				throw new ApplicationException("Error loading datos trx file.", e);
			}
		}
		return coreTransactionDetailsService;
	}

	@Override
	public CoreErrorTransactionsService getCoreErrorTransactionsService() {
		if (coreErrorTransactionsService == null) {
			coreErrorTransactionsService = new JsonFileCoreErrorTransactionsService();
		}
		return coreErrorTransactionsService;
	}

	@Override
	public NotifyService getNotifyService() {
		// TODO Auto-generated method stub
		return null;
	}

}
