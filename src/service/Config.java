package service;

public class Config {
    // URL de base de l'API
    public static final String API_BASE_URL = "http://localhost:8000/api";
    
    // Endpoints spécifiques
    public static final String LOGIN_ENDPOINT = API_BASE_URL + "/login";
    public static final String PENSIONS_ENDPOINT = API_BASE_URL + "/pension";
    public static final String PROPRIETAIRES_ENDPOINT = API_BASE_URL + "/proprietaire";
    public static final String ANIMAUX_ENDPOINT = API_BASE_URL + "/animal";
    public static final String HEBERGEMENTS_ENDPOINT = API_BASE_URL + "/hebergement";
    public static final String BOX_ENDPOINT = API_BASE_URL + "/boxes";
    public static final String TARIFICATION_ENDPOINT = API_BASE_URL + "/tarification";
    
    // Headers
    public static final String CONTENT_TYPE = "application/json";
    public static final String ACCEPT = "application/json";
}
