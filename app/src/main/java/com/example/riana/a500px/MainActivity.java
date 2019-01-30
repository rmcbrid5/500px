package com.example.riana.a500px;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ActionBar actionBar;
    private ImageAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String FEED_URL;
    private Button nextBtn, prevBtn;
    private int currentPage=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nextBtn = findViewById(R.id.next);
        prevBtn = findViewById(R.id.prev);
        prevBtn.setEnabled(false);
        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new ImageAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        //set on click listener for the "next" button
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage += 1;
                mGridData.clear();
                new AsyncHttpTask().execute("https://api.500px.com/v1/photos?feature=popular&image_size=2,2048&rpp=100&page="+currentPage +
                        "&consumer_key="+BuildConfig.ApiKey);
                prevBtn.setEnabled(true);
            }
        });

        //set on click listener for the "prev" button
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage -= 1;
                mGridData.clear();
                new AsyncHttpTask().execute("https://api.500px.com/v1/photos?feature=popular&image_size=2,2048&rpp=100&page="+currentPage +
                        "&consumer_key="+BuildConfig.ApiKey);
                if(currentPage == 1){
                    prevBtn.setEnabled(false);
                }
            }
        });
        //get the full path of the API call
        FEED_URL = "https://api.500px.com/v1/photos?feature=popular&image_size=2,2048&rpp=100&page="+currentPage +
                "&consumer_key="+BuildConfig.ApiKey;
        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent.putExtra("left", screenLocation[0]).
                        putExtra("top", screenLocation[1]).
                        putExtra("width", imageView.getWidth()).
                        putExtra("height", imageView.getHeight()).
                        putExtra("title", item.getTitle()).
                        putExtra("image", item.getLargeImage()).
                        putExtra("user", item.getUser()).
                        putExtra("rating", item.getRating()).
                        putExtra("description", item.getDescription());

                //Start details activity
                startActivity(intent);
            }
        });

        //Start download
        new AsyncHttpTask().execute(FEED_URL);
        mProgressBar.setVisibility(View.VISIBLE);
    }


    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    Log.d(TAG, String.valueOf(statusCode));
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI

            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(MainActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

            //Hide progressbar
            mProgressBar.setVisibility(View.GONE);
        }
    }


    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("photos");
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("name");
                item = new GridItem();
                item.setTitle(title);
                JSONArray attachments = post.getJSONArray("images");
                if (null != attachments && attachments.length() > 0) {
                    JSONObject attachment1 = attachments.getJSONObject(0);
                    if (attachment1 != null)
                        item.setSmallImage(attachment1.getString("https_url"));
                    JSONObject attachment2 = attachments.getJSONObject(1);
                    if (attachment2 != null)
                        item.setLargeImage(attachment2.getString("https_url"));
                }
                JSONObject user = post.getJSONObject("user");
                String userName = user.optString("fullname");
                item.setUser(userName);
                Double rating = post.optDouble("highest_rating");
                Log.d("TEST", String.valueOf(rating));
                item.setRating(rating);
                String description = post.optString("description");
                if(description==null||description.isEmpty()){
                    item.setDescription(" ");
                }
                else{
                    item.setDescription(description);
                }
                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
