package com.buffalo.edu;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class MainActivity extends ActionBarActivity implements OnClickListener, OnCheckedChangeListener {
    
	ExpandableListView listView;
    public List<Entry<String, Integer>> wakelockMap = new ArrayList<Entry<String, Integer>>();
	private Button applyButton;
    public static final String PREFS_NAME = "WakelockMap";
    static final int PARTIAL_WL_MASK = 1 << 2;
    static final int SCREEN_WL_MASK = 1 << 1;
    static final int WIFI_WL_MASK = 1 << 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SharedPreferences wakelockMapSharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        
        // create adapter for listview
        /*
         * ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
         * android.R.layout.simple_list_item_multiple_choice, myList);
         */
        ArrayList<String> appList = getInstalledAppPermissions();
        
        for(int i=0; i< appList.size(); i++)
        {
        	Log.d("app list" ,appList.get(i));
        	Entry<String, Integer> entry = new SimpleEntry<String, Integer>(appList.get(i), wakelockMapSharedPreferences.getInt(appList.get(i), 0));
        	wakelockMap.add(entry);
        }
        
        ExpandableListAdapter adapter = new MyCustomAdapter(this, R.layout.row, wakelockMap);
        listView = (ExpandableListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        // attach listeners
        /*
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Do something in response to the click
      
                CheckBox cc = (CheckBox)v.findViewById(R.id.checkBox1);
     
                cc.setChecked(!cc.isChecked());
                
                if(wakelockMap.get(listView.getItemAtPosition(position)) == 1)
                {
                	wakelockMap.put((String)listView.getItemAtPosition(position), 0);
                }
                else
                {
                	wakelockMap.put((String)listView.getItemAtPosition(position), 1);

                }
                
                Toast.makeText(getApplicationContext(),
                        "Item " + listView.getItemAtPosition(position),
                        Toast.LENGTH_SHORT).show();
            }
        });
        */
        
        applyButton = (Button) findViewById(R.id.apply);
        applyButton.setOnClickListener(this);
   
    }
    
    public void onClick(View v) { // Parameter v stands for the view that was clicked.
    	
    	
		SharedPreferences wakelockMapSharedPreferences = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = wakelockMapSharedPreferences.edit();
		// getId() returns this view's identifier.
		if(v.getId() == R.id.apply){
			// setText() sets the string value of the TextView
			Intent intent = new Intent();
			intent.setAction("android.PowerManager.ControlWakeLocks");
			HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
			Iterator iterator = wakelockMap.iterator();
			while(iterator.hasNext()) {
				Entry<String, Integer> entry = (Entry<String, Integer>) iterator.next();
				hashMap.put(entry.getKey(), entry.getValue());
				editor.putInt(entry.getKey(), entry.getValue());
			}
			intent.putExtra("AppRuntimePermissions", hashMap);
			sendBroadcast(intent);
			
			//commit the shared preferences
			editor.commit();
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override	
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
//            case R.id.action_nextactivity:
//                openAnotherActivity();
//                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettings() {
        // TODO Auto-generated method stub

    }
    
    public ArrayList<String> getInstalledAppPermissions()
	{
    	ArrayList<String> appList = new ArrayList<String>();
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo applicationInfo : packages) {
			Log.d("WakeLockDetector", "App: " + applicationInfo.name + " Package: " + applicationInfo.packageName);

			try {
				PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

				//Get Permissions
				String[] requestedPermissions = packageInfo.requestedPermissions;

				if(requestedPermissions != null) {
					for (int i = 0; i < requestedPermissions.length; i++) {
						//Log.d("WakeLockDetector", requestedPermissions[i]);
						if(requestedPermissions[i].equals("android.permission.WAKE_LOCK")) {
							appList.add(applicationInfo.packageName);
						}

					}
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return appList;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	  
		  
		 int position = (Integer)buttonView.getTag();
		 String appName = wakelockMap.get(position).getKey();
		 Integer permValue = wakelockMap.get(position).getValue();
		 if(isChecked) {
			 if (buttonView.getId() == R.id.checkBox1) permValue = permValue | PARTIAL_WL_MASK;
			 if (buttonView.getId() == R.id.checkBox2) permValue = permValue | SCREEN_WL_MASK;
			 if (buttonView.getId() == R.id.checkBox3) permValue = permValue | WIFI_WL_MASK;
		 } else {
			 if (buttonView.getId() == R.id.checkBox1) permValue = permValue & ~PARTIAL_WL_MASK;
			 if (buttonView.getId() == R.id.checkBox2) permValue = permValue & ~SCREEN_WL_MASK;
			 if (buttonView.getId() == R.id.checkBox3) permValue = permValue & ~WIFI_WL_MASK;
		 }
		 wakelockMap.get(position).setValue(permValue);
	}

}

class MyCustomAdapter extends BaseExpandableListAdapter {
	List<Entry<String,Integer>> wakelockEntriesList;
    Context mContext = null;
    LayoutInflater inflator = null;
    //private LayoutInflater inflater;

    public MyCustomAdapter(Context context, int resourceid, List<Entry<String, Integer>> map) {
        // TODO Auto-generated constructor stub
        //inflater = LayoutInflater.from(context);
    	inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	this.wakelockEntriesList = map;
        mContext = context;
    }

	@Override
	public int getGroupCount() {
		return wakelockEntriesList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
    	View rowView = inflator.inflate(R.layout.row, parent, false);
        String str = wakelockEntriesList.get(groupPosition).getKey();
        TextView tv = (TextView) rowView.findViewById(R.id.textView1);
        tv.setText(str);
        
        return rowView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
	    View childRowView = inflator.inflate(R.layout.child_row, parent, false);
        CheckBox cb1 = (CheckBox) childRowView.findViewById(R.id.checkBox1);
        cb1.setTag(groupPosition);
        CheckBox cb2 = (CheckBox) childRowView.findViewById(R.id.checkBox2);
        cb2.setTag(groupPosition);
        CheckBox cb3 = (CheckBox) childRowView.findViewById(R.id.checkBox3);
        cb3.setTag(groupPosition);
        Integer value = wakelockEntriesList.get(groupPosition).getValue();
        //need to convert integer to bit vector and set the checkboxes correspondingly
        char[] bits = Integer.toBinaryString(value).toCharArray();
        if(bits[bits.length-1]== '1') {
            cb3.setChecked(true);
        } else {
            cb3.setChecked(false);
        }
        if(bits.length > 1 && bits[bits.length-2]== '1') {
            cb2.setChecked(true);
        } else {
            cb2.setChecked(false);
        }
        if(bits.length > 2 && bits[bits.length-3]== '1') {
            cb1.setChecked(true);
        } else {
            cb1.setChecked(false);
        }
        
        CustomHolder ch = new CustomHolder();
        ch.chkbox1 = cb1;
        ch.chkbox2 = cb2;
        ch.chkbox3 = cb3;
        
        ch.chkbox1.setOnCheckedChangeListener((MainActivity) mContext);
        ch.chkbox2.setOnCheckedChangeListener((MainActivity) mContext);
        ch.chkbox3.setOnCheckedChangeListener((MainActivity) mContext);  
        
		return childRowView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}
	
	 private static class CustomHolder {	
	    	public CheckBox chkbox1;
	    	public CheckBox chkbox2;
	    	public CheckBox chkbox3;
	    }
    
}