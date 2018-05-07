package cn.huimin.process.web.util;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/6.
 */
public class UUIDutils
{
    public static String createUUID(){
       return UUID.randomUUID().toString();
    }


    public static String createShortUUID(){
        String s = UUID.randomUUID().toString();
        s.replace("-","");
        return s;
    }

    public static void main(String args[]){
        System.out.print(createUUID());
    }


}
