package com.cordova.plugins.push.baidu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;

import com.baidu.android.pushservice.PushMessageReceiver;







public static final String manufacturer = android.os.Build.MANUFACTURER;
try {
      Intent intent = new Intent();
      if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
      } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
      } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
      } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
      } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
      }

      List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
      if (list.size() > 0) {
                startActivity(intent);
      }

} catch (Exception e) {
      e.printStackTrace();
}






/*
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 *onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 *onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 返回值中的errorCode，解释如下：
 *0 - Success
 *10001 - Network Problem
 *10101  Integrate Check Error
 *30600 - Internal Server Error
 *30601 - Method Not Allowed
 *30602 - Request Params Not Valid
 *30603 - Authentication Failed
 *30604 - Quota Use Up Payment Required
 *30605 -Data Required Not Found
 *30606 - Request Time Expires Timeout
 *30607 - Channel Token Timeout
 *30608 - Bind Relation Not Found
 *30609 - Bind Number Too Many
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 *
 */

public class BaiduPushReceiver extends PushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = BaiduPushReceiver.class
            .getSimpleName();
    /** 回调类型 */
    private enum CB_TYPE {
    	onBind,
    	onUnbind,
    	onSetTags,
    	onDelTags,
    	onListTags,
    	onMessage,
    	onNotificationClicked,
    	onNotificationArrived
    };


    private static ArrayList<PluginResult> queuePushCallbackContext = new ArrayList<PluginResult>();
    private static ArrayList<PluginResult> queueOnMessageCallbackContext = new ArrayList<PluginResult>();
    private static ArrayList<PluginResult> queueOnNotificationClickedCallbackContext = new ArrayList<PluginResult>();
    private static ArrayList<PluginResult> queueOnNotificationArrivedCallbackContext = new ArrayList<PluginResult>();

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appId,
            String userId, String channelId, String requestId) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            if (errorCode == 0) {
                setStringData(data, "appId", appId);
                setStringData(data, "userId", userId);
                setStringData(data, "channelId", channelId);
                jsonObject.put("data", data);
                jsonObject.put("type", CB_TYPE.onBind);
                sendSuccessData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.d(TAG, jsonObject.toString());
            }else{
                setStringData(data, "errorCode", "透传消息为空");
                jsonObject.put("code", errorCode);
                sendErrorData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.e(TAG, "绑定失败");
                Log.e(TAG, "errorCode: " + errorCode);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    /**
     * 接收透传消息的函数。
     *
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String title, String customContentString) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            //if (!TextUtils.isEmpty(customContentString)) {
            if (!TextUtils.isEmpty(title)) {
                setStringData(data, "title", title);
                setStringData(data, "customContentString", customContentString);
                jsonObject.put("data", data);
                jsonObject.put("type", CB_TYPE.onMessage);
                
                
              Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.phonegap.helloworld");
                    if (intent != null) {
                        // We found the activity now start the activity
                         /*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          intent.putExtra("cdvStartInBackground", true);
                          intent.putExtra("foreground", false);*/
                        
                        
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                        
                        context.startActivity(intent);
                    }
                
              /*  Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.phonegap.helloworld");
                    if (intent != null) {
                        // We found the activity now start the activity
                         /*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          intent.putExtra("cdvStartInBackground", true);
                          intent.putExtra("foreground", false);*/
                        
                        /*
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                        intent.putExtra("cdvStartInBackground", startOnBackground);
                        
                        
                        
                        context.startActivity(intent);
                    }
*/
                
                
                
                
                
                
                sendSuccessData(queueOnMessageCallbackContext, BaiduPush.onMessageCallbackContext, jsonObject, true);
                Log.d(TAG, jsonObject.toString());
            }else{
                setStringData(data, "errorCode", "透传消息为空");
                sendErrorData(queueOnMessageCallbackContext, BaiduPush.onMessageCallbackContext, jsonObject, true);
                Log.e(TAG, "透传消息为空");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 接收通知点击的函数。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            if (!TextUtils.isEmpty(title)) {
                setStringData(data, "title", title);
                setStringData(data, "description", description);
                setStringData(data, "customContentString", customContentString);
                jsonObject.put("data", data);
                jsonObject.put("type", CB_TYPE.onNotificationClicked);
                sendSuccessData(queueOnNotificationClickedCallbackContext, BaiduPush.onNotificationClickedCallbackContext, jsonObject, true);
                Log.d(TAG, jsonObject.toString());
            }else{
                setStringData(data, "errorCode", "推送的通知点击内容为空");
                sendErrorData(queueOnNotificationClickedCallbackContext, BaiduPush.onNotificationClickedCallbackContext, jsonObject, true);
                Log.e(TAG, "推送的通知点击内容为空");
            }
        } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 接收通知到达的函数。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */

    @Override
    public void onNotificationArrived(Context context, String title,
            String description, String customContentString) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            if (!TextUtils.isEmpty(title)) {
                
                /*
                
                if(customContentString.toLowerCase().contains("openapp")){
                    
                    setStringData(data, "title", title);
                    setStringData(data, "description", "openapp"+description);
                    setStringData(data, "customContentString", customContentString);
                    jsonObject.put("data", data);
                    jsonObject.put("type", CB_TYPE.onNotificationArrived);

                    Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.phonegap.helloworld");
                    if (intent != null) {
                        // We found the activity now start the activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    
                }else{
                    
                    setStringData(data, "title", title);
                    setStringData(data, "description", "abc"+description);
                    setStringData(data, "customContentString", customContentString);
                    jsonObject.put("data", data);
                    jsonObject.put("type", CB_TYPE.onNotificationArrived);
                    
                };*/
                
                setStringData(data, "title", title);
                    setStringData(data, "description", "abc"+description);
                    setStringData(data, "customContentString", customContentString);
                    jsonObject.put("data", data);
                    jsonObject.put("type", CB_TYPE.onNotificationArrived);
                
                
                
                /*custom start*/
                /**custom start*/
                /*
             Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.phonegap.helloworld");
             if (launchIntent != null) { 
                 //startActivity(launchIntent);//null pointer check in case package name was not found
                 
                 context.startActivity(launchIntent);
             }*/
             
             /*
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setClass(context, MainActivity.class);
                intent.putExtra("cdvStartInBackground", true);
                intent.putExtra("foreground", false);
                context.startActivity(intent);
                Log.d(TAG, "test123");*/
                
                
                /*custom end*/


                
                
                
                sendSuccessData(queueOnNotificationArrivedCallbackContext, BaiduPush.onNotificationArrivedCallbackContext, jsonObject, true);
                Log.d(TAG, jsonObject.toString());
                
                
                
                
                
                
            }else{
                setStringData(data, "errorCode", "推送的通知内容为空");
                sendErrorData(queueOnNotificationArrivedCallbackContext, BaiduPush.onNotificationArrivedCallbackContext, jsonObject, true);
                Log.e(TAG, "推送的通知内容为空");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * setTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            if (errorCode == 0) {
                setArrayData(data, "sucessTags", sucessTags);
                setArrayData(data, "failTags", failTags);
                jsonObject.put("data", data);
                jsonObject.put("type", CB_TYPE.onSetTags);
                sendSuccessData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.d(TAG, jsonObject.toString());
            }else{
                setStringData(data, "errorCode", "" + errorCode);
                sendErrorData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.e(TAG, "设置Tag失败");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * delTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            if (errorCode == 0) {
                setArrayData(data, "sucessTags", sucessTags);
                setArrayData(data, "failTags", failTags);
                jsonObject.put("data", data);
                jsonObject.put("type", CB_TYPE.onDelTags);
                sendSuccessData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.d(TAG, jsonObject.toString());
            } else {
                setStringData(data, "errorCode", "" + errorCode);
                sendErrorData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.e(TAG, "设置Tag失败");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * listTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            if (errorCode == 0) {
                setArrayData(data, "tags", tags);
                jsonObject.put("data", data);
                jsonObject.put("type", CB_TYPE.onListTags);
                sendSuccessData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.d(TAG, jsonObject.toString());
            } else {
                setStringData(data, "errorCode", "" + errorCode);
                sendErrorData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.e(TAG, "listTags失败");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * PushManager.stopWork() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            if (errorCode == 0) {
                setStringData(data, "requestId", requestId);
                jsonObject.put("data", data);
                jsonObject.put("type", CB_TYPE.onUnbind);
                sendSuccessData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.d(TAG, jsonObject.toString());
            } else {
                setStringData(data, "errorCode", "" + errorCode);
                sendErrorData(queuePushCallbackContext, BaiduPush.pushCallbackContext, jsonObject, false);
                Log.e(TAG, "解绑定失败");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 接收推送成功内容并返回给前端JS
     *
     * @param jsonObject JSON对象
     */
    private void sendSuccessData(ArrayList<PluginResult> queue, CallbackContext callbackContext, JSONObject jsonObject, boolean isCallBackKeep) {
        Log.d(TAG, "BaiduPushReceiver#sendSuccessData: " + (jsonObject != null ? jsonObject.toString() : "null"));

        PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
        result.setKeepCallback(isCallBackKeep);
        sendResultWithQueue(queue, callbackContext, result);
    }

    /**
     * 接收推送失败内容并返回给前端JS
     *
     * @param jsonObject JSON对象
     */
    private void sendErrorData(ArrayList<PluginResult> queue, CallbackContext callbackContext, JSONObject jsonObject, boolean isCallBackKeep) {
        Log.d(TAG, "BaiduPushReceiver#sendErrorData: " + (jsonObject != null ? jsonObject.toString() : "null"));

        PluginResult result = new PluginResult(PluginResult.Status.ERROR, jsonObject);
        result.setKeepCallback(false);
        sendResultWithQueue(queue, callbackContext, result);
    }

    private void sendResultWithQueue(ArrayList<PluginResult> queue, CallbackContext callbackContext, PluginResult result) {
        if (callbackContext == null) {
            Log.d(TAG, "BaiduPushReceiver#sendResultWithQueue: callbackContext IS NULL");
            queue.add(result);
        }
        else {
            callbackContext.sendPluginResult(result);
            sendQueue(queuePushCallbackContext, BaiduPush.pushCallbackContext);
            sendQueue(queueOnNotificationArrivedCallbackContext, BaiduPush.onNotificationArrivedCallbackContext);
            sendQueue(queueOnMessageCallbackContext, BaiduPush.onMessageCallbackContext);
            sendQueue(queueOnNotificationClickedCallbackContext, BaiduPush.onNotificationClickedCallbackContext);
        }
    }

    private void sendQueue(ArrayList<PluginResult> queue, CallbackContext callbackContext) {
        if (callbackContext != null) {
            for (PluginResult result : queue) {
                callbackContext.sendPluginResult(result);
            }
            queue.clear();
        }
    }

    /**
     * 设定字符串类型JSON对象，如值为空时不设定
     *
     * @param jsonObject JSON对象
     * @param name 关键字
     * @param value 值
     * @throws JSONException JSON异常
     */
    private void setStringData(JSONObject jsonObject, String name, String value) throws JSONException {
    	if (value != null && !"".equals(value)) {
    		jsonObject.put(name, value);
    	}
    }

    /**
     * 设定Array类型JSON对象，如值为空时不设定
     *
     * @param jsonObject JSON对象
     * @param name 关键字
     * @param value 值
     * @throws JSONException JSON异常
     */
    private void setArrayData(JSONObject jsonObject, String name, List<String> value) throws JSONException {
    	if (value != null) {
    		jsonObject.put(name, new JSONArray(value));
    	}
    }
}
