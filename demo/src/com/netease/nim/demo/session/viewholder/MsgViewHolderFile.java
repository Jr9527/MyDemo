package com.netease.nim.demo.session.viewholder;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.file.FileIcons;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.file.FileUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;

import java.io.File;

import rsa.Arithmetic;
import rsa.FilesUtils;
import rsa.SecretManageFactory;

/**
 * Created by zhoujianghua on 2015/8/6.
 */
public class MsgViewHolderFile extends MsgViewHolderBase {

    private File f;
    private ImageView fileIcon;
    private TextView fileNameLabel;
    private TextView fileStatusLabel;
    private ProgressBar progressBar;

    private FileAttachment msgAttachment;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_file;
    }

    @Override
    protected void inflateContentView() {
        fileIcon = (ImageView) view.findViewById(R.id.message_item_file_icon_image);
        fileNameLabel = (TextView)view.findViewById(R.id.message_item_file_name_label);
        fileStatusLabel = (TextView)view.findViewById(R.id.message_item_file_status_label);
        fileStatusLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileAttachment fileAttachment = (FileAttachment) message.getAttachment();
                String path=fileAttachment.getPath();
                if(TextUtils.isEmpty(path)){
                    NIMClient.getService(MsgService.class).downloadAttachment(message, true).setCallback(
                            new RequestCallback() {
                                @Override
                                public void onSuccess(Object o) {
                                    Log.i("MsgViewHolderFile", "--点击将要下载的file.成功？");
                                    String path=((FileAttachment) message.getAttachment()).getPath();
                                    FileAttachment f=(FileAttachment) message.getAttachment();
                                    String displayName= f.getDisplayName();
                                    Log.i("MsgViewHolderFile", "--将要解密的"+path+"displayName:"+displayName);
                                    // 在这里解密
                                    File file=new File(path);
                                    FileAsync fileAsync=new FileAsync();
                                    fileAsync.execute(file);
                                }

                                @Override
                                public void onFailed(int i) {

                                }

                                @Override
                                public void onException(Throwable throwable) {

                                }
                            }


                    );

                }
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.message_item_file_transfer_progress_bar);
    }




    @Override
    protected void bindContentView() {
        msgAttachment = (FileAttachment) message.getAttachment();
        String path = msgAttachment.getPath();
        initDisplay();

        if (!TextUtils.isEmpty(path)) {
            loadImageView();
            LogUtil.i("MsgViewHolderFile","--传输成功");
            File file=new File(path);
            FileAsync fileAsync=new FileAsync();
            fileAsync.execute(file);
        } else {
            AttachStatusEnum status = message.getAttachStatus();
            switch (status) {
            case def:
                updateFileStatusLabel();
                break;
            case transferring:
                fileStatusLabel.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                int percent = (int) (getAdapter().getProgress (message) * 100);
                progressBar.setProgress(percent);
                break;
            case transferred:
                // 传输成功？
                break;
            case fail:
                updateFileStatusLabel();
                break;
            }
        }
    }

    private void loadImageView() {
        fileStatusLabel.setVisibility(View.VISIBLE);
        // 文件长度
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.formatFileSize(msgAttachment.getSize()));
        fileStatusLabel.setText(sb.toString());

        progressBar.setVisibility(View.GONE);
    }

    private void initDisplay() {
        int iconResId = FileIcons.smallIcon(msgAttachment.getDisplayName());
        fileIcon.setImageResource(iconResId);
        fileNameLabel.setText(msgAttachment.getDisplayName());
    }

    private void updateFileStatusLabel() {
        fileStatusLabel.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        // 文件长度
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.formatFileSize(msgAttachment.getSize()));
        sb.append("  ");
        // 下载状态
        String path = msgAttachment.getPathForSave();
        if (AttachmentStore.isFileExist(path)) {
            sb.append(context.getString(R.string.file_transfer_state_downloaded));
        } else {
            sb.append(context.getString(R.string.file_transfer_state_undownload));
        }
        fileStatusLabel.setText(sb.toString());
        fileStatusLabel.setTextColor(DemoCache.getContext().getResources().getColor(R.color.color_red_ccfa3c55));
    }

    @Override
    protected int leftBackground() {
        return R.drawable.nim_message_left_white_bg;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.nim_message_right_blue_bg;
    }

    class FileAsync extends AsyncTask<File,Integer,Boolean> {

        SecretManageFactory secretManageFactory;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(File... params) {
            boolean dec_ok=false;
            f=params[0];
            secretManageFactory=new SecretManageFactory(Arithmetic.IMAGE,f);
            dec_ok= (boolean)secretManageFactory.doDecode();// 解密
            LogUtil.i("MsgViewHolderFile","--path?？"+f.getAbsolutePath());
            return dec_ok;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
               // 解密成功
                LogUtil.i("MsgViewHolderFile","--file文件解密成功啦？");

            }else{
                // 失败
                LogUtil.i("MsgViewHolderFile","--file文件解密失败啦？");
            }
        }
    }


}
