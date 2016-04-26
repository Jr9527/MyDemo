package com.netease.nim.uikit.session.helper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo;
import com.netease.nim.uikit.common.media.picker.model.PickerContract;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.file.FileUtil;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nim.uikit.session.constant.Extras;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rsa.Arithmetic;
import rsa.SecretManageFactory;

public class SendImageHelper {
	public interface Callback {
		void sendImage(File file, boolean isOrig);
    }

    public static void sendImageAfterPreviewPhotoActivityResult(Intent data, Callback callback) {
        final ArrayList<String> selectedImageFileList = data.getStringArrayListExtra(Extras.EXTRA_SCALED_IMAGE_LIST);
        final ArrayList<String> origSelectedImageFileList = data.getStringArrayListExtra(Extras.EXTRA_ORIG_IMAGE_LIST);

        boolean isOrig = data.getBooleanExtra(Extras.EXTRA_IS_ORIGINAL, false);
        for (int i = 0; i < selectedImageFileList.size(); i++) {
            String imageFilepath = selectedImageFileList.get(i);

				// 打个log
			Log.i("SendImageHelper","--读取图片？"+imageFilepath);
			String origImageFilePath = origSelectedImageFileList.get(i);
			// 在这偷换地址,把加密图片处理
			String patPath=imageFilepath;// 在原始地址上生成新地址
			/*int last=patPath.lastIndexOf("/");
			String newPath=patPath.substring(0,last)+ File.separator+"encImage";
			File encFile=new File(newPath);
			encFile.mkdirs();// 创建加密的目录，在原目录基础上*/
			// 加密
			SecretManageFactory secretManageFactory=new SecretManageFactory(Arithmetic.IMAGE,patPath);
			// 加密
			String newsolutePath=secretManageFactory.doEncryptManage();
			File imageFile = new File(newsolutePath);
			Log.i("SendImageHelper","--发送图片？"+newsolutePath);

            if (isOrig) {
                // 把原图按md5存放
                String origMD5 = MD5.getStreamMD5(origImageFilePath);
                String extension = FileUtil.getExtensionName(origImageFilePath);
                String origMD5Path = StorageUtil.getWritePath(origMD5 + "" + extension,
                        StorageType.TYPE_IMAGE);
                AttachmentStore.copy(origImageFilePath, origMD5Path);

                // 把缩略图移到按原图计算的新md5目录下
                String thumbFilename = FileUtil.getFileNameFromPath(imageFilepath);
                String thumbMD5Path = StorageUtil.getReadPath(thumbFilename,
                        StorageType.TYPE_THUMB_IMAGE);
                String origThumbMD5Path = StorageUtil.getWritePath(origMD5 + "" + extension,
                        StorageType.TYPE_THUMB_IMAGE);
                AttachmentStore.move(thumbMD5Path, origThumbMD5Path);

                if (callback != null) {
                    callback.sendImage(new File(origMD5Path), isOrig);
                }
            } else {
                if (callback != null) {
                    callback.sendImage(imageFile, isOrig);
                }
            }
        }
    }

	public static void sendImageAfterSelfImagePicker(Context context, Intent data, final Callback callback){		
		boolean isOrig = data.getBooleanExtra(Extras.EXTRA_IS_ORIGINAL, false);

		List<PhotoInfo> photos = PickerContract.getPhotos(data);
		if(photos == null) {
			Toast.makeText(context, R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return;
		}
		
		for (PhotoInfo photoInfo : photos) {
			new SendImageTask(context, isOrig, photoInfo, new Callback() {
				
				@Override
				public void sendImage(File file, boolean isOrig) {
					if (callback != null) {
						callback.sendImage(file, isOrig);
					}
				}
			}).execute();			
		}		
	}
	
	// 从相册选择图片进行发送(Added by NYB)
	public static class SendImageTask extends AsyncTask<Void, Void, File> {

		private Context context;
		private boolean isOrig;
		private PhotoInfo info;
		private Callback callback;

		public SendImageTask(Context context, boolean isOrig, PhotoInfo info,
				Callback callback) {
			this.context = context;
			this.isOrig = isOrig;
			this.info = info;
			this.callback = callback;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected File doInBackground(Void... params) {
			String photoPath = info.getAbsolutePath();
			if (TextUtils.isEmpty(photoPath))
				return null;

			if (isOrig) {
				// 把原图按md5存放
				String origMD5 = MD5.getStreamMD5(photoPath);
				String extension = FileUtil.getExtensionName(photoPath);
				String origMD5Path = StorageUtil.getWritePath(origMD5 + ""
						+ extension, StorageType.TYPE_IMAGE);
				AttachmentStore.copy(photoPath, origMD5Path);
				// 生成缩略图
				File imageFile = new File(origMD5Path);
				ImageUtil.makeThumbnail(context, imageFile);

				return new File(origMD5Path);
			} else {
				File imageFile = new File(photoPath);
				String mimeType = FileUtil.getExtensionName(photoPath);
				imageFile = ImageUtil.getScaledImageFileWithMD5(imageFile, mimeType);
				if (imageFile == null) {
					new Handler(context.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, R.string.picker_image_error, Toast.LENGTH_LONG).show();
						}
					});
					return null;
				} else {
					ImageUtil.makeThumbnail(context, imageFile);
				}
				// 在这偷换地址,把加密图片处理
				String patPath=imageFile.getAbsolutePath();// 获取地址
				SecretManageFactory secretManageFactory=new SecretManageFactory(Arithmetic.IMAGE,patPath);
				// 加密
				String newsolutePath=secretManageFactory.doEncryptManage();
				// 将地址换取
				File newencimgfile=new File(newsolutePath);
				return newencimgfile;
			}
		}

		@Override
		protected void onPostExecute(File result) {
			super.onPostExecute(result);

			if (result != null) {
				if (callback != null) {
					String imageFilepath = result.getAbsolutePath();
					String md5 = FileUtil.getFileNameNoEx(FileUtil.getFileNameFromPath(imageFilepath));

					if (callback != null) {
						callback.sendImage(result, isOrig);
					}
				}
			}
		}
	}
}
