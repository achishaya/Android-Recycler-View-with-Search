package test.com.recyclerviewtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    EditText editText;
    ArrayList<DataItemModel> dataItemModelArrayList = new ArrayList<>();
    MainAdapter adapter;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nestedScrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        editText = findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        adapter = new MainAdapter(MainActivity.this, dataItemModelArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getData(page);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getData(page);
                }
            }
        });

    }

    private void filter(String name) {
        ArrayList<DataItemModel> filteredList = new ArrayList<>();
        for(DataItemModel item : dataItemModelArrayList){
            if(item.getFirst_name().toLowerCase().contains(name.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void getData(int page) {
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl("https://reqres.in/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        MainInterface mainInterface = retrofit.create(MainInterface.class);
        Call<String> call = mainInterface.STRING_CALL(page);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null){
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonArray = new JSONObject(response.body());
                        Log.d("Response Array", jsonArray.toString());
                        parseResult(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void parseResult(JSONObject jsonArray) {
        for(int i=0; i<jsonArray.length(); i++){
            try {

                MainDataModel data = new MainDataModel();
                JSONArray challengeItemArray = jsonArray.getJSONArray("data");
                for (int j = 0; j < challengeItemArray.length(); j++) {
                    JSONObject challengeObject = challengeItemArray.getJSONObject(j);
                    DataItemModel dataItemModel = new DataItemModel();
                    dataItemModel.setAvatar(challengeObject.getString("avatar"));
                    dataItemModel.setFirst_name(challengeObject.getString("first_name"));
                    dataItemModelArrayList.add(dataItemModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new MainAdapter(MainActivity.this, dataItemModelArrayList);
            recyclerView.setAdapter(adapter);
        }
    }
}