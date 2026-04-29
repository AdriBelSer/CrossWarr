package com.yinya.crosswarr.network.models;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo de datos de red (Data Transfer Object) para los Usuarios.
 * Esta clase representa la estructura exacta de un documento de la colección de usuarios
 * en Firebase Firestore. Actúa como intermediario entre la base de datos en la nube y el
 * modelo de datos local de la aplicación ({@link UserData}).
 */
public class FirebaseUserData {
    private String uid;
    private String email;
    private String name;
    private String photo;
    private String role;
    private Timestamp accountCreationDate;
    private String notificationPushToken;
    private Map<String, Object> settings;
    private List<Map<String, Object>> challenges;

    /**
     * Constructor vacío por defecto.
     * Es un requisito estricto del SDK de Firebase Firestore para poder deserializar
     * la información descargada de la nube y convertirla en este objeto Java automáticamente.
     */
    public FirebaseUserData() {
    }

    /**
     * Constructor parcial para inicializar los datos básicos de un usuario recién registrado.
     *
     * @param uid                   Identificador único del usuario (proveniente de Firebase Auth).
     * @param email                 Correo electrónico vinculado a la cuenta.
     * @param name                  Nombre de visualización.
     * @param role                  Rol dentro del sistema ("admin" o "user").
     * @param accountCreationDate   Fecha y hora de creación de la cuenta.
     * @param notificationPushToken Token de Firebase Cloud Messaging para enviar notificaciones.
     */
    public FirebaseUserData(String uid, String email, String name, String role, Timestamp accountCreationDate, String notificationPushToken) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.role = role;
        this.accountCreationDate = accountCreationDate;
        this.notificationPushToken = notificationPushToken;
    }

    /**
     * Constructor completo para estructurar el perfil de un usuario con todos sus datos,
     * incluyendo sus configuraciones y el historial de retos.
     *
     * @param uid                   Identificador único del usuario.
     * @param email                 Correo electrónico.
     * @param name                  Nombre de visualización.
     * @param photo                 URL de la foto de perfil.
     * @param role                  Rol en el sistema.
     * @param accountCreationDate   Fecha de registro.
     * @param notificationPushToken Token de notificaciones push.
     * @param settings              Mapa de configuraciones (ej. preferencias de la app).
     * @param challenges            Lista de retos completados por el usuario.
     */
    public FirebaseUserData(String uid, String email, String name, String photo, String role, Timestamp accountCreationDate, String notificationPushToken, Map<String, Object> settings, List<Map<String, Object>> challenges) {
        this(uid, email, name, role, accountCreationDate, notificationPushToken);
        this.photo = photo;
        this.settings = settings;
        this.challenges = challenges;
    }

    // --- GETTERS Y SETTERS ---

    /**
     * @return El identificador único del usuario.
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid El nuevo identificador a asignar.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return El correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email El nuevo correo a asignar.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return El nombre de visualización del usuario.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name El nuevo nombre a asignar.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return La URL de la foto de perfil.
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * @param photo La nueva URL de la foto a asignar.
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * @return El rol del usuario ("admin" / "user").
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role El nuevo rol a asignar.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return La fecha de creación de la cuenta en formato Timestamp de Firebase.
     */
    public Timestamp getAccountCreationDate() {
        return accountCreationDate;
    }

    /**
     * @param accountCreationDate La nueva fecha de creación a asignar.
     */
    public void setAccountCreationDate(Timestamp accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    /**
     * @return El token de notificaciones push del dispositivo.
     */
    public String getNotificationPushToken() {
        return notificationPushToken;
    }

    /**
     * @param notificationPushToken El nuevo token de notificaciones a asignar.
     */
    public void setNotificationPushToken(String notificationPushToken) {
        this.notificationPushToken = notificationPushToken;
    }

    /**
     * @return Mapa con las configuraciones del usuario.
     */
    public Map<String, Object> getSettings() {
        return settings;
    }

    /**
     * @param settings El nuevo mapa de configuraciones a asignar.
     */
    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    /**
     * @return Lista de mapas con el historial de desafíos completados.
     */
    public List<Map<String, Object>> getChallenges() {
        return challenges;
    }

    /**
     * @param challenges La nueva lista de desafíos a asignar.
     */
    public void setChallenges(List<Map<String, Object>> challenges) {
        this.challenges = challenges;
    }

    // --- MÉTODOS DE CONVERSIÓN ---

    /**
     * Convierte este modelo de red (Firebase) al modelo de negocio local ({@link UserData}).
     * Se utiliza inmediatamente después de descargar los datos de la base de datos para
     * alimentar la interfaz de usuario con objetos locales.
     *
     * @return Una instancia de UserData con todos los valores de este documento.
     */
    public UserData asUserData() {
        UserData userData = new UserData(this.uid, this.email, this.name, this.photo, this.role, this.accountCreationDate, this.notificationPushToken, this.settings, this.challenges);
        return userData;
    }

    /**
     * Empaqueta todos los atributos de este objeto en un {@link HashMap}.
     * Es ideal para realizar operaciones de actualización parcial (update) en Firestore.
     * Incluye inicializaciones seguras (null checks) para la foto, los ajustes y los retos,
     * evitando que se envíen valores nulos que puedan corromper la base de datos.
     *
     * @return Un mapa que contiene los nombres de los campos como claves y sus respectivos valores seguros.
     */
    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("uid", this.uid);
        hashMap.put("email", this.email);
        hashMap.put("name", this.name);

        // Prevención de nulos para la foto de perfil
        if (this.photo != null) {
            hashMap.put("photo", this.photo);
        } else {
            hashMap.put("photo", "");
        }
        hashMap.put("role", this.role);
        hashMap.put("accountCreationDate", this.accountCreationDate);
        hashMap.put("notificationPushToken", this.notificationPushToken);

        // Inicialización segura para mapas y listas vacías
        if (this.settings != null) {
            hashMap.put("settings", this.settings);
        } else {
            hashMap.put("settings", new HashMap<String, Object>());
        }
        if (this.challenges != null) {
            hashMap.put("challenges", this.challenges);
        } else {
            hashMap.put("challenges", new ArrayList<Map<String, Object>>());
        }
        return hashMap;
    }
}
