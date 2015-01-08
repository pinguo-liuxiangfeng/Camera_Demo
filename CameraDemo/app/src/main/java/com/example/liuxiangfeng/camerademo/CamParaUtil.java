package com.example.liuxiangfeng.camerademo;

import android.hardware.Camera;
import android.util.Log;
import android.util.Size;

import java.util.List;

/**
 * Created by liuxiangfeng on 15-1-7.
 */
public class CamParaUtil {
    private static final String TAG = "CamParaUtil";
//    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CamParaUtil myCamPara = null;
    private CamParaUtil(){

    }
    public static CamParaUtil getInstance(){
        if(myCamPara == null){
            myCamPara = new CamParaUtil();
            return myCamPara;
        }
        else{
            return myCamPara;
        }
    }
//    public  Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){
//        Collections.sort(list, sizeComparator);
//
//        int i = 0;
//        for(Size s:list){
//            if((s.width >= minWidth) && equalRate(s, th)){
//                Log.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
//                break;
//            }
//            i++;
//        }
//        if(i == list.size()){
//            i = 0;//如果没找到，就选最小的size
//        }
//        return list.get(i);
//    }
//    public Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
//        Collections.sort(list, sizeComparator);
//
//        int i = 0;
//        for(Size s:list){
//            if((s.width >= minWidth) && equalRate(s, th)){
//                Log.i(TAG, "PictureSize : w = " + s.width + "h = " + s.height);
//                break;
//            }
//            i++;
//        }
//        if(i == list.size()){
//            i = 0;//如果没找到，就选最小的size
//        }
//        return list.get(i);
//    }

    /**打印支持的previewSizes
     * @param params
     */
    public  void printSupportPreviewSize(Camera.Parameters params){
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        for(int i=0; i< previewSizes.size(); i++){
            Camera.Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = " + size.width + " height = " + size.height);
        }

    }

    /**打印支持的pictureSizes
     * @param params
     */
    public  void printSupportPictureSize(Camera.Parameters params){
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        for(int i=0; i< pictureSizes.size(); i++){
            Camera.Size size = pictureSizes.get(i);
            Log.i(TAG, "pictureSizes:width = "+ size.width
                    +" height = " + size.height);
        }
    }
    /**打印支持的聚焦模式
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params){
        List<String> focusModes = params.getSupportedFocusModes();
        for(String mode : focusModes){
            Log.i(TAG, "focusModes--" + mode);
        }
    }
}
