package com.neoqee.commonlib.http;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface ApiService {

    @GET()
    Observable<BaseResponse> getData();

}
