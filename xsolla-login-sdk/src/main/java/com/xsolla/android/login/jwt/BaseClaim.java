package com.xsolla.android.login.jwt;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Deprecated
class BaseClaim implements Claim {

    @Override
    public Boolean asBoolean() {
        return null;
    }

    @Override
    public Integer asInt() {
        return null;
    }

    @Override
    public Long asLong() {
        return null;
    }

    @Override
    public Double asDouble() {
        return null;
    }

    @Override
    public String asString() {
        return null;
    }

    @Override
    public Date asDate() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] asArray(Class<T> tClazz) throws DecodeException {
        return (T[]) Array.newInstance(tClazz, 0);
    }

    @Override
    public <T> List<T> asList(Class<T> tClazz) throws DecodeException {
        return Collections.emptyList();
    }

    @Override
    public <T> T asObject(Class<T> tClazz) throws DecodeException {
        return null;
    }
}
