package com.example.testapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;;
import android.app.job.JobService;
import android.util.Log;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


//import static com.example.testapp.MainActivity.DataPrevious;
import static com.example.testapp.NotApp.CHANNEL_ID;
import static com.example.testapp.MainActivity.FuelType;

public class PriceUpdateService extends JobService {
    public static final String TAG = "PriceUpdateService";
    private boolean jobCancelled = false;
    private NotificationManagerCompat notificationManager;
    private List<String> DataToDisplay;
    private List<String> DataPrevious;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        logTimestamp();

        notificationManager = NotificationManagerCompat.from(this);
        GetDataFromNeste(params);
        return true;
    }

    public void showNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("The best price for you sir")
                .setContentText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_money)
                .build();
        notificationManager.notify(1,notification);

    }

    public void logTimestamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        Log.d("MainActivity", "Current Timestamp: " + format);
    }

    public void GetDataFromNeste(final JobParameters params) {

        final GetDataFromServer getDataFromServer = new GetDataFromServer();

        new Thread(new Runnable(){
            @Override
            public void run() {


                getDataFromServer.fetch(FuelType);

                DataToDisplay = getDataFromServer.get();

                Log.d(TAG, "Here's what we've fetched: " + DataToDisplay.get(0) + "   " + DataToDisplay.get(1) + "   " + DataToDisplay.get(2) + "   " + DataToDisplay.get(3));
                logTimestamp();

                if (DataToDisplay.equals(DataPrevious)) { // TODO consider refactoring this. ALSO DataPrevious = Null if we checked price with button.

                }else{
                    showNotification();
                }

                DataPrevious = DataToDisplay;
                //TODO Compating to DB value shoud happen here, as this thread will be only thing that is active in the background

            }
        }).start();

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled");
        jobCancelled = true;
        return true;
    }

    public void setDataPrevious(List<String> DataPreviousFromButton){
        DataPrevious = DataPreviousFromButton;
    }

}
