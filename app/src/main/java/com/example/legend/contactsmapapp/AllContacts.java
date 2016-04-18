package com.example.legend.contactsmapapp;

/**
 * Created by legend on 16/4/16.
 */
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

 import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class AllContacts extends Fragment {
    TextView output;
    String dat="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final String[] out = new String[1];

        View view = inflater.inflate(R.layout.allcontacts, container, false);
        String loginURL = "http://private-b08d8d-nikitest.apiary-mock.com/contacts";
        final String[] data = {""};



        RequestQueue requestQueue;



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
                        if (!contactExists(getActivity(),phone))
                        {
                            WritePhoneContact(name,phone,getActivity());
                        }

                        double latitude = jsonObject.getDouble("latitude");
                        double longtitude = jsonObject.getDouble("longitude");
                        String email = jsonObject.getString("email");

                         out[0] ="";

                        dat += "Name:" + name + " \n Phone= " + phone + " \nOffice= " + office +" \nEmail= " + email + " \n\n\n\n ";
                        output.setText(dat);

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



    public void WritePhoneContact(String displayName, String number,Context cntx /*App or Activity Ctx*/)
    {
        Context contetx = cntx;
         //Application's context or Activity's context
        String strDisplayName =  displayName; // Name of the Person to add
        String strNumber =  number; //number of the person to add with the Contact
        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strNumber) // Number to be added
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
        try
        {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = contetx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
        }
        catch (RemoteException exp)
        {
            //logs;
        }
        catch (OperationApplicationException exp)
        {
            //logs
        }


    }
    public boolean contactExists(Context context, String number) {
/// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }


}

