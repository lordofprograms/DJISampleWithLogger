package com.dji.sdk.sample.internal.controller;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.dji.sdk.sample.internal.utils.CsvLogger;
import com.dji.sdk.sample.internal.utils.CsvUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.BluetoothProductConnector;
import dji.sdk.sdkmanager.DJISDKManager;

import static com.dji.sdk.sample.internal.utils.ModuleVerificationUtil.getFlightController;

/**
 * Main application
 */
public class DJISampleApplication extends Application {

    public static final String TAG = DJISampleApplication.class.getName();

    private static BaseProduct product;
    private static BluetoothProductConnector bluetoothConnector = null;
    private static Bus bus = new Bus(ThreadEnforcer.ANY);
    private static Application app = null;

    /**
     * Gets instance of the specific product connected after the
     * API KEY is successfully validated. Please make sure the
     * API_KEY has been added in the Manifest
     */
    public static synchronized BaseProduct getProductInstance() {
        product = DJISDKManager.getInstance().getProduct();
        return product;
    }

    public static synchronized BluetoothProductConnector getBluetoothProductConnector() {
        bluetoothConnector = DJISDKManager.getInstance().getBluetoothProductConnector();
        return bluetoothConnector;
    }

    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static boolean isHandHeldConnected() {
        return getProductInstance() != null && getProductInstance() instanceof HandHeld;
    }

    public static synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) {
            return null;
        }
        return (Aircraft) getProductInstance();
    }

    public static synchronized HandHeld getHandHeldInstance() {
        if (!isHandHeldConnected()) {
            return null;
        }
        return (HandHeld) getProductInstance();
    }

    public static Application getInstance() {
        return DJISampleApplication.app;
    }

    public static Bus getEventBus() {
        return bus;
    }

    private File getLogFile() {
        File directory = new File(getFilesDir(), "DJILog");
        if (!directory.exists()) directory.mkdirs();

        String csvName = "log-" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + ".csv";
        return new File(directory, csvName);
    }

    public void setFlightController() throws IOException {
        final FileWriter fileWriter = new FileWriter(getLogFile());
        CsvUtils.writeLine(fileWriter, Arrays.asList("Date", "Time", "lat", "lon", "alt", "pitch", "roll", "yaw"));

        getFlightController().setStateCallback(new FlightControllerState.Callback() {
            @Override
            public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                LocationCoordinate3D coordinate3D = flightControllerState.getAircraftLocation();
                Attitude attitude = flightControllerState.getAttitude();

                String date = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(new Date());
                String time = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());

                double lat = coordinate3D.getLatitude();
                double lon = coordinate3D.getLongitude();
                double alt = coordinate3D.getAltitude();

                double pitch = attitude.pitch;
                double roll = attitude.roll;
                double yaw = attitude.yaw;

                try {
                    CsvUtils.writeLine(fileWriter, Arrays.asList(date, time,
                            String.valueOf(lat), String.valueOf(lon), String.valueOf(alt),
                            String.valueOf(pitch), String.valueOf(roll), String.valueOf(yaw)));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        fileWriter.flush();
        fileWriter.close();
    }

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        MultiDex.install(this);
        com.secneo.sdk.Helper.install(this);
        app = this;
    }


}