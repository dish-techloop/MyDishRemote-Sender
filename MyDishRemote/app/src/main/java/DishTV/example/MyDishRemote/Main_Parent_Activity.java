package DishTV.example.MyDishRemote;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import DishTV.example.MyDishRemote.Utility.GlobalData;

import com.example.MyDishRemote.R;

import java.util.ArrayList;
import java.util.Set;


public class Main_Parent_Activity extends AppCompatActivity {
    GlobalData globalData;
    GridView androidGridView;

    private EditText eTextSearch;
    private TextView status;
    private Button btnConnect,btn_search_channel;
    private ListView listViewChatData;
    private Dialog dialog;
    private TextInputLayout inputLayout;
    private ArrayAdapter<String> channelAdapter;
    private ArrayList<String> channelMessages;
    private BluetoothAdapter bluetoothAdapter;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChannelController chatController;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;

    private int backButtonCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_parent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        globalData = new GlobalData(getApplicationContext());
        if (globalData.getUserId().length() <= 0) {
            Intent it = new Intent(Main_Parent_Activity.this, Login.class);
            startActivity(it);
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        String UserType=globalData.getUserType();
        switch(UserType) {
            case "admin":
                actionBar.setTitle("My Dish TV Remote");
                Bind_left_menu();
                break;
        }


        findViewsByIds();

        //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish();
        }

        //show bluetooth devices dialog when click connect button
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrinterPickDialog();
            }
        });

        channelMessages = new ArrayList<>();
        channelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, channelMessages);
        listViewChatData.setAdapter(channelAdapter);

        eTextSearch.setVisibility(View.GONE);
        btn_search_channel.setVisibility(View.GONE);

    }



    private void Bind_left_menu()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView lbl_userNamedisp = (TextView) header.findViewById(R.id.lblUserName);
        lbl_userNamedisp.setText("Welcome : " + globalData.getUserName());

        TextView lbl_sessionNamedisp = (TextView) header.findViewById(R.id.lblSessionName);
        lbl_sessionNamedisp.setText("Your User ID : " + globalData.getUserId());

        final DrawerLayout drawerLayout =(DrawerLayout) findViewById(R.id.drawer_layout);
        Menu menu = navigationView.getMenu();

        SubMenu menu4 = menu.addSubMenu("Profile").setIcon(R.drawable.baseline_group_add_black_18dp);

        menu4.add("Logout").setIcon(R.drawable.baseline_touch_app_black_18dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                globalData.remobeGlobalData();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return false;
            }
        });

        drawerLayout.closeDrawers();

    }

    private void Bind_home_content()
    {
        final String[] gridViewString = {
                "Aastha",
                "Alankar",
                "Animal Planet",
                "B4U Movies",
                "Cartoon Network",
                "Colors HD",
                "Discovery Kids",
                "National Geographic",
                "9XM",
                "Zee Hindustan",
                "Zee News",
                "Zee Odhisha",
                "Zee Salaam",
                "Zee Talkies HD",
                "Zee Yuva"
        } ;
        final int[] gridViewImageId = {
                R.drawable.aastha,
                R.drawable.alankar,
                R.drawable.animal_planet,
                R.drawable.bfouru_movies,
                R.drawable.cartoon_network,
                R.drawable.colors_hd,
                R.drawable.discovery_kids,
                R.drawable.national_geographic,
                R.drawable.nine_xm,
                R.drawable.zee_hindustan,
                R.drawable.zee_news,
                R.drawable.zee_odisha,
                R.drawable.zee_salaam,
                R.drawable.zee_talkies_hd,
                R.drawable.zee_yuva,
        };

        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(getApplicationContext(), gridViewString, gridViewImageId);
        androidGridView=(GridView)findViewById(R.id.grid_view_image_home);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                sendMessage(gridViewString[+i]);
                Toast.makeText(getApplicationContext(), gridViewString[+i], Toast.LENGTH_LONG).show();
            }
        });
    }


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChannelController.STATE_CONNECTED:
                            setStatus("Connected to: " + connectingDevice.getName());
                            //btnConnect.setEnabled(false);
                            Bind_home_content();
                            eTextSearch.setVisibility(View.VISIBLE);
                            btn_search_channel.setVisibility(View.VISIBLE);
                            btnConnect.setVisibility(View.GONE);
                            break;
                        case ChannelController.STATE_CONNECTING:
                            setStatus("Connecting...");
                            break;
                        case ChannelController.STATE_LISTEN:
                        case ChannelController.STATE_NONE:
                            setStatus("Not connected");
                            eTextSearch.setVisibility(View.GONE);
                            btn_search_channel.setVisibility(View.GONE);
                            btnConnect.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
                    channelMessages.add("Me: " + writeMessage);
                    channelAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
                    channelMessages.add(connectingDevice.getName() + ":  " + readMessage);
                    channelAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void showPrinterPickDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle("Bluetooth Devices");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add("No devices have been paired");
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setStatus(String s) {
        status.setText(s);
    }

    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        chatController.connect(device);
    }

    private void findViewsByIds() {
        status = (TextView) findViewById(R.id.status);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        eTextSearch = (EditText) findViewById(R.id.edt_channel_name);
        btn_search_channel = (Button) findViewById(R.id.btn_search_channel);
        listViewChatData = (ListView) findViewById(R.id.list);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    chatController = new ChannelController(this, handler);
                } else {
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void sendMessage(String message) {
        if (chatController.getState() != ChannelController.STATE_CONNECTED) {
            Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatController.write(send);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = new ChannelController(this, handler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (chatController != null) {
            if (chatController.getState() == ChannelController.STATE_NONE) {
                chatController.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatController != null)
            chatController.stop();
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add("No devices have been paired");
                }
            }
        }
    };


    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ChangeSession) {
            Toast.makeText(this, "Change Session clicked.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_LogOut) {
            Toast.makeText(this, "Log Out clicked.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
