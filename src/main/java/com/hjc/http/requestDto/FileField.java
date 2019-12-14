package com.hjc.http.requestDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileField {
    private String uid;//用户id
    private String url;
    private String name;//文件原名
    private String type;//文件类型
    private String ossFilename;//临时文件名

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOssFilename() {
        return ossFilename;
    }

    public void setOssFilename(String ossFilename) {
        this.ossFilename = ossFilename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
