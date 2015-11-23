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

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity {

    private static final int NOTIFICATION_ID = 123;
    public static boolean AdRadey = false;
    public static boolean DBUpdate = false;
    public String[] aTitle;
    public String[] aDescription;
    public String[] aUUID;
    public String[] aMajor;
    public String[] aMinor;
    private BeaconManager beaconManager;
    private NotificationManager notificationManager;
    private Region iceRegion;
    private Region blueberryRegion;
    private Region mintRegion;
    private Region iceColdRegion;
    private Region blueberryColdRegion;
    private Region mintColdRegion;
    private boolean notfiy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            Intent UpdateAdService = new Intent(this, UpdateAdService.class);
            startService(UpdateAdService);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();


            // Beacon 1 "ice" info
            UUID iceUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int iceMajor = 15011;
            int iceMinor = 3641;
//            iceRegion = new Region("ice", iceUUID, iceMajor, iceMinor);

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
//            iceColdRegion = new Region("iceCold", iceColdUUID, iceColdMajor, iceColdMinor);

            // Beacon 5 "blueberryCold" info
            UUID blueberryColdUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int blueberryColdMajor = 20458;
            int blueberryColdMinor = 40609;
//            blueberryColdRegion = new Region("blueberryCold", blueberryColdUUID, blueberryColdMajor, blueberryColdMinor);

            // Beacon 6 "mintCold" info
            UUID mintColdUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            int mintColdMajor = 47188;
            int mintColdMinor = 25260;
//            mintColdRegion = new Region("mintCold", mintColdUUID, mintColdMajor, mintColdMinor);

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
//                    postNotification(region.getIdentifier(), false, region.getProximityUUID(), region.getMajor(), region.getMinor());
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationManager.cancel(NOTIFICATION_ID);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
//                beaconManager.startMonitoring(iceRegion);
                beaconManager.startMonitoring(blueberryRegion);
                beaconManager.startMonitoring(mintRegion);
//                beaconManager.startMonitoring(iceColdRegion);
//                beaconManager.startMonitoring(blueberryColdRegion);
//                beaconManager.startMonitoring(mintColdRegion);
            }
        });
    }

    @Override
    protected void onDestroy() {
        notificationManager.cancel(NOTIFICATION_ID);
        beaconManager.disconnect();
        super.onDestroy();
    }

    //test

    private void postNotification(String Identifier, Boolean states, UUID UUID, int Major, int Minor) {
        if (AdRadey) {

            SQLiteHelper mSqLiteHelper = new SQLiteHelper(this);
            SQLiteDatabase db = mSqLiteHelper.getWritableDatabase();

            String[] columns = {mSqLiteHelper.TITLE, mSqLiteHelper.DESCRIPTION, mSqLiteHelper.UUID, mSqLiteHelper.MAJOR, mSqLiteHelper.MINOR};

            Cursor resalt = db.query(
                    mSqLiteHelper.TABLE_NAME,  // The table to query
                    columns,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );


            int nRow = resalt.getCount();


            int x = 0;
            if (DBUpdate) {
                aTitle = new String[nRow];
                aDescription = new String[nRow];
                aUUID = new String[nRow];
                aMajor = new String[nRow];
                aMinor = new String[nRow];
                resalt.moveToFirst();
                do {
                    aTitle[x] = resalt.getString(0);
                    aDescription[x] = resalt.getString(1);
                    aUUID[x] = resalt.getString(2).toLowerCase();
                    aMajor[x] = resalt.getString(3);
                    aMinor[x] = resalt.getString(4);
                    x++;
                } while (resalt.moveToNext());
            }
            DBUpdate = false;


            String sUUID = String.valueOf(UUID).toLowerCase();
            String sMajor = String.valueOf(Major);
            String sMinor = String.valueOf(Minor);

            String nTitle = "";
            String nDescription = "";
//
            for (int count = 0; count < nRow; count++) {
                if (sUUID.equals(aUUID[count]) && sMajor.equals(aMajor[count]) && sMinor.equals(aMinor[count])) {
                    nTitle = aTitle[count];
                    nDescription = aDescription[count];
                    notfiy = true;
                    break;
                }
            }

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

            if (notfiy) {
                notificationManager.notify(NOTIFICATION_ID, notification);
                notfiy = false;
            }
        }
    }
}
