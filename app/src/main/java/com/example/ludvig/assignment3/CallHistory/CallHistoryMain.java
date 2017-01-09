package com.example.ludvig.assignment3.CallHistory;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ludvig.assignment3.R;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CallHistoryMain extends ListActivity {

    File dir;

    ListView listView;
    Cursor callHistoryCursor;
    ArrayAdapter adapter;
    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history_main);

        updateList();
        dir = this.getExternalFilesDir(null);
        listView = getListView();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,list);
        listView.setAdapter(adapter);

        //Open context on short click aswell as the default long-click.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openContextMenu(view);
            }
        });

        registerForContextMenu(listView);
    }


    static final int CALL = 0;
    static final int DIAL = 1;
    static final int MESSAGE = 2;

    @Override
    public void onCreateContextMenu(ContextMenu menu,View view, ContextMenu.ContextMenuInfo info) {
        menu.add(0,CALL,0,"Call");
        menu.add(0,DIAL,0,"Dial Number");
        menu.add(0, MESSAGE,0,"Send Message");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        String numberSelected = list.get(pos);
        switch(item.getItemId()) {
            case CALL:
                makePhoneCall(numberSelected);
                return true;
            case DIAL:
                dialNumber(numberSelected);
                return true;
            case MESSAGE:
                sendMessage(numberSelected);
                return true;
            default:
                return false;
        }
    }

    private void dialNumber(String number) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +number)));
    }
    private void makePhoneCall(String number) {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +number)));
    }
    private void sendMessage(String number) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/plan");
        i.setData(Uri.parse("sms:"+number));
        i.putExtra("sms_body",number);
        startActivity(Intent.createChooser(i,"Send Message"));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.callhistory_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.callhistory_update_item:
                updateList();
                return true;
            case R.id.callhistory_delete_file:
                File file = new File(dir,"phoneNumbers");
                file.delete();
                adapter.clear();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateList() {
        String contents;
        File file = new File(dir,"phoneNumbers");
        if(file.isFile()) {
            try {
                contents = Files.toString(file, Charsets.UTF_8);
                list.clear();
                String[] arr = contents.split(",");
                list.addAll(Arrays.asList(arr));
                adapter.notifyDataSetChanged();
                System.out.println("list updated..");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
