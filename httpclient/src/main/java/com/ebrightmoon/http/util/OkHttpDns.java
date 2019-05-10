package com.ebrightmoon.http.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Dns;

/**
 * 作者：create by  Administrator on 2019/1/19
 * 邮箱：2315813288@qq.com
 */
public class OkHttpDns implements Dns {

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {

        return SYSTEM.lookup(hostname);
    }
}
