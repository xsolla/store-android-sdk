package com.xsolla.android.login.jwt;


import java.util.Date;
import java.util.List;

public interface Claim {


    Boolean asBoolean();

    Integer asInt();

    Long asLong();

    Double asDouble();

    String asString();

    Date asDate();

    <T> T[] asArray(Class<T> tClazz) throws DecodeException;

    <T> List<T> asList(Class<T> tClazz) throws DecodeException;

    <T> T asObject(Class<T> tClazz) throws DecodeException;
}
