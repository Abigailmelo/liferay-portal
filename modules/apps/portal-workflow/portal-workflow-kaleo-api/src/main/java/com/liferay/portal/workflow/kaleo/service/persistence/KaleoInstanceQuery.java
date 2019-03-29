/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.kaleo.service.persistence;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

import java.io.Serializable;

import java.util.Date;

/**
 * @author István András Dézsi
 */
public class KaleoInstanceQuery implements Serializable {

	public KaleoInstanceQuery(ServiceContext serviceContext) {
		_serviceContext = serviceContext;

		_companyId = serviceContext.getCompanyId();
		_userId = serviceContext.getUserId();
	}

	public String getAssetDescription() {
		return _assetDescription;
	}

	public String getAssetTitle() {
		return _assetTitle;
	}

	public String getClassName() {
		return _className;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public Date getCompletionDateGT() {
		return _completionDateGT;
	}

	public Date getCompletionDateLT() {
		return _completionDateLT;
	}

	public int getEnd() {
		return _end;
	}

	public String getKaleoDefinitionName() {
		return _kaleoDefinitionName;
	}

	public Long getKaleoInstanceId() {
		return _kaleoInstanceId;
	}

	public OrderByComparator<KaleoInstance> getOrderByComparator() {
		return _orderByComparator;
	}

	public ServiceContext getServiceContext() {
		return _serviceContext;
	}

	public int getStart() {
		return _start;
	}

	public String getStatus() {
		return _status;
	}

	public long getUserId() {
		return _userId;
	}

	public boolean isAndOperator() {
		return _andOperator;
	}

	public Boolean isCompleted() {
		return _completed;
	}

	public void setAndOperator(boolean andOperator) {
		_andOperator = andOperator;
	}

	public void setAssetDescription(String assetDescription) {
		_assetDescription = assetDescription;
	}

	public void setAssetTitle(String assetTitle) {
		_assetTitle = assetTitle;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setCompleted(Boolean completed) {
		_completed = completed;
	}

	public void setCompletionDateGT(Date completionDateGT) {
		_completionDateGT = completionDateGT;
	}

	public void setCompletionDateLT(Date completionDateLT) {
		_completionDateLT = completionDateLT;
	}

	public void setEnd(int end) {
		_end = end;
	}

	public void setKaleoDefinitionName(String kaleoDefinitionName) {
		_kaleoDefinitionName = kaleoDefinitionName;
	}

	public void setKaleoInstanceId(Long kaleoInstanceId) {
		_kaleoInstanceId = kaleoInstanceId;
	}

	public void setOrderByComparator(
		OrderByComparator<KaleoInstance> orderByComparator) {

		_orderByComparator = orderByComparator;
	}

	public void setServiceContext(ServiceContext serviceContext) {
		_serviceContext = serviceContext;
	}

	public void setStart(int start) {
		_start = start;
	}

	public void setStatus(String status) {
		_status = status;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	private boolean _andOperator = true;
	private String _assetDescription;
	private String _assetTitle;
	private String _className;
	private long _companyId;
	private Boolean _completed;
	private Date _completionDateGT;
	private Date _completionDateLT;
	private int _end = QueryUtil.ALL_POS;
	private String _kaleoDefinitionName;
	private Long _kaleoInstanceId;
	private OrderByComparator<KaleoInstance> _orderByComparator;
	private ServiceContext _serviceContext;
	private int _start = QueryUtil.ALL_POS;
	private String _status;
	private long _userId;

}