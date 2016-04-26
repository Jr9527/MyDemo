package com.netease.nim.demo.session.action;

import android.content.Intent;
import android.util.Log;

import com.netease.nim.demo.R;
import com.netease.nim.demo.file.browser.FileBrowserActivity;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.constant.RequestCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

import rsa.Arithmetic;
import rsa.SecretManageFactory;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class FileAction extends BaseAction {

    public FileAction() {
        super(R.drawable.message_plus_file_selector, R.string.input_panel_file);
    }

    /**
     * **********************文件************************
     */
    private void chooseFile() {
        FileBrowserActivity.startActivityForResult(getActivity(), makeRequestCode(RequestCode.GET_LOCAL_FILE));
    }

    @Override
    public void onClick() {
        chooseFile();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.GET_LOCAL_FILE) {
            String path = data.getStringExtra(FileBrowserActivity.EXTRA_DATA_PATH);
            //  在这里 换取path?
            // 在这偷换地址,把加密图片处理
            String patPath=path;// 在原始地址上生成新地址
            int last=patPath.lastIndexOf("/");
            String newPath=patPath.substring(0,last)+ File.separator+"encImage";
            File encFile=new File(newPath);
            encFile.mkdirs();// 创建加密的目录，在原目录基础上
            // 加密
            SecretManageFactory secretManageFactory=new SecretManageFactory(Arithmetic.IMAGE,patPath);
            // 加密
            String newsolutePath=secretManageFactory.doEncryptManage();
            File file = new File(newsolutePath);
            Log.i("FileAction","--发送file？"+newsolutePath);
            IMMessage message = MessageBuilder.createFileMessage(getAccount(), getSessionType(), file, file.getName());
            sendMessage(message);
        }
    }

}
