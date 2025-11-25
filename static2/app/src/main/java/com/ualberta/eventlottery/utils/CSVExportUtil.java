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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for exporting event entrant data to CSV format.
 * Provides functionality to export final lists of enrolled entrants for events.
 *
 * @author static2
 * @version 1.0
 */
public class CSVExportUtil {
    private static final String TAG = "CSVExportUtil";
    private static final String CONFIRMED_HEADER = "Name,Email,Phone,User ID";
    private static final String WAITING_LIST_HEADER = "Name,Email,Phone,User ID";

    /**
     * Exports the final list of confirmed entrants for an event to CSV format.
     *
     * @param context The application context for showing toasts and accessing storage
     * @param event The event containing confirmed entrants to export
     * @param callback Callback to handle export completion or errors
     */
    public static void exportConfirmedEntrants(Context context, Event event, ExportCallback callback) {
        if (context == null || event == null) {
            if (callback != null) callback.onFailure(new IllegalArgumentException("Context and Event cannot be null"));
            return;
        }

        List<String> confirmedUserIds = event.getConfirmedUserIds();
        if (confirmedUserIds == null || confirmedUserIds.isEmpty()) {
            String message = "No confirmed entrants to export for event: " + event.getTitle();
            Log.w(TAG, message);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onSuccess(null);
            return;
        }

        EntrantRepository entrantRepository = EntrantRepository.getInstance();

        // Fetch all entrant details
        entrantRepository.getAllEntrants(new EntrantRepository.EntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> allEntrants) {
                try {
                    // Filter to get only confirmed entrants for this event
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

                    // Save CSV file
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

    /**
     * Exports the waiting list entrants for an event to CSV format.
     *
     * @param context The application context for showing toasts and accessing storage
     * @param event The event containing waiting list entrants to export
     * @param callback Callback to handle export completion or errors
     */
    public static void exportWaitingListEntrants(Context context, Event event, ExportCallback callback) {
        if (context == null || event == null) {
            if (callback != null) callback.onFailure(new IllegalArgumentException("Context and Event cannot be null"));
            return;
        }

        List<String> waitListUserIds = event.getWaitListUserIds();
        if (waitListUserIds == null || waitListUserIds.isEmpty()) {
            String message = "No waiting list entrants to export for event: " + event.getTitle();
            Log.w(TAG, message);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onSuccess(null);
            return;
        }

        EntrantRepository entrantRepository = EntrantRepository.getInstance();

        // Fetch all entrant details
        entrantRepository.getAllEntrants(new EntrantRepository.EntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> allEntrants) {
                try {
                    // Filter to get only waiting list entrants for this event
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

                    // Save CSV file
                    String fileName = generateFileName(event, "waiting_list");
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

    /**
     * Formats an entrant's data as a CSV line.
     * Handles escaping of commas and quotes in the data.
     *
     * @param entrant The entrant to format
     * @return A formatted CSV line
     */
    private static String formatEntrantForCSV(Entrant entrant) {
        String name = escapeCSVField(entrant.getName());
        String email = escapeCSVField(entrant.getEmail());
        String phone = escapeCSVField(entrant.getPhone());
        String userId = escapeCSVField(entrant.getUserId());

        return String.format("%s,%s,%s,%s", name, email, phone, userId);
    }

    /**
     * Generates a filename for CSV export based on event details.
     *
     * @param event The event to generate a filename for
     * @param suffix Optional suffix to add to the filename (e.g., "confirmed", "waiting_list")
     * @return A generated filename
     */
    private static String generateFileName(Event event, String suffix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());

        // Clean event title to make it filename-safe
        String cleanTitle = event.getTitle() != null ?
                event.getTitle().replaceAll("[^a-zA-Z0-9\\s-]", "").trim() : "event";
        cleanTitle = cleanTitle.replaceAll("\\s+", "_");

        return String.format("%s_%s_%s.csv", cleanTitle, suffix, timestamp);
    }

    /**
     * Exports entrant data for a specific event to CSV format.
     *
     * @param context The application context
     * @param eventId The ID of the event to export
     * @param eventName The name of the event (used for filename)
     * @param registrations List of registrations for the event
     * @param callback Callback to handle the result
     */
    public static void exportEventEntrantsToCSV(Context context, String eventId, String eventName,
                                               List<Registration> registrations, ExportCallback callback) {
        // Create CSV content
        StringBuilder csvContent = new StringBuilder();

        // Add CSV header
        csvContent.append("Name,Email,Phone,Registration Status,Registration Date,Response Date,Cancellation Date\n");

        // Process each registration
        for (Registration registration : registrations) {
            // Get entrant details for each registration
            EntrantRepository.getInstance().findEntrantById(registration.getEntrantId(), new EntrantRepository.EntrantCallback() {
                @Override
                public void onSuccess(Entrant entrant) {
                    // Add entrant data to CSV
                    appendRegistrationData(csvContent, entrant, registration);

                    // Check if this is the last registration
                    if (registrations.indexOf(registration) == registrations.size() - 1) {
                        // Save the CSV file
                        saveCSVFile(context, csvContent.toString(), eventName, callback);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to load entrant data: " + registration.getEntrantId(), e);
                    // Still add the registration data without entrant details
                    appendRegistrationData(csvContent, null, registration);

                    // Check if this is the last registration
                    if (registrations.indexOf(registration) == registrations.size() - 1) {
                        // Save the CSV file
                        saveCSVFile(context, csvContent.toString(), eventName, callback);
                    }
                }
            });
        }

        // Handle case where there are no registrations
        if (registrations.isEmpty()) {
            saveCSVFile(context, csvContent.toString(), eventName, callback);
        }
    }

    /**
     * Appends registration data to the CSV content.
     *
     * @param csvContent The StringBuilder containing CSV content
     * @param entrant The entrant data (may be null)
     * @param registration The registration data
     */
    private static void appendRegistrationData(StringBuilder csvContent, Entrant entrant, Registration registration) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Add entrant data (or empty fields if entrant is null)
        String name = entrant != null ? escapeCSVField(entrant.getName()) : "";
        String email = entrant != null ? escapeCSVField(entrant.getEmail()) : "";
        String phone = entrant != null ? escapeCSVField(entrant.getPhone()) : "";

        // Add registration data
        String status = registration.getStatus() != null ? escapeCSVField(registration.getStatus().name()) : "";
        String registeredAt = registration.getRegisteredAt() != null ?
                escapeCSVField(dateFormat.format(registration.getRegisteredAt())) : "";
        String respondedAt = registration.getRespondedAt() != null ?
                escapeCSVField(dateFormat.format(registration.getRespondedAt())) : "";
        String cancelledAt = registration.getCancelledAt() != null ?
                escapeCSVField(dateFormat.format(registration.getCancelledAt())) : "";

        // Append the row to CSV
        csvContent.append(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                name, email, phone, status, registeredAt, respondedAt, cancelledAt));
    }

    /**
     * Escapes a field for CSV format.
     *
     * @param field The field to escape
     * @return The escaped field
     */
    private static String escapeCSVField(String field) {
        if (field == null) {
            return "";
        }

        // If field contains commas, quotes, or newlines, wrap in quotes and escape quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }

    /**
     * Saves the CSV content to a file.
     *
     * @param context The application context
     * @param csvContent The CSV content to save
     * @param fileName The name of the file to save
     * @param callback Callback to handle the result
     */
    private static void saveCSVFile(Context context, String csvContent, String fileName, ExportCallback callback) {
        try {
            // Get the downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File csvFile = new File(downloadsDir, fileName);

            // Write CSV content to file
            FileOutputStream fos = new FileOutputStream(csvFile);
            fos.write(csvContent.getBytes());
            fos.close();

            String successMessage = String.format("CSV file saved to: %s", csvFile.getAbsolutePath());
            Log.d(TAG, successMessage);

            // Show success message
            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show();

            callback.onSuccess(Uri.fromFile(csvFile));
        } catch (IOException e) {
            Log.e(TAG, "Failed to save CSV file", e);
            Toast.makeText(context, "Failed to save CSV file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            callback.onFailure(e);
        }
    }

    /**
     * Callback interface for export operations.
     */
    public interface ExportCallback {
        /**
         * Called when the export is successful.
         *
         * @param fileUri The URI of the exported file, or null if no data to export
         */
        void onSuccess(Uri fileUri);

        /**
         * Called when the export fails.
         *
         * @param e The exception that caused the failure
         */
        void onFailure(Exception e);
    }
}