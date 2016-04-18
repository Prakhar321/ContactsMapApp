package com.example.legend.contactsmapapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by legend on 16/4/16.
 */
public class ContactsMap extends Fragment implements GoogleMap.OnMarkerClickListener{

    TextView out;
    GoogleMap map;
    TextView output;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contactsmap, container, false);
        String loginURL = "http://private-b08d8d-nikitest.apiary-mock.com/contacts";
        final String[] data = {""};


        RequestQueue requestQueue;
        if (map == null) {
            map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();                //map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            //Service1=(TextView)view.findViewById(R.id.title);
            //Service1.setOnClickListener(new View.OnClickListener() {
            //  @Override
            //public void onClick(View view) {
            //  Intent i=new Intent(getActivity(), Service1.class);
            //startActivity(i);
            // }
            // });
        }


        requestQueue = Volley.newRequestQueue(getActivity());


        output = (TextView) view.findViewById(R.id.output);

        JsonArrayRequest jor = new JsonArrayRequest(Request.Method.GET, loginURL, (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //  ProgressDialog p=new ProgressDialog(MainActivity.this);
                //p.setTitle("loading");
                //p.show();
                try {
                    JSONObject ja = response.getJSONObject(0);


                    JSONArray ar = ja.getJSONArray("contacts");

                    for (int i = 0; i < ar.length(); i++) {
                        JSONObject jsonObject = ar.getJSONObject(i);


                        // int id = Integer.parseInt(jsonObject.optString("id").toString());
                        String name = null;

                        name = jsonObject.getString("name");

                        String phone = null;
                        String office = null;

                        phone = jsonObject.getString("phone");
                        office = jsonObject.getString("officePhone");

                        double latitude = jsonObject.getDouble("latitude");
                        double longtitude = jsonObject.getDouble("longitude");
                        String email = jsonObject.getString("email");


                      map.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)).title(name).snippet("Phn:" + phone) );


                        //data[0] += "Blog Number " + (i + 1) + " \n Blog Name= " + title + " \n URL= " + url + " \n\n\n\n ";
                    }
                    // p.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();


                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.e("Volley", "Error");

            }
        });
        jor.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jor);


        return view;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        Toast.makeText(getActivity(),
                "Marker Clicked: " + marker.getTitle(), Toast.LENGTH_LONG)
                .show();
        return false;

    }
}






