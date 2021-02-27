package com.banistmo.itf.account.banking.transfer.services.db;

import java.util.List;
import java.util.function.Predicate;

import com.banistmo.commons.bso.services.db.CoreTransactionDetailsService;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.db.entities.TransactionConfig;

public class JsonFileCoreTransactionDetailService implements CoreTransactionDetailsService{
	
	private List<CoreTransaction> coreTransactions;
	
	public JsonFileCoreTransactionDetailService(List<CoreTransaction> coreTransactions) {
		this.coreTransactions = coreTransactions;
	}
	
	@Override
	public CoreTransaction get(String channelId, String serviceType) {
		return get(channelId, serviceType, null);
	}

	@Override
	public CoreTransaction get(String channelId, String serviceType, Predicate<TransactionConfig> filter) {
		CoreTransaction coreTransaction;
		coreTransaction = coreTransactions.stream()
				.filter(coreTx -> channelId.equalsIgnoreCase(coreTx.getChannelId()) && serviceType.equalsIgnoreCase(coreTx.getServiceType()))
				.findAny()
				.orElseThrow(null);

		return coreTransaction;
	}

}
