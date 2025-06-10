package de.sovity.edc.ext.wrapper.api.ui.pages.asset;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BscwApiService {
    private final String bscwHost;

    public String getMetadataForOID(int oid, String auth) {
        var client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
        var req = new Request.Builder()
            .url(bscwHost + oid)
            .header("Authorization", "Basic " + auth)
            .build();

        try(var response = client.newCall(req).execute()) {
            if(response.code() == 401) throw new NotAuthorizedException("BSCW authentication failed");
            if (response.code() == 403) throw new ForbiddenException("BSCW access forbidden");
            if(response.body() == null) return "";
            return response.body().string();
        } catch (IOException | NullPointerException e) {
            throw new InternalServerErrorException("failed to get metadata from BSCW for OID " + oid);
        }
    }
}
