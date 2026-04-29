package com.yinya.crosswarr.models;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.network.models.FirebaseUserData;

import java.util.List;
import java.util.Map;

/**
 * Modelo de datos que representa a un Usuario (Usuario o Administrador) de la aplicación.
 * Esta clase se utiliza a nivel local para gestionar el perfil del usuario,
 * sus preferencias y su historial de desafíos completados.
 */
public class UserData {
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
     * Es estrictamente necesario para que Firebase Firestore pueda deserializar
     * los documentos de la base de datos y convertirlos en objetos Java automáticamente.
     */
    public UserData() {
    }

    /**
     * Constructor parcial para inicializar un usuario básico al momento del registro.
     *
     * @param uid                   Identificador único del usuario proporcionado por Firebase Authentication.
     * @param email                 Correo electrónico vinculado a la cuenta.
     * @param name                  Nombre de visualización del usuario.
     * @param role                  Rol dentro de la app (ej. "admin" o "user").
     * @param accountCreationDate   Fecha y hora exacta de la creación de la cuenta.
     * @param notificationPushToken Token único del dispositivo (FCM) utilizado para enviar notificaciones push.
     */
    public UserData(String uid, String email, String name, String role, Timestamp accountCreationDate, String notificationPushToken) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.role = role;
        this.accountCreationDate = accountCreationDate;
        this.notificationPushToken = notificationPushToken;
    }

    /**
     * Constructor completo que abarca todos los datos del perfil, incluyendo configuración e historial.
     *
     * @param uid                   Identificador único del usuario.
     * @param email                 Correo electrónico del usuario.
     * @param name                  Nombre de visualización.
     * @param photo                 URL de la foto de perfil del usuario.
     * @param role                  Rol asignado ("admin" / "user").
     * @param accountCreationDate   Fecha de registro en la app.
     * @param notificationPushToken Token de notificaciones del dispositivo.
     * @param settings              Mapa clave-valor con las preferencias de la app (ej. idioma, tema oscuro).
     * @param challenges            Lista de mapas que contiene el historial de desafíos completados y sus tiempos.
     */
    public UserData(String uid, String email, String name, String photo, String role, Timestamp accountCreationDate, String notificationPushToken, Map<String, Object> settings, List<Map<String, Object>> challenges) {
        this(uid, email, name, role, accountCreationDate, notificationPushToken);
        this.photo = photo;
        this.settings = settings;
        this.challenges = challenges;
    }

    // --- GETTERS Y SETTERS ---

    /**
     * @return El identificador único (UID) del usuario.
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid El nuevo identificador único a asignar.
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
     * @param photo La nueva URL de foto de perfil a asignar.
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * @return El rol actual del usuario en el sistema.
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role El nuevo rol a asignar ("user" o "admin").
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return El Timestamp de Firebase con la fecha de registro.
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
     * @return El token de Firebase Cloud Messaging para notificaciones.
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
     * @return Un mapa con las configuraciones personalizadas del usuario.
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
     * @return Una lista con el historial de retos completados por el usuario.
     */
    public List<Map<String, Object>> getChallenges() {
        return challenges;
    }

    /**
     * @param challenges La nueva lista de retos a asignar.
     */
    public void setChallenges(List<Map<String, Object>> challenges) {
        this.challenges = challenges;
    }

    // --- MÉTODOS DE CONVERSIÓN ---

    /**
     * Convierte este modelo local ({@link UserData}) en un modelo específico
     * para la capa de base de datos en Firebase ({@link FirebaseUserData}).
     * Separa la información utilizada en la interfaz de usuario de la que viaja por la red.
     *
     * @return Una nueva instancia de FirebaseUserData con los valores mapeados.
     */
    public FirebaseUserData asFirebaseUserData() {
        FirebaseUserData firebaseUserData = new FirebaseUserData(this.uid, this.email, this.name, this.photo, this.role, this.accountCreationDate, this.notificationPushToken, this.settings, this.challenges);
        return firebaseUserData;
    }
}
