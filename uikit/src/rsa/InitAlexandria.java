package rsa;

import org.alexandria.client.AlexCipherClient;

/**
 * Created by Wall on 2016/3/28.
 * Alexandria组件配置
 *在 LoginActivity 页面中，注册成功后通过账户生成 AlexCipherClient 端
 *
 */
public class InitAlexandria  {
    public  static final String test_txt = "testtxt"; //
    public static final String test_img= "testimg";  //
    private static final String test_location = "ws://192.168.3.24:8086";//123.56.95.25
    private static AlexCipherClient initAlexandria_img;

    // 获取AlexCipherClient单例模式、
    public static AlexCipherClient getAlexandriaInstance(String type){
        if(type==null){
            return null;
        }
            initAlexandria_img=new AlexCipherClient(type,test_location);
            return initAlexandria_img;

    }



}
