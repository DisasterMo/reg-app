/*******************************************************************************
 * Copyright (c) 2014 Michael Simon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Michael Simon - initial
 ******************************************************************************/
package edu.kit.scc.webreg.service.impl;

import static edu.kit.scc.webreg.dao.ops.RqlExpressions.contains;
import static edu.kit.scc.webreg.dao.ops.RqlExpressions.equal;

import java.util.List;

import edu.kit.scc.webreg.dao.BaseDao;
import edu.kit.scc.webreg.dao.ops.PaginateBy;
import edu.kit.scc.webreg.dao.ops.RqlExpression;
import edu.kit.scc.webreg.dao.ops.SortBy;
import edu.kit.scc.webreg.entity.BaseEntity;
import edu.kit.scc.webreg.service.BaseService;

public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

	private static final long serialVersionUID = 1L;

	protected abstract BaseDao<T> getDao();

	@Override
	public T createNew() {
		return getDao().createNew();
	}

	@Override
	public T save(T entity) {
		return getDao().persist(entity);
	}

	@Override
	public T merge(T entity) {
		return getDao().merge(entity);
	}

	@Override
	public void delete(T entity) {
		getDao().delete(entity);
	}

	@Override
	public List<T> findAll() {
		return getDao().findAll();
	}

	@Override
	public List<T> findAllPaging(PaginateBy paginateBy, SortBy sortBy, RqlExpression rqlExpression) {
		return getDao().findAllPaging(paginateBy, sortBy, rqlExpression);
	}

	@Override
	public List<T> findAllPaging(PaginateBy paginateBy, List<SortBy> sortBy, RqlExpression rqlExpression,
			String... joinFetchBy) {
		return getDao().findAllPaging(paginateBy, sortBy, rqlExpression, joinFetchBy);
	}

	@Override
	public Number countAll(RqlExpression filterBy) {
		return getDao().countAll(filterBy);
	}

	@Override
	public T findById(Long id) {
		return getDao().findById(id);
	}

	@Override
	public T findByAttr(String attr, Object value) {
		return getDao().findByAttr(attr, value);
	}

	@Override
	public List<T> findAllByAttr(String attr, Object value) {
		return value instanceof String ? getDao().findAllPaging(contains(attr, value.toString()))
				: getDao().findAllPaging(equal(attr, value));
	}

	@Override
	public List<T> findByMultipleId(List<Long> ids) {
		return getDao().findByMultipleId(ids);
	}

	@Override
	public T findByIdWithAttrs(Long id, String... attrs) {
		return getDao().findByIdWithAttrs(id, attrs);
	}
}
