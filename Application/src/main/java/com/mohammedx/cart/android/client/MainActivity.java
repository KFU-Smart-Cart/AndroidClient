package com.mohammedx.cart.android.client;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity {

    private static final int NOTIFICATION_ID = 123;
    public static boolean AdReady = false;
    public static boolean DBUpdate = false;
    public static boolean isConncted = false;
    public String[] aTitle;
    public String[] aDescription;
    public String[] aUUID;
    public String[] aMajor;
    public String[] aMinor;
    public Region beaconX, beaconY;
    double x = -1;
    double y = -1;
    boolean redayx = false;
    boolean redayy = false;
    double pos = 0;
    private BluetoothChatFragment fragment;
    private BeaconManager beaconManager;
    private NotificationManager notificationManager;
    private Region iceRegion;
    private Region blueberryRegion;
    private Region mintRegion;
    private Region iceColdRegion;
    private Region blueberryColdRegion;
    private Region mintColdRegion;
    private boolean notify = false;
    private ArrayAdapter offeradap;
    private Utils.Proximity proximityx;
    private Utils.Proximity proximityy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {


            Intent UpdateAdService = new Intent(this, UpdateAdService.class);
            startService(UpdateAdService);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothChatFragment();

            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();

            offeradap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            ListView listView1 = (ListView) findViewById(R.id.list);
            listView1.setAdapter(offeradap);

            // Beacon 1 "ice" info
            UUID iceUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int iceMajor = 15011;
            int iceMinor = 3641;
            iceRegion = new Region("ice", iceUUID, iceMajor, iceMinor);

            // Beacon 2 "blueberry" info
            UUID blueberryUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int blueberryMajor = 59081;
            int blueberryMinor = 14607;
            blueberryRegion = new Region("blueberry", blueberryUUID, blueberryMajor, blueberryMinor);

            // Beacon 3 "mint" info
            UUID mintUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int mintMajor = 33392;
            int mintMinor = 16805;
            mintRegion = new Region("mint", mintUUID, mintMajor, mintMinor);

            // Beacon 4 "iceCold" info
            UUID iceColdUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int iceColdMajor = 22224;
            int iceColdMinor = 23153;
            iceColdRegion = new Region("iceCold", iceColdUUID, iceColdMajor, iceColdMinor);

            // Beacon 5 "blueberryCold" info
            UUID blueberryColdUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int blueberryColdMajor = 20458;
            int blueberryColdMinor = 40609;
            blueberryColdRegion = new Region("blueberryCold", blueberryColdUUID, blueberryColdMajor, blueberryColdMinor);

            // Beacon 6 "mintCold" info
            UUID mintColdUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int mintColdMajor = 47188;
            int mintColdMinor = 25260;
            mintColdRegion = new Region("mintCold", mintColdUUID, mintColdMajor, mintColdMinor);

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            beaconManager = new BeaconManager(this);

            // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
            // In order for this demo to be more responsive and immediate we lower down those values.
            beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
            beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
                @Override
                public void onEnteredRegion(Region region, List<Beacon> beacons) {
                    postNotification(region.getIdentifier(), true, region.getProximityUUID(), region.getMajor(), region.getMinor());
                }

                @Override
                public void onExitedRegion(Region region) {
                    postNotification(region.getIdentifier(), false, region.getProximityUUID(), region.getMajor(), region.getMinor());
                }
            });


            beaconX = new Region("mintCold", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 47188, 25260);
            beaconY = new Region("iceCold", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 22224, 23153);


            beaconManager.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region region, final List<Beacon> list) {
                    for (final Beacon beacon : list) {
                        if (beacon.getProximityUUID().equals(beaconX.getProximityUUID()) && (beacon.getMajor() == beaconX.getMajor()) && (beacon.getMinor() == beaconX.getMinor())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    x = Utils.computeAccuracy(beacon);
                                    proximityx = Utils.computeProximity(beacon);
                                    Log.d("MMMM", "MMx = " + beacon.getMeasuredPower());
                                    redayx = true;
                                }
                            });
                        }
                        if (beacon.getProximityUUID().equals(beaconY.getProximityUUID()) && (beacon.getMajor() == beaconY.getMajor()) && (beacon.getMinor() == beaconY.getMinor())) {
                            runOnUiThread(new Runnable() {
                                ;

                                @Override
                                public void run() {
                                    y = Utils.computeAccuracy(beacon);
                                    proximityy = Utils.computeProximity(beacon);
                                    redayy = true;
                                    Log.d("MMMM", "MMy = " + beacon.getMeasuredPower());
                                }
                            });
                        }
                        if (redayx && redayy) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dis(x, y);
                                }
                            });
                        } else if (!redayx && !redayy) {
                            redayx = false;
                            redayy = false;
                        }
                    }
                }
            });

        }
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(beaconX);
                beaconManager.startRanging(beaconY);
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(iceRegion);
                beaconManager.startMonitoring(blueberryRegion);
                beaconManager.startMonitoring(mintRegion);
//                beaconManager.startMonitoring(iceColdRegion);
                beaconManager.startMonitoring(blueberryColdRegion);
//                beaconManager.startMonitoring(mintColdRegion);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(beaconX);
                beaconManager.startRanging(beaconY);
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
//                beaconManager.startMonitoring(iceRegion);
//                beaconManager.startMonitoring(blueberryRegion);
                beaconManager.startMonitoring(mintRegion);
                beaconManager.startMonitoring(iceColdRegion);
                beaconManager.startMonitoring(blueberryColdRegion);
                beaconManager.startMonitoring(mintColdRegion);
            }
        });
    }

    @Override
    protected void onDestroy() {
        notificationManager.cancel(NOTIFICATION_ID);
        beaconManager.disconnect();
        super.onDestroy();
    }

    public void dis(double x, double y) {
        redayx = false;
        redayy = false;
        pos = Math.abs(((x - y) / ((x + y) / 2)) * 100);
        if (proximityx == Utils.Proximity.IMMEDIATE && proximityy == Utils.Proximity.IMMEDIATE) {
            fragment.sendMessage("1");
            fragment.sendMessage("4");
            try {
                Thread.sleep(1550);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (proximityx == Utils.Proximity.FAR && proximityy == Utils.Proximity.FAR) {
            fragment.sendMessage("1");
        } else {
            fragment.sendMessage("5");
            if (pos >= 90.0) {
                if (x > y) {
                    //left
                    Log.d("ddd", "XXXXXX");
                    if (isConncted) {
                        fragment.sendMessage("3");
                        try {
                            Thread.sleep(3050);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (y > x) {
                    //right
                    Log.d("ddd", "YYYYYY");
                    if (isConncted) {
                        fragment.sendMessage("2");
                        try {
                            Thread.sleep(3050);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {//move
                Log.d("ddd", "SSSS");
                if (isConncted) {
                    fragment.sendMessage("0");
                    try {
                        Thread.sleep(1050);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("ddd", "X = " + x);
            Log.d("ddd", "Y = " + y);
            Log.d("ddd", "pos = " + pos);
            pos = 0;
        }
    }

    private void postNotification(String Identifier, Boolean states, UUID UUID, int Major, int Minor) {
        if (AdReady) {

            SQLiteHelper mSqLiteHelper = new SQLiteHelper(this);
            SQLiteDatabase db = mSqLiteHelper.getWritableDatabase();

            String[] columns = {mSqLiteHelper.TITLE, mSqLiteHelper.DESCRIPTION, mSqLiteHelper.UUID, mSqLiteHelper.MAJOR, mSqLiteHelper.MINOR};

            Cursor result = db.query(mSqLiteHelper.TABLE_NAME, columns, null, null, null, null, null);

            int nRow = result.getCount();

            int x = 0;
            if (DBUpdate) {
                aTitle = new String[nRow];
                aDescription = new String[nRow];
                aUUID = new String[nRow];
                aMajor = new String[nRow];
                aMinor = new String[nRow];
                result.moveToFirst();
                if (nRow != 0) {
                    do {
                        aTitle[x] = result.getString(0);
                        aDescription[x] = result.getString(1);
                        aUUID[x] = result.getString(2).toLowerCase();
                        aMajor[x] = result.getString(3);
                        aMinor[x] = result.getString(4);
                        x++;
                    } while (result.moveToNext());
                }
            }
            DBUpdate = false;
            db.close();


            String sUUID = String.valueOf(UUID).toLowerCase();
            String sMajor = String.valueOf(Major);
            String sMinor = String.valueOf(Minor);

            String nTitle = "";
            String nDescription = "";
            if (nRow != 0) {
                for (int count = 0; count < nRow; count++) {
                    if (sUUID.equals(aUUID[count]) && sMajor.equals(aMajor[count]) && sMinor.equals(aMinor[count])) {
                        nTitle = aTitle[count];
                        nDescription = aDescription[count];
                        notify = true;
                        break;
                    }
                }
            }

            if (notify) {
                int beaconColor = R.drawable.beacon_gray;
                Intent notifyIntent = new Intent(MainActivity.this, MainActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivities(
                        MainActivity.this,
                        0,
                        new Intent[]{notifyIntent},
                        PendingIntent.FLAG_UPDATE_CURRENT);

                switch (Identifier) {
                    case "ice":
                    case "iceCold":
                        beaconColor = R.drawable.beacon_ice;
                        break;
                    case "blueberry":
                    case "blueberryCold":
                        beaconColor = R.drawable.beacon_blueberry;
                        break;
                    case "mint":
                    case "mintCold":
                        beaconColor = R.drawable.beacon_mint;
                        break;
                }

                Notification notification = new Notification.Builder(MainActivity.this)
                        .setSmallIcon((beaconColor))
                        .setContentTitle(nTitle)
                        .setContentText(nDescription)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();

                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_LIGHTS;


                if (states) {
                    offeradap.remove(nDescription);
                    offeradap.add(nDescription);
                    notificationManager.notify(NOTIFICATION_ID, notification);
                } else {
                    offeradap.remove(nDescription);
                    notificationManager.cancel(NOTIFICATION_ID);
                }


                notify = false;
            }
        }
    }
}
