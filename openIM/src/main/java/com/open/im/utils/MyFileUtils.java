package com.open.im.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.open.im.bean.ResultBean;
import com.open.im.receiver.ProgressListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 上传文件到服务器工具类，但是目前有问题，上传后返回的文件名都是以.jpg结尾
 *
 * @author Administrator
 */
public class MyFileUtils {

    /**
     * 让保存的图片能在图库中能找到
     *
     * @param path 图片的路径
     */
    public static void scanFileToPhotoAlbum(Context ctx, String path) {
        // 媒体扫描服务
        MediaScannerConnection.scanFile(ctx, new String[]{path}, null, new OnScanCompletedListener() {

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.i("lzh", "Finished scanning " + path);
            }
        });
    }

    /**
     * 方法 上传图片到新的服务器
     *
     * @param srcPath    图片本地路径
     * @param resolution 图片尺寸
     * @return
     */
    public static String uploadImage(String srcPath, String resolution, final ProgressDialog pd) {
        try {
            ResultBean resultBean = new ResultBean();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MyConstance.UPLOAD_IMAGE);
            File file = new File(srcPath);
            final long size = file.length();
            MultipartEntityBuilder entity = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(file);
            entity.addPart("file", fileBody);
            entity.addPart("resolution", new StringBody(resolution));
            entity.addPart("size", new StringBody(size + ""));

            HttpEntity httpEntity = entity.build();
            ProgressOutHttpEntity progressHttpEntity = new ProgressOutHttpEntity(
                    httpEntity, new ProgressListener() {
                @Override
                public void transferred(long transferredBytes) {
                    int progress = (int) (100 * transferredBytes / size);

                    if (progress < 100) {
                        pd.setProgress(progress);
                    } else if (progress >= 100) {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                    }

                    MyLog.showLog("上传进度::" + progress);
                }
            });

            httppost.setEntity(progressHttpEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String result = EntityUtils.toString(resEntity);
                resultBean = (ResultBean) resultBean.fromJson(result);
                String error = resultBean.getError();
                if (TextUtils.isEmpty(error)) {
                    MyLog.showLog("上传成功:" + resultBean);
                } else {
                    MyLog.showLog("上传失败");
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    return null;
                }
                resEntity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
            return resultBean.getResult();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传头像
     *
     * @param srcPath
     * @param resolution
     * @return
     */
    public static String uploadAvatar(String srcPath, String resolution) {
        try {
            ResultBean resultBean = new ResultBean();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MyConstance.UPLOAD_AVATAR);
            File file = new File(srcPath);
            long size = file.length();
            MultipartEntityBuilder entity = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(file);
            entity.addPart("file", fileBody);
            entity.addPart("resolution", new StringBody(resolution));
            entity.addPart("size", new StringBody(size + ""));
            httppost.setEntity(entity.build());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String result = EntityUtils.toString(resEntity);
                resultBean = (ResultBean) resultBean.fromJson(result);
                String error = resultBean.getError();
                if (TextUtils.isEmpty(error)) {
                    MyLog.showLog("上传成功:" + resultBean);
                } else {
                    MyLog.showLog("上传失败");
                    return null;
                }
                resEntity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
            return resultBean.getResult();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传普通长文本
     *
     * @param srcPath
     * @return
     */
    public static String uploadText(String srcPath) {
        try {
            ResultBean resultBean = new ResultBean();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MyConstance.UPLOAD_TEXT);
            File file = new File(srcPath);
            long size = file.length();
            MultipartEntityBuilder entity = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(file);
            entity.addPart("file", fileBody);
            entity.addPart("size", new StringBody(size + ""));
            httppost.setEntity(entity.build());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String result = EntityUtils.toString(resEntity);
                resultBean = (ResultBean) resultBean.fromJson(result);
                String error = resultBean.getError();
                if (TextUtils.isEmpty(error)) {
                    MyLog.showLog("上传成功:" + resultBean);
                } else {
                    MyLog.showLog("上传失败");
                    return null;
                }
                resEntity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
            return resultBean.getResult();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传语音
     *
     * @param srcPath
     * @param length  语音持续秒数
     * @return
     */
    public static String uploadVoice(String srcPath, long length) {
        try {
            ResultBean resultBean = new ResultBean();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MyConstance.UPLOAD_VOICE);
            File file = new File(srcPath);
            long size = file.length();
            MultipartEntityBuilder entity = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(file);
            entity.addPart("file", fileBody);
            entity.addPart("size", new StringBody(size + ""));
            entity.addPart("length", new StringBody(length + ""));
            httppost.setEntity(entity.build());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String result = EntityUtils.toString(resEntity);
                resultBean = (ResultBean) resultBean.fromJson(result);
                String error = resultBean.getError();
                if (TextUtils.isEmpty(error)) {
                    MyLog.showLog("上传成功:" + resultBean);
                } else {
                    MyLog.showLog("上传失败");
                    return null;
                }
                resEntity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
            return resultBean.getResult();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传位置
     *
     * @param srcPath
     * @param longitude
     * @param latitude
     * @param accuracy
     * @param description
     * @return
     */
    public static String uploadLocation(String srcPath, double longitude, double latitude, double accuracy, String description) {
        try {
            ResultBean resultBean = new ResultBean();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MyConstance.UPLOAD_LOCATION);
            File file = new File(srcPath);
            long size = file.length();
            MultipartEntityBuilder entity = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(file);
            entity.addPart("file", fileBody);
            entity.addPart("size", new StringBody(size + ""));
            entity.addPart("longitude", new StringBody(longitude + ""));
            entity.addPart("latitude", new StringBody(latitude + ""));
            entity.addPart("accuracy", new StringBody(accuracy + ""));
            entity.addPart("description", new StringBody(description));
            httppost.setEntity(entity.build());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String result = EntityUtils.toString(resEntity);
                resultBean = (ResultBean) resultBean.fromJson(result);
                String error = resultBean.getError();
                if (TextUtils.isEmpty(error)) {
                    MyLog.showLog("上传成功:" + resultBean);
                } else {
                    MyLog.showLog("上传失败");
                    return null;
                }
                resEntity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
            return resultBean.getResult();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 递归删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        //判断给定的是否是目录
        if (file.isDirectory()) {
            //得到这个目录下的所有文件和目录
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    //如果是文件删除
                    f.delete();
                    MyLog.showLog("删除::" + f.getName());
                } else {
                    //说明是个目录
                    //递归删除其所有子文件
                    deleteFile(f);
                }
            }
            //foreach结束后
            //目录中的所有文件已经全部删除，所以将目录删掉
            file.delete();
        }
    }

//    /**
//     * 根据文件获取字节数组
//     *
//     * @param file
//     * @return
//     * @throws IOException
//     */
//    private static byte[] getFileBytes(File file) throws IOException {
//        BufferedInputStream bis = null;
//        try {
//            bis = new BufferedInputStream(new FileInputStream(file));
//            int bytes = (int) file.length();
//            byte[] buffer = new byte[bytes];
//            int readBytes = bis.read(buffer);
//            if (readBytes != buffer.length) {
//                throw new IOException("Entire file not read");
//            }
//            return buffer;
//        } finally {
//            if (bis != null) {
//                bis.close();
//            }
//        }
//    }
}
