package io.hasura.db;

import io.hasura.core.Converter;
import io.hasura.core.HasuraJsonException;
import io.hasura.core.Util;

import java.io.IOException;
import java.lang.reflect.Type;

public class DBResponseConverter<T> implements Converter<T, DBException> {

    private final Type resType;

    public DBResponseConverter(Type resType) {
        this.resType = resType;
    }

    @Override
    public T fromResponse(okhttp3.Response response) throws DBException {
        int code = response.code();

        try {
            if (code == 200) {
                return Util.parseJson(DBService.gson, response, resType);
            } else {
                DBErrorResponse err = Util.parseJson(DBService.gson, response, DBErrorResponse.class);
                DBError errCode;
                switch (code) {
                    case 400:
                        errCode = DBError.BAD_REQUEST;
                        break;
                    case 401:
                        errCode = DBError.UNAUTHORIZED;
                        break;
                    case 402:
                        errCode = DBError.REQUEST_FAILED;
                        break;
                    case 403:
                        errCode = DBError.INVALID_SESSION;
                        break;
                    case 500:
                        errCode = DBError.INTERNAL_ERROR;
                        break;
                    default:
                        errCode = DBError.UNEXPECTED_CODE;
                        break;
                }
                throw new DBException(errCode, err.getError());
            }
        } catch (HasuraJsonException e) {
            throw new DBException(DBError.INTERNAL_ERROR, e);
        }
    }

    @Override
    public DBException fromIOException(IOException e) {
        return new DBException(DBError.CONNECTION_ERROR, e);
    }

    @Override
    public DBException castException(Exception e) {
        return (DBException) e;
    }
}
