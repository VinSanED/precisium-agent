package com.precisium.agent.utils;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface Transfer {
    HttpResponse<String> send(HttpRequest request)
        throws IOException, InterruptedException;
}

