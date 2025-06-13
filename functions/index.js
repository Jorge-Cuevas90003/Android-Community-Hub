const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Envía notificaciones push cuando se agrega una noticia
exports.sendNewsNotification = functions.database.ref("/news/{newsId}")
    .onCreate((snapshot, context) => {
        const news = snapshot.val(); // Obtiene el contenido de la noticia

        const message = {
            notification: {
                title: "Nueva Noticia",
                body: news,
            },
            topic: "news", // Tema al que los usuarios están suscritos
        };

        // Envía la notificación
        return admin.messaging().send(message)
            .then((response) => {
                console.log("Notificación enviada exitosamente:", response);
                return null;
            })
            .catch((error) => {
                console.error("Error enviando notificación:", error);
            });
    });
