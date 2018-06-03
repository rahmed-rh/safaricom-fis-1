/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.rahmed.redhat.demo.rest;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.infinispan.commons.api.BasicCacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayeesService {

	final static Logger LOG = LoggerFactory.getLogger(PayeesService.class);
	private static CopyOnWriteArrayList<Payee> dummyList = new CopyOnWriteArrayList<Payee>();

	@Autowired
	private BasicCacheContainer remoteCacheContainer;

	public String createPayee(Payee payee) {
		LOG.info("In createPayee, remoteCacheContainer =" + remoteCacheContainer);
		LOG.info("In createPayee, remoteCacheContainer.getCache(\"payees\") =" + remoteCacheContainer.getCache("payees"));
		LOG.info("In createPayee, Payee value =" + payee);
		remoteCacheContainer.getCache("payees").put("payee_id_" + payee.getId(), payee);
		LOG.info("In createPayee, after calling remote cache");
		// dummyList.add(payee);
		return "order: " + payee.getId();
	}

	public List<Payee> listPayees() {
		Set<Object> payeesSet= remoteCacheContainer.getCache("payees").keySet();
		LOG.info("In listPayees, remoteCacheContainer.getCache(\"payees\").keySet() =" + payeesSet);
		return payeesSet.stream().map(k -> remoteCacheContainer.getCache("payees").get((String)k)).map(Payee.class::cast)
				.collect(Collectors.toList());

	}

	public Payee findPayeeById(String payeeId) {
		LOG.info("In findPayeeById, payeeId =" + payeeId);
		LOG.info("In findPayeeById, payee =" + remoteCacheContainer.getCache("payees").get("payee_id_" + payeeId));
		return remoteCacheContainer.getCache("payees").get("payee_id_" + payeeId) != null
				? (Payee) remoteCacheContainer.getCache("payees").get("payee_id_" + payeeId)
				: null;
	}
}