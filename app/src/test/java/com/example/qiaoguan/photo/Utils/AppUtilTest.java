package com.example.qiaoguan.photo.Utils;

import com.example.qiaoguan.photo.PhotoDataManger;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppUtilTest {


    @Test
    public void testKey(){
        String url = "https://img.xcitefun.net/users/2012/11/309561,xcitefun-ileana-wallpaper-6.jpg";
        String key = AppUtil.hashKeyForDisk(url);
        assertNotNull(key);
        System.out.println(key);
    }
}