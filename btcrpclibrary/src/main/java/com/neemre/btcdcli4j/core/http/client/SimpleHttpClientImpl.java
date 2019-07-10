package com.neemre.btcdcli4j.core.http.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neemre.btcdcli4j.core.NodeProperties;
import com.neemre.btcdcli4j.core.common.Constants;
import com.neemre.btcdcli4j.core.common.DataFormats;
import com.neemre.btcdcli4j.core.common.Errors;
import com.neemre.btcdcli4j.core.http.HttpConstants;
import com.neemre.btcdcli4j.core.http.HttpLayerException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimpleHttpClientImpl implements SimpleHttpClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(SimpleHttpClientImpl.class);
	
	private OkHttpClient provider;
	private Properties nodeConfig;


	public SimpleHttpClientImpl(OkHttpClient provider, Properties nodeConfig) {
		LOG.info("** SimpleHttpClientImpl(): initiating the HTTP communication layer");
		this.provider = provider;
		this.nodeConfig = nodeConfig;
	}

	@Override
	public String execute(String reqMethod, String reqPayload) throws HttpLayerException {
		Response response = null;
		try {
			response=provider.newCall(getNewRequest(reqMethod, reqPayload)).execute();
			response = checkResponse(response);
			String respPayload = Constants.STRING_EMPTY;

			if(response!=null){
				respPayload=response.body().string();
			}

			return respPayload;
		} catch (IOException e) {
			throw new HttpLayerException(Errors.IO_UNKNOWN, e);
		}finally {
			if(response != null) {
				try {

					response.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	@Override
	public void close() {
		try {
			LOG.info(">> close(..): attempting to shut down the underlying HTTP provider");
//			provider.close();
		} catch (Exception e) {
			LOG.warn("<< close(..): failed to shut down the underlying HTTP provider, message was: "
					+ "'{}'", e.getMessage());
		}
	}

	private Request getNewRequest(String reqMethod, String reqPayload) {
		Request request;
		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqPayload);
		if(reqMethod.equals(HttpConstants.REQ_METHOD_POST)) {

			request=new Request.Builder()
					.url(nodeConfig.getProperty("host"))
					.post(body)
					.build();





		} else {
			throw new IllegalArgumentException(Errors.ARGS_HTTP_METHOD_UNSUPPORTED.getDescription());
		}


		return request;
	}



	
	private Response checkResponse(Response response)
			throws HttpLayerException,IOException {
		LOG.debug(">> checkResponse(..): checking HTTP response for non-OK status codes & "
				+ "unexpected header values");

		int code=response.code();
		if((code >= 400) && (code <= 499)) {
			throw new HttpLayerException(Errors.RESPONSE_HTTP_CLIENT_FAULT, response.body().string());
		}
		if((code >= 500) && (code <= 599)) {
			throw new HttpLayerException(Errors.RESPONSE_HTTP_SERVER_FAULT, response.body().string());
		}	
		return response;
	}
}