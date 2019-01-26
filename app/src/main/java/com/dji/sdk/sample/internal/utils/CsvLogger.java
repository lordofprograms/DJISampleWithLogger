package com.dji.sdk.sample.internal.utils;

import android.content.Context;
import android.support.annotation.NonNull;

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

import static com.dji.sdk.sample.internal.utils.ModuleVerificationUtil.getFlightController;

public class CsvLogger {
    private static CsvLogger csvLogger;
    private Context context;

    public static CsvLogger getInstance(Context context) {
        if (csvLogger == null) {
            csvLogger = new CsvLogger(context);
        }
        return csvLogger;
    }

    private CsvLogger(Context context) {
        this.context = context;
    }

    private File getLogFile() {
        File directory = new File(context.getFilesDir(), "DJILog");
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

}
