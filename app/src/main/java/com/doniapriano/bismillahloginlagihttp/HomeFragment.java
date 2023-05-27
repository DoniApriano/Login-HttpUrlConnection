package com.doniapriano.bismillahloginlagihttp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterData adapterData;
    LocalStorage localStorage;
    List<DataModel> listDataModel = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_data);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        new FetchData().execute();
    }

    public class FetchData extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        final String URL_STRING = getResources().getString(R.string.api_server) + "/Book";
        String response;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Harap Menunggu......");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            response = creatingUrlConnection(URL_STRING);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Terkoneksi", Toast.LENGTH_SHORT).show();
            try {
                if (response != null && !response.equals("")) {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            DataModel dataModel = new DataModel();
                            dataModel.setName(jsonObject.optString("name"));
                            dataModel.setAuthors(jsonObject.optString("authors"));
                            listDataModel.add(dataModel);
                        }
                        adapterData = new AdapterData(getActivity(),listDataModel);
                        recyclerView.setAdapter(adapterData);
                        adapterData.notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(getActivity(),
                            "Error in fetching data.",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String creatingUrlConnection(String url_string) {
        String response = "";
        HttpURLConnection conn;
        StringBuilder stringBuilder = new StringBuilder();
        localStorage = new LocalStorage(getActivity());
        try {
            URL url = new URL(url_string);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + localStorage.getToken());
            conn.setRequestProperty("Accept","*");
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);
            conn.connect();
            int statusCode = conn.getResponseCode();
            System.out.println(statusCode);
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1) {
                stringBuilder.append(buff,0,read);
            }
            response = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}