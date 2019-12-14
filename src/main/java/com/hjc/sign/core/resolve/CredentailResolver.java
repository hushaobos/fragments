package com.hjc.sign.core.resolve;


import com.hjc.sign.core.common.Credential;

import javax.servlet.http.HttpServletRequest;

public interface CredentailResolver {
    Credential resolveCredentail(HttpServletRequest request);
}
