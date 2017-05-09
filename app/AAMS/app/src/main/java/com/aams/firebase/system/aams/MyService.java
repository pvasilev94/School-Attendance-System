package com.aams.firebase.system.aams;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.aams.firebase.system.aams.models.User;
import com.bluvision.beeks.sdk.constants.BeaconType;
import com.bluvision.beeks.sdk.domainobjects.Beacon;
import com.bluvision.beeks.sdk.interfaces.BeaconListener;
import com.bluvision.beeks.sdk.interfaces.OnBeaconChangeListener;
import com.bluvision.beeks.sdk.util.BeaconManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aams.firebase.system.aams.models.device.MonitoringDevice;
import com.aams.firebase.system.aams.models.student.StudentBeacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyService extends Service implements BeaconListener {

    public static final String TAG = "MyService";
    public static final String STUDENT = "students";
    public static final String MONITORING_DEVICE = "device";
    public static final String USER = "users";
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private DatabaseReference updateReference;
    private User user;
    private List<StudentBeacon> currentStudentBeacons;
    private List<StudentBeacon> updateStudentBeacon;
    private String DEVICE_ID;
    private App app;
    private boolean isAuth;

    private BeaconManager beaconManager;
    private OnBeaconChangeListener listener;


    //App context can be received only from onCreate method
    @Override
    public void onCreate() {
        super.onCreate();
        app = (App) getApplication();
        beaconManager = new BeaconManager(app.getApplicationContext());
        beaconManager.addBeaconListener(this);
        beaconManager.addRuleToIncludeScanByType(BeaconType.S_BEACON);
    }

    public MyService() {
        currentStudentBeacons = new ArrayList<>();
        updateStudentBeacon = new ArrayList<>();
        updateReference = FirebaseDatabase.getInstance().getReference();
        userReference = FirebaseDatabase.getInstance().getReference().child(USER).getRef();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(STUDENT).getRef();
        isAuth = false;
        if (isAuthenticated()) {
            startMonitoring();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beaconManager.startScan();
            }
        }, 10000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Initialize monitoring and populate data through firebase
     */
    public void startMonitoring() {
        Log.i(TAG, "startMonitoring() has started");
        if (currentStudentBeacons == null) {
            userReference.child(DEVICE_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "IS USER DATA CALLED");
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                }
            });
        }
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                StudentBeacon beacon = dataSnapshot.getValue(StudentBeacon.class);
                MonitoringDevice device = dataSnapshot.child(MONITORING_DEVICE).getValue(MonitoringDevice.class);
                beacon.setDeviceData(device);
                addStudent(beacon);
                Log.d(TAG, "onChildAdded with data" + dataSnapshot.toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                StudentBeacon beacon = dataSnapshot.getValue(StudentBeacon.class);
                MonitoringDevice device = dataSnapshot.child(MONITORING_DEVICE).getValue(MonitoringDevice.class);
                beacon.setDeviceData(device);
                updateStudent(beacon);

                Log.d(TAG, "CHILD HAS CHANGED" + dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                StudentBeacon beacon = dataSnapshot.getValue(StudentBeacon.class);
                MonitoringDevice device = dataSnapshot.child(MONITORING_DEVICE).getValue(MonitoringDevice.class);
                beacon.setDeviceData(device);
                removeStudent(beacon);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //should never be called if it does log it as error
                Log.e(TAG, "onChildMoved error : KEY --- " + s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //should never be called if it does log it as error
                Log.e(TAG, databaseError.toString());
            }
        };
        databaseReference.addChildEventListener(childEventListener);
//        listener = new OnBeaconChangeListener() {
//            @Override
//            public void onRssiChanged(Beacon beacon, int i) {
//                Log.d(TAG, "OnRssi changed : " + i);
//            }
//
//            @Override
//            public void onRangeChanged(Beacon beacon, Range range) {
//                Log.d(TAG, "On Range Changed :" + range.toString());
//            }
//
//            @Override
//            public void onBeaconExit(Beacon beacon) {
//                Log.d(TAG, "onBeaconExit");
//            }
//
//            @Override
//            public void onBeaconEnter(Beacon beacon) {
//                Log.d(TAG, "On Becon Enter");
//            }
//        };
    }

    /**
     * Check to see if user has authenticated
     * @return
     */
    public boolean isAuthenticated() {
        if (getUid() != null) {
            DEVICE_ID = getUid();
            isAuth = true;
        }
        return isAuth;
    }

    /**
     * Returns Uid of authenticated user
     * @return
     */
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Return device LEVEL SETTINGS
     * @return
     */
    public int getLevel() {return 2;}
    /**
     * Writes to firebase cloud aams with
     * @param beacon
     * @param key
     */
    public void writeToDatabase(StudentBeacon beacon, String key) {
        StudentBeacon updateBeacon = beacon;
        beacon.getDeviceData().setName(getUid());
        Map<String, Object> studentValues = null;
        studentValues = updateBeacon.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/students/" + key, studentValues);
        updateReference.updateChildren(childUpdates);
    }

    /**
     * When child changes replaces the beacon and that place with the current one from aams
     * @param beacon
     */
    public void updateStudent(StudentBeacon beacon) {
        int index;
        if (currentStudentBeacons != null) {
            for (StudentBeacon b1 : currentStudentBeacons) {
                if (b1.getBeaconMAC().equals(beacon.getBeaconMAC()));
                index = currentStudentBeacons.indexOf(b1);
                if (index != -1) {
                    currentStudentBeacons.set(index, b1);
                }
            }
        }
    }

    /**
     * Called through onChildRemoved
     * @param student
     */
    public void removeStudent(StudentBeacon student) {
        currentStudentBeacons.remove(student);
    }

    /**
     * Called through onChildAdded - creates a local arraylist with all the students
     * @param student
     */
    public void addStudent(StudentBeacon student) {
        if (currentStudentBeacons != null) {
            Log.d(TAG, "ADD STUDENT CALLED : " + currentStudentBeacons.size());
        }
        if (student != null) {
            currentStudentBeacons.add(student);
        }
    }

    /**
     * Update firebase aams(complicated algorithm)
     * @param beacon
     */
    public void updateBeaconDatabase(StudentBeacon beacon) {
        beacon.getDeviceData().setName(getUid());

        if(beacon.getDeviceData().getLevel() == 1) {
            if (getLevel() == 2) {
                beacon.setLastSeen(TRUE);
                beacon.getDeviceData().setLevel(getLevel());
            writeToDatabase(beacon, beacon.getDeviceData().getKey());
        }
        if (beacon.isLastSeen() && getLevel() == 1) {
            beacon.setLastSeen(FALSE);
            beacon.getDeviceData().setLevel(getLevel());
            writeToDatabase(beacon, beacon.getDeviceData().getKey());
        }

        }
        if (beacon.getDeviceData().getLevel() == 2) {
            if (getLevel() == 1) {
                beacon.setLastSeen(FALSE);
                beacon.getDeviceData().setLevel(getLevel());
                writeToDatabase(beacon, beacon.getDeviceData().getKey());
            }
        }
        if(beacon.getDeviceData().getName() == null) {
            beacon.setLastSeen(TRUE);
            beacon.getDeviceData().setLevel(getLevel());
            writeToDatabase(beacon, beacon.getDeviceData().getKey());
        }
    }
    /**
     * Compares MAC address when detected against the currentStudentBeacon list
     *
     * @param MAC
     * @param currentStudentBeacons
     */
    public void compareMAC(String MAC, List<StudentBeacon> currentStudentBeacons) {
        if (currentStudentBeacons != null) {
            for (StudentBeacon beacon : currentStudentBeacons) {
                if (beacon.getBeaconMAC().equals(MAC)) {
                    Log.d(TAG, "IS TRUE");
                    updateBeaconDatabase(beacon);
                }
            }
        }
    }

    /**
     * Start of BeaconListener methods
     * This listener is used to listen when a beacon is discovered
     */
    @Override
    public void onBeaconFound(Beacon beacon) {
        if (beacon != null) {
            if (beacon.getDevice().getAddress() != null) {
                String MAC = beacon.getDevice().getAddress();
                compareMAC(MAC, currentStudentBeacons);
            }
        }
    }

    @Override
    public void bluetoothIsNotEnabled() {
        Log.e(TAG, "ENABLE BLUETOOTH");
    }

}
