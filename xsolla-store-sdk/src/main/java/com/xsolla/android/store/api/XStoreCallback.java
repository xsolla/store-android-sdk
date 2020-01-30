package com.xsolla.android.store.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract public class XStoreCallback<T> implements Callback<T> {

    private String SERVER_IS_NOT_RESPONDING = "Server is not responding. Please try later.";

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else {
            String errorMessage = getErrorMessage(response.errorBody());
            onFailure(errorMessage);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure(SERVER_IS_NOT_RESPONDING);
    }

    abstract protected void onSuccess(T response);

    abstract protected void onFailure(String errorMessage);

    private String getErrorMessage(ResponseBody errorBody) {
        try {
            JSONObject errorObject = new JSONObject(errorBody.string());
            return errorObject.getJSONObject("error").getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Error";
    }

}
