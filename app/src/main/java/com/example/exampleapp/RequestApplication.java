package com.example.exampleapp;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/*
*Application 클래스
 -액티비티 간 어떠한 클래스든 공유할 수 있고, Application 객체의 멤버는 프로세스 어디에서나 참조가 가능함
 -앱의 컴포넌트들을 공동으로 사용할 수 있기 때문에 공통되는 내용을 작성해주면 어디서는 context 객체를 이용한 접근이 가능함
 -공통으로 전역 변수를 사용하고 싶을 때 Application 클래스를 상속받아 사용 가능함
 */

public class RequestApplication extends Application {
    private static final String TAG = "RequestApplication";

    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        if(requestQueue != null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);

                    return  connection;
                }
            });
        }
    }

    @Override
    public void onTerminate() { // 애플리케이션 객체와 모든 컴포넌트가 종료될 때 호출돰
        super.onTerminate();
    }

    public static interface OnResponseListener {
        public void processResponse(int requestCode, int responseCode, String response);
    }

    public static void send(final int requestCode, final int requestMethod, final String url, final Map<String,String> params,
                            final OnResponseListener listener) {

        // 문자열을 주고 받기 위한 요청 객체
        StringRequest stringRequest = new StringRequest(requestMethod, url,
                new Response.Listener<String>() { // 응답을 문자열로 받아서 여기에 넣음
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, requestCode + "의 응답 : " + response);

                        if (listener != null) {
                            listener.processResponse(requestCode, 200, response);
                        }
                    }
                },
                new Response.ErrorListener() { // 에러 발생 시 호출됨
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, requestCode + "의 에러 : " + error.getMessage());

                        if (listener != null) {
                            listener.processResponse(requestCode, 400, error.getMessage());
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

        };
        stringRequest.setShouldCache(false); // 이전 응답 결과를 사용하지 않겠다면 캐시를 사용하지 않도록 false
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (RequestApplication.requestQueue != null) {
            RequestApplication  .requestQueue.add(stringRequest);
            Log.d(TAG, "Request sent : " + requestCode);
            Log.d(TAG, "Request url : " + url);
        } else {
            Log.d(TAG, "Request queue is null.");
        }
    }
}
