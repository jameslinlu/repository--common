package com.commons.common.generic.service;


import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.generic.IRepository;
import com.commons.metadata.generic.IService;
import com.commons.metadata.model.Page;
import com.commons.metadata.model.PageParam;
import com.commons.metadata.model.PageWrapper;

import java.util.List;

public abstract class GenericService<T> implements IService<T> {

    public abstract IRepository<T> getRepository();

    public T insert(T model) throws ServiceException {
        try {
            int result = getRepository().insert(model);
            if (result <= 0) {
                throw new ServiceException(ResultCode.ERROR_SAVE);
            }
            return model;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_SAVE, e);
        }
    }

    @Override
    public List<T> inserts(List<T> models) throws ServiceException {
        for (T model : models) {
            this.insert(model);
        }
        return models;
    }

    public boolean update(T model) throws ServiceException {
        try {
            int result = getRepository().update(model);
            if (result <= 0)
                return false;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_UPDATE, e);
        }
        return true;
    }

    public boolean delete(T model) throws ServiceException {
        try {
            int result = getRepository().delete(model);
            if (result <= 0)
                return false;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_DELETE, e);
        }
        return true;
    }

    @Override
    public boolean deletes(List<T> models) throws ServiceException {
        for (T model : models) {
            this.delete(model);
        }
        return true;
    }

    public T get(T model) throws ServiceException {
        try {
            return getRepository().get(model);
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_SEARCH, e);
        }
    }

    public Page<T> page(PageParam page, T model) throws ServiceException {
        try {
            return ((PageWrapper<T>) getRepository().page(page, model)).toPage();
        } catch (Exception e) {
            throw new ServiceException(ResultCode.ERROR_SEARCH, e);
        }
    }
}
