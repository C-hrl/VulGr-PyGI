package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ResultConfig;

import java.util.Map;


public interface ResultFactory {

    Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception;

}
