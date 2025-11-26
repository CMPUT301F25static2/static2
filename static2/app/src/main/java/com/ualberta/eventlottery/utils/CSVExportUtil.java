package com.ualberta.eventlottery.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ualberta.eventlottery.model.Entrant;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EntrantRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CSVExportUtil {
    private static final String TAG = "CSVExportUtil";
    private static final String CONFIRMED_HEADER = "Name,Email,Phone,User ID";
    private static final String WAITING_LIST_HEADER = "Name,Email,Phone,User ID";

    public static void exportConfirmedEntrants(Context context, Event event, ExportCallback callback) {
        if (context == null || event == null) {
            if (callback != null) callback.onFailure(new IllegalArgumentException("Context and Event cannot be null"));
            return;
        }

        RegistrationRepository.getInstance().getConfirmedRegistrationsByEvent(event.getId(), new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> confirmedRegistrations) {
                if (confirmedRegistrations == null || confirmedRegistrations.isEmpty()) {
                    String message = "No confirmed entrants to export for event: " + event.getTitle();
                    Log.w(TAG, message);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onSuccess(null);
                    return;
                }

                List<String> confirmedUserIds = new ArrayList<>();
                for (Registration registration : confirmedRegistrations) {
                    confirmedUserIds.add(registration.getEntrantId());
                }

                EntrantRepository.getInstance().getAllEntrants(new EntrantRepository.EntrantListCallback() {
                    @Override
                    public void onSuccess(List<Entrant> allEntrants) {
                        try {
                            StringBuilder csvContent = new StringBuilder();
                            csvContent.append(CONFIRMED_HEADER).append("\n");

                            int exportedCount = 0;
                            for (Entrant entrant : allEntrants) {
                                if (confirmedUserIds.contains(entrant.getUserId())) {
                                    csvContent.append(formatEntrantForCSV(entrant)).append("\n");
                                    exportedCount++;
                                }
                            }

                            if (exportedCount == 0) {
                                String message = "No entrant details found for confirmed users in event: " + event.getTitle();
                                Log.w(TAG, message);
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                if (callback != null) callback.onSuccess(null);
                                return;
                            }

                            String fileName = generateFileName(event, "confirmed");
                            saveCSVFile(context, csvContent.toString(), fileName, callback);

                        } catch (Exception e) {
                            Log.e(TAG, "Error creating CSV for event: " + event.getTitle(), e);
                            Toast.makeText(context, "Failed to create CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            if (callback != null) callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to fetch entrant data for CSV export", e);
                        Toast.makeText(context, "Failed to fetch entrant data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        if (callback != null) callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                String message = "Failed to fetch confirmed entrants: " + e.getMessage();
                Log.e(TAG, message, e);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if (callback != null) callback.onFailure(e);
            }
        });
    }

    public static void exportWaitingListEntrants(Context context, Event event, ExportCallback callback) {
        if (context == null || event == null) {
            if (callback != null) callback.onFailure(new IllegalArgumentException("Context and Event cannot be null"));
            return;
        }

        RegistrationRepository.getInstance().getWaitingRegistrationsByEvent(event.getId(), new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> waitingRegistrations) {
                if (waitingRegistrations == null || waitingRegistrations.isEmpty()) {
                    String message = "No waiting list entrants to export for event: " + event.getTitle();
                    Log.w(TAG, message);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onSuccess(null);
                    return;
                }

                List<String> waitListUserIds = new ArrayList<>();
                for (Registration registration : waitingRegistrations) {
                    waitListUserIds.add(registration.getEntrantId());
                }

                EntrantRepository.getInstance().getAllEntrants(new EntrantRepository.EntrantListCallback() {
                    @Override
                    public void onSuccess(List<Entrant> allEntrants) {
                        try {
                            StringBuilder csvContent = new StringBuilder();
                            csvContent.append(WAITING_LIST_HEADER).append("\n");

                            int exportedCount = 0;
                            for (Entrant entrant : allEntrants) {
                                if (waitListUserIds.contains(entrant.getUserId())) {
                                    csvContent.append(formatEntrantForCSV(entrant)).append("\n");
                                    exportedCount++;
                                }
                            }

                            if (exportedCount == 0) {
                                String message = "No entrant details found for waiting list users in event: " + event.getTitle();
                                Log.w(TAG, message);
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                if (callback != null) callback.onSuccess(null);
                                return;
                            }

                            String fileName = generateFileName(event, "confirmed");
                            saveCSVFile(context, csvContent.toString(), fileName, callback);

                        } catch (Exception e) {
                            Log.e(TAG, "Error creating CSV for event: " + event.getTitle(), e);
                            Toast.makeText(context, "Failed to create CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            if (callback != null) callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to fetch entrant data for CSV export", e);
                        Toast.makeText(context, "Failed to fetch entrant data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        if (callback != null) callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                String message = "Failed to fetch waiting list: " + e.getMessage();
                Log.e(TAG, message, e);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if (callback != null) callback.onFailure(e);
            }
        });
    }

    private static String formatEntrantForCSV(Entrant entrant) {
        String name = escapeCSVField(entrant.getName());
        String email = escapeCSVField(entrant.getEmail());
        String phone = escapeCSVField(entrant.getPhone());
        String userId = escapeCSVField(entrant.getUserId());

        return String.format("%s,%s,%s,%s", name, email, phone, userId);
    }

    private static String generateFileName(Event event, String suffix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());

        String cleanTitle = event.getTitle() != null ?
                event.getTitle().replaceAll("[^a-zA-Z0-9\\s-]", "").trim() : "event";
        cleanTitle = cleanTitle.replaceAll("\\s+", "_");

        return String.format("%s_%s_%s.csv", cleanTitle, suffix, timestamp);
    }

    public static void exportEventEntrantsToCSV(Context context, String eventId, String eventName,
                                               List<Registration> registrations, ExportCallback callback) {
        StringBuilder csvContent = new StringBuilder();

        csvContent.append("Name,Email,Phone,Registration Status,Registration Date,Response Date,Cancellation Date\n");

        for (Registration registration : registrations) {
            EntrantRepository.getInstance().findEntrantById(registration.getEntrantId(), new EntrantRepository.EntrantCallback() {
                @Override
                public void onSuccess(Entrant entrant) {
                    appendRegistrationData(csvContent, entrant, registration);

                    if (registrations.indexOf(registration) == registrations.size() - 1) {
                        saveCSVFile(context, csvContent.toString(), eventName, callback);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to load entrant data: " + registration.getEntrantId(), e);
                    appendRegistrationData(csvContent, null, registration);

                    if (registrations.indexOf(registration) == registrations.size() - 1) {
                        saveCSVFile(context, csvContent.toString(), eventName, callback);
                    }
                }
            });
        }

        if (registrations.isEmpty()) {
            saveCSVFile(context, csvContent.toString(), eventName, callback);
        }
    }

    private static void appendRegistrationData(StringBuilder csvContent, Entrant entrant, Registration registration) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String name = entrant != null ? escapeCSVField(entrant.getName()) : "";
        String email = entrant != null ? escapeCSVField(entrant.getEmail()) : "";
        String phone = entrant != null ? escapeCSVField(entrant.getPhone()) : "";

        String status = registration.getStatus() != null ? escapeCSVField(registration.getStatus().name()) : "";
        String registeredAt = registration.getRegisteredAt() != null ?
                escapeCSVField(dateFormat.format(registration.getRegisteredAt())) : "";
        String respondedAt = registration.getRespondedAt() != null ?
                escapeCSVField(dateFormat.format(registration.getRespondedAt())) : "";
        String cancelledAt = registration.getCancelledAt() != null ?
                escapeCSVField(dateFormat.format(registration.getCancelledAt())) : "";

        csvContent.append(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                name, email, phone, status, registeredAt, respondedAt, cancelledAt));
    }

    private static String escapeCSVField(String field) {
        if (field == null) {
            return "";
        }

        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }

    private static void saveCSVFile(Context context, String csvContent, String fileName, ExportCallback callback) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File csvFile = new File(downloadsDir, fileName);

            FileOutputStream fos = new FileOutputStream(csvFile);
            fos.write(csvContent.getBytes());
            fos.close();

            String successMessage = String.format("CSV file saved to: %s", csvFile.getAbsolutePath());
            Log.d(TAG, successMessage);

            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show();

            callback.onSuccess(Uri.fromFile(csvFile));
        } catch (IOException e) {
            Log.e(TAG, "Failed to save CSV file", e);
            Toast.makeText(context, "Failed to save CSV file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            callback.onFailure(e);
        }
    }

    public interface ExportCallback {
        void onSuccess(Uri fileUri);
        void onFailure(Exception e);
    }
}
