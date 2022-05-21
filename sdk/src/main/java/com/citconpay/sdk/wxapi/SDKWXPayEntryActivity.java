package com.citconpay.sdk.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.citconpay.sdk.utils.Constant;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import sdk.CPaySDK;
import upisdk.CPayUPISDK;

public class SDKWXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String wxAppId = Constant.SystemType.equalsIgnoreCase("UPI") ?
                CPayUPISDK.getInstance().mWXAppId : CPaySDK.getInstance().mWXAppId;
        if (wxAppId != null) {
            api = WXAPIFactory.createWXAPI(this, wxAppId);
            api.handleIntent(getIntent(), this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e(TAG, req.toString());
    }

    @Override
    public void onResp(BaseResp resp) {
        final String orderID = getIntent().getStringExtra("_wxapi_payresp_extdata");
        final String errMsg;

        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        switch (resp.errCode){
            case 0:
                // success
                if (Constant.SystemType.equalsIgnoreCase("UPI"))
                    CPayUPISDK.getInstance().onWXPaySuccess(orderID);
                else
                    CPaySDK.getInstance().onWXPaySuccess(orderID);

                finish();
            case -1:
                errMsg = "sign error";
                break;
            case -2:
                errMsg = "user cancel";
                break;
            default:
                errMsg = "other error";
        }

        if (Constant.SystemType.equalsIgnoreCase("UPI"))
            CPayUPISDK.getInstance().onWXPayFailed(orderID, resp.errCode, errMsg);
        else
            CPaySDK.getInstance().onWXPayFailed(orderID, resp.errCode, errMsg);

        finish();
    }

}