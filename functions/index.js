const functions = require("firebase-functions");
const admin = require("firebase-admin");
const {initializeApp} = require("firebase-admin/app");

initializeApp();
const fcm = admin.messaging();

// function 1: send notification to a single device
exports.sendNotification = functions.https.onCall((request) => {
  const {token, title, body, eventId} = request.data;
  if (!token) {
    throw new functions.https.HttpsError(
        "invalid-argument",
        "Missing target device token.",
    );
  }
  const message = {
    data: {
      title: title || "",
      body: body || "",
      eventId: eventId || "",
    },
    token: token,
  };

  fcm.send(message)
      .then((response) => {
        // Response is a message ID string.
        console.log("Successfully sent message:", response);
      })
      .catch((error) => {
        console.log("Error sending message:", error);
      });
},
);

// function 2: send notification to multiple devices
exports.sendMultipleNotifications = functions.https.onCall((request) => {
  const {tokens, title, body, eventId} = request.data;
  if (!tokens) {
    throw new functions.https.HttpsError(
        "invalid-argument",
        "Missing target device token.",
    );
  }
  const message = {
    data: {
      title: title || "",
      body: body || "",
      eventId: eventId || "",
    },
    tokens: tokens,
    android: {
      priority: "high",
    },
  };

  fcm.sendEachForMulticast(message)
      .then((response) => {
        if (response.failureCount > 0) {
          const failedTokens = [];
          response.responses.forEach((resp, idx) => {
            if (!resp.success) {
              failedTokens.push(tokens[idx]);
            }
          });
          console.log("List of tokens that caused failures: " + failedTokens);
        }
        console.log("Successfully sent message:", response);
      });
},
);
