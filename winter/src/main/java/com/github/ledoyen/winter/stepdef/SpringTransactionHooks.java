package com.github.ledoyen.winter.stepdef;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.github.ledoyen.automocker.tools.Classes;

import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * Pasted from SpringFactory as extending Cucumber is such a pain.
 * 
 * <p>
 * This class defines before and after hooks which provide automatic spring rollback capabilities. These hooks will apply to any element(s) within a
 * <code>.feature</code> file tagged with <code>@txn</code>.
 * </p>
 * <p>
 * Clients wishing to leverage these hooks should include this class' package in the <code>glue</code> code.
 * </p>
 * <p>
 * The BEFORE and AFTER hooks (both with hook order 100) rely on being able to obtain a <code>PlatformTransactionManager</code> by type, or by an
 * optionally specified bean name, from the runtime <code>BeanFactory</code>.
 * </p>
 * <p>
 * NOTE: This class is NOT threadsafe! It relies on the fact that cucumber-jvm will instantiate an instance of any applicable hookdef class per
 * scenario run.
 * </p>
 */
public class SpringTransactionHooks implements BeanFactoryAware {

	private static final boolean ENABLED = Classes
			.isPresent("org.springframework.transaction.TransactionStatus");

	private BeanFactory beanFactory;
	private String txnManagerBeanName;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * Setter to allow (optional) bean name to be specified for transaction manager bean - if null, attempt will be made to find a transaction manager
	 * by bean type
	 *
	 * @param txnManagerBeanName bean name of transaction manager bean
	 */
	public void setTxnManagerBeanName(String txnManagerBeanName) {
		this.txnManagerBeanName = txnManagerBeanName;
	}

	private TransactionHolder holder;

	@Before(value = { "@txn" }, order = 100)
	public void startTransaction() {
		if (ENABLED) {
			holder = new TransactionHolder(beanFactory, txnManagerBeanName).start();
		}
	}

	@After(value = { "@txn" }, order = 100)
	public void rollBackTransaction() {
		if (ENABLED) {
			holder.stop();
		}
	}

	private static class TransactionHolder {
		private final BeanFactory beanFactory;
		private final String txnManagerBeanName;

		private TransactionStatus transactionStatus;

		public TransactionHolder(BeanFactory beanFactory, String txnManagerBeanName) {
			this.beanFactory = beanFactory;
			this.txnManagerBeanName = txnManagerBeanName;
		}

		private TransactionHolder start() {
			transactionStatus = obtainPlatformTransactionManager()
					.getTransaction(new DefaultTransactionDefinition());
			return this;
		}

		private void stop() {
			obtainPlatformTransactionManager().rollback(transactionStatus);
		}

		public PlatformTransactionManager obtainPlatformTransactionManager() {
			if (txnManagerBeanName == null) {
				return beanFactory.getBean(PlatformTransactionManager.class);
			} else {
				return beanFactory.getBean(txnManagerBeanName, PlatformTransactionManager.class);
			}
		}
	}
}
