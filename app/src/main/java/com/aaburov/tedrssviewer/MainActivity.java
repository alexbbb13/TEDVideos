package com.aaburov.tedrssviewer;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private ArrayList<RSSPostData> listData;
    private PostItemAdapter itemAdapter;
    private Activity mActivity = this;
    private static final String URL="http://www.ted.com/talks/rss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) this.findViewById(R.id.lvRSSPosts);
        listData = new ArrayList<RSSPostData>();
        new RSSDataController().execute(URL);
        itemAdapter = new PostItemAdapter(this, R.layout.rss_item, listData);
        listView.setAdapter(itemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intentPlayVideo = new Intent(mActivity,VideoViewActivity.class);
                intentPlayVideo.putExtra("url", listData.get(position).postVideoUrl);
                startActivity(intentPlayVideo);

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // no menu for now
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private class RSSDataController extends AsyncTask<String, Integer, ArrayList<RSSPostData>>
    {
        private RSSXMLTag currentTag;
        private static final int TIMEOUT=10*1000;
        private static final String TAG="RSSDataController";

        @Override
        protected ArrayList<RSSPostData> doInBackground(String... params) {
            // TODO Auto-generated method stub
            String urlStr = params[0];
            //Log.d(TAG, "URL="+urlStr);
            InputStream is = null;
            ArrayList<RSSPostData> postDataList = new ArrayList<RSSPostData>();
            try {
                Log.d("try connection", "try connection");
                URL url = new URL (urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Log.d(TAG, connection.toString());
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(TIMEOUT);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                //Log.d(TAG, "The response id: " + response);
                is = connection.getInputStream();

                // parse xml after getting the data
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(is, null);

                int eventType = xpp.getEventType();
                //Log.d(TAG, "eventType:" + eventType);
                RSSPostData pdData = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, DD MMM yyyy HH:mm:ss");
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    //Log.d(TAG, "eventType:" + eventType);

                    if (eventType == XmlPullParser.START_TAG) {
                        String name = xpp.getName();

                        //Log.d(TAG, "name:" + name);
                        if (name.equals("item")) {
                            pdData = new RSSPostData();
                            currentTag = RSSXMLTag.IGNORETAG;
                        } else if (name.equals("title")) {
                            currentTag = RSSXMLTag.TITLE;
                            eventType = xpp.next();
                            // a bit of hardcoding assumes that the title has text
                            if (eventType == XmlPullParser.TEXT && pdData != null) {   //!=null added cause there are two titles already before items start
                                //Log.d(TAG, "title text:" + xpp.getText());
                                pdData.postTitle = xpp.getText();
                            }
                        } else if (name.equals("link")) {
                            currentTag = RSSXMLTag.LINK;
                        } else if (name.equals("pubDate")) {
                            currentTag = RSSXMLTag.DATE;
                            eventType = xpp.next();
                            // a bit of hardcoding assumes that the date has text
                            if (eventType == XmlPullParser.TEXT) {
                                //Log.d(TAG, "date text:" + xpp.getText());
                                pdData.postInfo = xpp.getText();
                            }
                        } else if (name.equals("content")) {
                            currentTag = RSSXMLTag.CONTENT;
                            Map<String, String> attributes = getAttributes(xpp);
                            //Log.d(TAG, "video text:" + attributes.get("url"));
                            pdData.postVideoUrl = attributes.get("url");

                        } else if (name.equals("thumbnail")) {
                            currentTag = RSSXMLTag.THUMBNAIL;
                            Map<String, String> attributes = getAttributes(xpp);
                            //Log.d(TAG, "thumbnail text:" + attributes.get("url"));
                            pdData.postThumbUrl = attributes.get("url");

                        }
                    }
                        else if (eventType == XmlPullParser.END_TAG) {

                        if (xpp.getName().equals("item")) {
                            Date postDate;
                            if (pdData.isFilled()){
                                //Log.d(TAG, "adding filled element");
                                postDataList.add(pdData);
                            }
                        } else {
                            currentTag = RSSXMLTag.IGNORETAG;
                        }
                    }


                    eventType = xpp.next();
            }


        } catch (Exception e) {
                e.printStackTrace();
            }
            return postDataList;
        }

        private Map<String,String> getAttributes(XmlPullParser parser) throws Exception {
            //final String TAG = "getAttributes";
            Map<String,String> attrs=null;
            int acount=parser.getAttributeCount();
            if(acount != -1) {
                //Log.d(TAG,"Attributes for ["+parser.getName()+"]");
                attrs = new HashMap<String,String>(acount);
                for(int x=0;x<acount;x++) {
                   // Log.d(TAG,"\t["+parser.getAttributeName(x)+"]=" +
                   //         "["+parser.getAttributeValue(x)+"]");
                    attrs.put(parser.getAttributeName(x), parser.getAttributeValue(x));
                }
            }
            else {
                throw new Exception("Required entity attributes missing");
            }
            return attrs;
        }
        protected void onPostExecute(ArrayList<RSSPostData> result) {
            for (int i = 0; i < result.size(); i++) {
                listData.add(result.get(i));
            }
            itemAdapter.notifyDataSetChanged();
        }


    }
}