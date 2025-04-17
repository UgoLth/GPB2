package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import modele.*;

public class ApiService {
    private static final Logger LOGGER = Logger.getLogger(ApiService.class.getName());
    private final OkHttpClient client = new OkHttpClient();
    private String authToken;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static ApiService instance; // Instance unique

    private static final String BASE_URL = "http://localhost:8000/api";
    private static final String LOGIN_ENDPOINT = BASE_URL + "/login";
    private static final String PENSION_ENDPOINT = BASE_URL + "/pension";
    private static final String PROPRIETAIRE_ENDPOINT = BASE_URL + "/proprietaires";
    private static final String ANIMAL_ENDPOINT = BASE_URL + "/animals";
    private static final String BOX_ENDPOINT = BASE_URL + "/boxes";
    private static final String HEBERGEMENT_ENDPOINT = BASE_URL + "/hebergement";
    private static final String TARIFICATION_ENDPOINT = BASE_URL + "/tarification";
    private static final String TYPE_GARDIENNAGE_ENDPOINT = BASE_URL + "/type-gardiennage";
    private static final String ACCEPT = "application/json";
    private static final String CONTENT_TYPE = "application/json";

    private ApiService() {
    }

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public static void setAuthToken(String token) {
        getInstance().authToken = token;
    }

    // Authentification utilisateur
    public String loginUser(String email, String password) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        RequestBody body = RequestBody.create(
            json.toString(),
            JSON
        );

        Request request = new Request.Builder()
            .url(LOGIN_ENDPOINT)
            .post(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Content-Type", CONTENT_TYPE)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Échec de l'authentification");
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);
            String token = jsonResponse.get("token").getAsString();
            setAuthToken(token);
            return responseBody;
        }
    }

    // Gestion des Propriétaires
    public List<Proprietaire> getProprietaires() throws IOException {
        Request request = new Request.Builder()
            .url(PROPRIETAIRE_ENDPOINT)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOGGER.warning("Erreur lors de la récupération des propriétaires : " + response.code());
                return null;
            }
            String responseBody = response.body().string();
            LOGGER.info("Response body: " + responseBody);
            Type listType = new TypeToken<List<Proprietaire>>(){}.getType();
            return new Gson().fromJson(responseBody, listType);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des propriétaires : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean ajouterProprietaire(Proprietaire proprietaire) throws IOException {
        String json = new Gson().toJson(proprietaire);
        LOGGER.info("Envoi de la requête d'ajout de propriétaire: " + json);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(PROPRIETAIRE_ENDPOINT)
            .post(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Content-Type", CONTENT_TYPE)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Réponse de l'API: " + responseBody);
            if (!response.isSuccessful()) {
                LOGGER.warning("Erreur lors de l'ajout du propriétaire. Code: " + response.code() + ", Message: " + responseBody);
            }
            return response.isSuccessful();
        }
    }

    public boolean modifierProprietaire(Proprietaire proprietaire) throws IOException {
        String json = new Gson().toJson(proprietaire);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(PROPRIETAIRE_ENDPOINT + "/" + proprietaire.getId())
            .put(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public boolean supprimerProprietaire(int id) throws IOException {
        Request request = new Request.Builder()
            .url(PROPRIETAIRE_ENDPOINT + "/" + id)
            .delete()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public List<Animal> getAnimaux(int proprietaireId) throws IOException {
        Request request = new Request.Builder()
            .url(ANIMAL_ENDPOINT + "?proprietaire_id=" + proprietaireId)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            String responseBody = response.body().string();
            LOGGER.info("Response body: " + responseBody);
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray animauxArray = jsonResponse.getAsJsonArray("data");
            List<Animal> animaux = new ArrayList<>();
            
            for (JsonElement element : animauxArray) {
                JsonObject animalObj = element.getAsJsonObject();
                Animal animal = new Animal();
                animal.setId(animalObj.get("id").getAsInt());
                animal.setNom(animalObj.get("nom").getAsString());
                animal.setRace(animalObj.has("race") ? animalObj.get("race").getAsString() : "");
                
                if (animalObj.has("date_naissance") && !animalObj.get("date_naissance").isJsonNull()) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        animal.setDateNaissance(format.parse(animalObj.get("date_naissance").getAsString()));
                    } catch (ParseException e) {
                        LOGGER.warning("Erreur lors du parsing de la date: " + e.getMessage());
                    }
                }
                
                animal.setEspeceId(animalObj.get("espece_id").getAsInt());
                animal.setProprietaireId(animalObj.get("proprietaire_id").getAsInt());
                
                JsonObject especeObj = animalObj.getAsJsonObject("espece");
                if (especeObj != null && especeObj.has("nom")) {
                    animal.setEspeceNom(especeObj.get("nom").getAsString());
                }
                
                animaux.add(animal);
            }
            return animaux;
        }
    }

    public boolean ajouterAnimal(Animal animal) throws IOException {
        JsonObject jsonAnimal = new JsonObject();
        jsonAnimal.addProperty("nom", animal.getNom());
        jsonAnimal.addProperty("race", animal.getRace());
        jsonAnimal.addProperty("espece_id", animal.getEspeceId());
        jsonAnimal.addProperty("proprietaire_id", animal.getProprietaireId());
        
        if (animal.getDateNaissance() != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            jsonAnimal.addProperty("date_naissance", format.format(animal.getDateNaissance()));
        }

        String json = jsonAnimal.toString();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(ANIMAL_ENDPOINT)
            .post(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public boolean modifierAnimal(Animal animal) throws IOException {
        JsonObject jsonAnimal = new JsonObject();
        jsonAnimal.addProperty("nom", animal.getNom());
        jsonAnimal.addProperty("race", animal.getRace());
        jsonAnimal.addProperty("espece_id", animal.getEspeceId());
        jsonAnimal.addProperty("proprietaire_id", animal.getProprietaireId());
        
        if (animal.getDateNaissance() != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            jsonAnimal.addProperty("date_naissance", format.format(animal.getDateNaissance()));
        }

        String json = jsonAnimal.toString();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(ANIMAL_ENDPOINT + "/" + animal.getId())
            .put(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public boolean supprimerAnimal(int id) throws IOException {
        Request request = new Request.Builder()
            .url(ANIMAL_ENDPOINT + "/" + id)
            .delete()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    // Gestion des Animaux
    public List<Animal> getAnimauxList(int proprietaireId) throws IOException {
        Request request = new Request.Builder()
            .url(ANIMAL_ENDPOINT + "?proprietaire_id=" + proprietaireId)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray animauxArray = jsonResponse.getAsJsonArray("data");
            List<Animal> animaux = new ArrayList<>();
            
            for (JsonElement element : animauxArray) {
                JsonObject animalObj = element.getAsJsonObject();
                Animal animal = new Animal();
                animal.setId(animalObj.get("id").getAsInt());
                animal.setNom(animalObj.get("nom").getAsString());
                animal.setRace(animalObj.has("race") ? animalObj.get("race").getAsString() : "");
                animal.setEspeceId(animalObj.get("espece_id").getAsInt());
                animal.setProprietaireId(animalObj.get("proprietaire_id").getAsInt());
                
                if (animalObj.has("date_naissance") && !animalObj.get("date_naissance").isJsonNull()) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        animal.setDateNaissance(format.parse(animalObj.get("date_naissance").getAsString()));
                    } catch (ParseException e) {
                        LOGGER.warning("Erreur lors du parsing de la date: " + e.getMessage());
                    }
                }
                
                JsonObject especeObj = animalObj.getAsJsonObject("espece");
                if (especeObj != null && especeObj.has("nom")) {
                    animal.setEspeceNom(especeObj.get("nom").getAsString());
                }
                
                animaux.add(animal);
            }
            return animaux;
        }
    }

    public boolean ajouterAnimalList(Animal animal) throws IOException {
        JsonObject jsonAnimal = new JsonObject();
        jsonAnimal.addProperty("nom", animal.getNom());
        jsonAnimal.addProperty("race", animal.getRace());
        jsonAnimal.addProperty("espece_id", animal.getEspeceId());
        jsonAnimal.addProperty("proprietaire_id", animal.getProprietaireId());
        
        if (animal.getDateNaissance() != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            jsonAnimal.addProperty("date_naissance", format.format(animal.getDateNaissance()));
        }

        String json = jsonAnimal.toString();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(ANIMAL_ENDPOINT)
            .post(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public boolean modifierAnimalList(Animal animal) throws IOException {
        JsonObject jsonAnimal = new JsonObject();
        jsonAnimal.addProperty("nom", animal.getNom());
        jsonAnimal.addProperty("race", animal.getRace());
        jsonAnimal.addProperty("espece_id", animal.getEspeceId());
        jsonAnimal.addProperty("proprietaire_id", animal.getProprietaireId());
        
        if (animal.getDateNaissance() != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            jsonAnimal.addProperty("date_naissance", format.format(animal.getDateNaissance()));
        }

        String json = jsonAnimal.toString();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(ANIMAL_ENDPOINT + "/" + animal.getId())
            .put(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public boolean supprimerAnimalList(int id) throws IOException {
        Request request = new Request.Builder()
            .url(ANIMAL_ENDPOINT + "/" + id)
            .delete()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Delete animal response: " + responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete animal: " + response.code() + " " + responseBody);
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting animal", e);
            throw new IOException("Failed to delete animal: " + e.getMessage(), e);
        }
    }

    // Gestion des Pensions
    public List<Pension> getPensions() throws IOException {
        Request request = new Request.Builder()
            .url(PENSION_ENDPOINT)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get pensions: " + response.code());
            }

            String responseBody = response.body().string();
            LOGGER.info("Raw response: " + responseBody);
            
            try {
                responseBody = responseBody
                    .replace("\\n", "")
                    .replace("\\r", "")
                    .replace("\n", "")
                    .replace("\r", "")
                    .trim();

                if (responseBody.startsWith("[") && responseBody.endsWith("]")) {
                    return new Gson().fromJson(responseBody, new TypeToken<List<Pension>>(){}.getType());
                } else {
                    JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);
                    if (jsonResponse.has("data")) {
                        JsonElement dataElement = jsonResponse.get("data");
                        if (dataElement.isJsonArray()) {
                            return new Gson().fromJson(dataElement, new TypeToken<List<Pension>>(){}.getType());
                        }
                    }
                }
                
                throw new IOException("Format de réponse invalide");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors du parsing de la réponse: " + responseBody, e);
                throw new IOException("Erreur lors du parsing des données: " + e.getMessage());
            }
        }
    }

    public boolean ajouterPension(Pension pension) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("Adresse", pension.getAdresse());
        json.addProperty("Ville", pension.getVille());
        json.addProperty("Responsable", pension.getResponsable());
        json.addProperty("Telephone", pension.getTelephone());

        LOGGER.info("Sending JSON: " + json.toString());

        RequestBody body = RequestBody.create(
            json.toString(),
            JSON
        );

        Request request = new Request.Builder()
            .url(PENSION_ENDPOINT)
            .post(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Add pension response: " + responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("Failed to add pension: " + response.code() + " " + responseBody);
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding pension", e);
            throw new IOException("Failed to add pension: " + e.getMessage(), e);
        }
    }

    public boolean modifierPension(Pension pension) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("Adresse", pension.getAdresse());
        json.addProperty("Ville", pension.getVille());
        json.addProperty("Responsable", pension.getResponsable());
        json.addProperty("Telephone", pension.getTelephone());

        RequestBody body = RequestBody.create(
            json.toString(),
            JSON
        );

        Request request = new Request.Builder()
            .url(PENSION_ENDPOINT + "/" + pension.getId())
            .put(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Update pension response: " + responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update pension: " + response.code() + " " + responseBody);
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating pension", e);
            throw new IOException("Failed to update pension: " + e.getMessage(), e);
        }
    }

    public boolean supprimerPension(int id) throws IOException {
        Request request = new Request.Builder()
            .url(PENSION_ENDPOINT + "/" + id)
            .delete()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Delete pension response: " + responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete pension: " + response.code() + " " + responseBody);
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting pension", e);
            throw new IOException("Failed to delete pension: " + e.getMessage(), e);
        }
    }

    // Gestion des Hébergements
    public List<Hebergement> getHebergements(int pensionId) throws IOException {
        Request request = new Request.Builder()
            .url(HEBERGEMENT_ENDPOINT + "/pension/" + pensionId)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get hebergements: " + response.code());
            }

            String responseBody = response.body().string();
            LOGGER.info("Raw hebergements response: " + responseBody);
            
            try {
                responseBody = responseBody.trim();
                if (responseBody.startsWith("[") && responseBody.endsWith("]")) {
                    return new Gson().fromJson(responseBody, new TypeToken<List<Hebergement>>(){}.getType());
                } else {
                    JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);
                    if (jsonResponse.has("data")) {
                        JsonElement dataElement = jsonResponse.get("data");
                        if (dataElement.isJsonArray()) {
                            return new Gson().fromJson(dataElement, new TypeToken<List<Hebergement>>(){}.getType());
                        }
                    }
                }
                throw new IOException("Format de réponse invalide");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors du parsing de la réponse: " + responseBody, e);
                throw new IOException("Erreur lors du parsing des données: " + e.getMessage());
            }
        }
    }

    public boolean ajouterHebergement(Hebergement hebergement) throws IOException {
        JsonObject jsonHebergement = new JsonObject();
        jsonHebergement.addProperty("id", hebergement.getId());
        jsonHebergement.addProperty("typegardiennage_id", hebergement.getTypeGardiennageId());
        jsonHebergement.addProperty("description", hebergement.getDescription());
        jsonHebergement.addProperty("tarif", hebergement.getTarif());
        jsonHebergement.addProperty("pension_id", hebergement.getPensionId());

        String jsonBody = jsonHebergement.toString();
        LOGGER.info("Sending JSON: " + jsonBody);

        RequestBody body = RequestBody.create(
            jsonBody,
            JSON
        );

        Request request = new Request.Builder()
            .url(HEBERGEMENT_ENDPOINT)
            .post(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("API response: " + responseBody);
            
            if (!response.isSuccessful()) {
                LOGGER.severe("Erreur lors de l'ajout de l'hébergement. Code: " + response.code() + ", Message: " + responseBody);
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.severe("Exception lors de l'ajout de l'hébergement: " + e.getMessage());
            throw new IOException("Erreur lors de l'ajout de l'hébergement: " + e.getMessage());
        }
    }

    public boolean modifierHebergement(Hebergement hebergement) throws IOException {
        JsonObject jsonHebergement = new JsonObject();
        jsonHebergement.addProperty("typegardiennage_id", hebergement.getTypeGardiennageId());
        jsonHebergement.addProperty("description", hebergement.getDescription());
        jsonHebergement.addProperty("tarif", hebergement.getTarif());
        jsonHebergement.addProperty("pension_id", hebergement.getPensionId());

        RequestBody body = RequestBody.create(
            jsonHebergement.toString(),
            JSON
        );

        Request request = new Request.Builder()
            .url(HEBERGEMENT_ENDPOINT + "/" + hebergement.getId())
            .put(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public boolean supprimerHebergement(int id) throws IOException {
        Request request = new Request.Builder()
            .url(HEBERGEMENT_ENDPOINT + "/" + id)
            .delete()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Delete hebergement response: " + responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete hebergement: " + response.code() + " " + responseBody);
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting hebergement", e);
            throw new IOException("Failed to delete hebergement: " + e.getMessage(), e);
        }
    }

    // Gestion des Box
    public List<Box> getBoxes(int tarificationId) throws IOException {
        LOGGER.info("Récupération des boxes pour la tarification " + tarificationId);
        
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BOX_ENDPOINT).newBuilder();
        urlBuilder.addQueryParameter("tarification_id", String.valueOf(tarificationId));
        
        String url = urlBuilder.build().toString();
        LOGGER.info("URL de la requête: " + url);
        
        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Code de réponse: " + response.code());
            LOGGER.info("Réponse brute: " + responseBody);
            
            if (!response.isSuccessful()) {
                LOGGER.warning("Erreur lors de la récupération des boxes. Code: " + response.code() + ", Corps: " + responseBody);
                return null;
            }

            Type listType = new TypeToken<List<Box>>(){}.getType();
            List<Box> boxes = new Gson().fromJson(responseBody, listType);
            
            LOGGER.info("Nombre de boxes récupérés : " + (boxes != null ? boxes.size() : 0));
            return boxes;
        } catch (Exception e) {
            LOGGER.severe("Exception lors de la récupération des boxes: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean ajouterBox(Box box, int tarificationId) throws IOException {  
        LOGGER.info("Ajout d'un box pour la tarification " + tarificationId + " avec les données : " + 
                   "numero=" + box.getNumero() + ", " +
                   "taille=" + box.getTaille() + ", " +
                   "type=" + box.getType() + ", " +
                   "disponibilite=" + box.getDisponibilite() + ", " +
                   "tarificationId=" + box.getTarificationId());  
        
        JsonObject jsonBox = new JsonObject();
        jsonBox.addProperty("numero", box.getNumero());
        jsonBox.addProperty("taille", box.getTaille());
        jsonBox.addProperty("type", box.getType());
        jsonBox.addProperty("disponibilite", box.getDisponibilite());
        jsonBox.addProperty("tarification_id", tarificationId);  

        String jsonBody = jsonBox.toString();
        LOGGER.info("Envoi de la requête d'ajout de box: " + jsonBody);

        RequestBody body = RequestBody.create(jsonBody, JSON);
        
        Request request = new Request.Builder()
            .url(BOX_ENDPOINT)
            .post(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Content-Type", CONTENT_TYPE)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Réponse de l'API: " + responseBody);
            
            if (!response.isSuccessful()) {
                LOGGER.warning("Erreur lors de l'ajout du box : " + response.code() + " - " + responseBody);
                return false;
            }
            LOGGER.info("Box ajouté avec succès");
            return true;
        }
    }

    public boolean modifierBox(Box box) throws IOException {
        LOGGER.info("Modification du box " + box.getId());
        
        JsonObject jsonBox = new JsonObject();
        jsonBox.addProperty("numero", box.getNumero());
        jsonBox.addProperty("taille", box.getTaille());
        jsonBox.addProperty("type", box.getType());
        jsonBox.addProperty("disponibilite", box.getDisponibilite());
        jsonBox.addProperty("tarification_id", box.getTarificationId());  

        String jsonBody = jsonBox.toString();
        LOGGER.info("Envoi de la requête de modification de box: " + jsonBody);

        RequestBody body = RequestBody.create(jsonBody, JSON);
        
        Request request = new Request.Builder()
            .url(BOX_ENDPOINT + "/" + box.getId())
            .put(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Content-Type", CONTENT_TYPE)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Réponse de l'API: " + responseBody);
            
            if (!response.isSuccessful()) {
                LOGGER.warning("Erreur lors de la modification du box : " + response.code() + " - " + responseBody);
                return false;
            }
            LOGGER.info("Box modifié avec succès");
            return true;
        }
    }

    public boolean supprimerBox(int id) throws IOException {
        LOGGER.info("Suppression du box " + id);
        
        Request request = new Request.Builder()
            .url(BOX_ENDPOINT + "/" + id)
            .delete()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body().string();
                LOGGER.warning("Erreur lors de la suppression du box : " + response.code() + " - " + responseBody);
                return false;
            }
            LOGGER.info("Box supprimé avec succès");
            return true;
        }
    }

    // Gestion des Tarifications
    public List<Tarification> getTarifications(int pensionId) throws IOException {
        Request request = new Request.Builder()
            .url(TARIFICATION_ENDPOINT + "?pension_id=" + pensionId)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Réponse de l'API: " + responseBody);
            
            if (!response.isSuccessful()) {
                LOGGER.severe("Erreur lors de la récupération des tarifications. Code: " + response.code() + ", Message: " + responseBody);
                return null;
            }

            Type listType = new TypeToken<List<Tarification>>(){}.getType();
            return new Gson().fromJson(responseBody, listType);
        }
    }

    public boolean ajouterTarification(Tarification tarification) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("TypeGardiennage_id", tarification.getTypeGardiennageId());
        json.addProperty("Tarif", tarification.getTarif());
        json.addProperty("pension_id", tarification.getPensionId());

        String jsonBody = json.toString();
        LOGGER.info("Envoi de la requête d'ajout de tarification: " + jsonBody);
        LOGGER.info("URL: " + TARIFICATION_ENDPOINT);
        LOGGER.info("Headers: Accept=" + ACCEPT + ", Authorization=Bearer " + authToken);

        RequestBody body = RequestBody.create(
            jsonBody,
            JSON
        );

        Request request = new Request.Builder()
            .url(TARIFICATION_ENDPOINT)
            .post(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .addHeader("Content-Type", CONTENT_TYPE)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Réponse de l'API: " + responseBody);
            LOGGER.info("Code de réponse: " + response.code());
            LOGGER.info("Headers de réponse: " + response.headers());
            
            if (!response.isSuccessful()) {
                LOGGER.severe("Erreur lors de l'ajout de la tarification. Code: " + response.code() + ", Message: " + responseBody);
                return false;
            }
            return true;
        }
    }

    public boolean modifierTarification(Tarification tarification) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("TypeGardiennage_id", tarification.getTypeGardiennageId());
        json.addProperty("Tarif", tarification.getTarif());
        json.addProperty("pension_id", tarification.getPensionId());

        String jsonBody = json.toString();
        LOGGER.info("Envoi de la requête de modification de tarification: " + jsonBody);

        RequestBody body = RequestBody.create(
            jsonBody,
            JSON
        );

        Request request = new Request.Builder()
            .url(TARIFICATION_ENDPOINT + "/" + tarification.getId())
            .put(body)
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .addHeader("Content-Type", CONTENT_TYPE)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Réponse de l'API: " + responseBody);
            
            if (!response.isSuccessful()) {
                LOGGER.severe("Erreur lors de la modification de la tarification. Code: " + response.code() + ", Message: " + responseBody);
                return false;
            }
            return true;
        }
    }

    public boolean supprimerTarification(int id) throws IOException {
        Request request = new Request.Builder()
            .url(TARIFICATION_ENDPOINT + "/" + id)
            .delete()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            LOGGER.info("Réponse de l'API: " + responseBody);
            
            if (!response.isSuccessful()) {
                LOGGER.severe("Erreur lors de la suppression de la tarification. Code: " + response.code() + ", Message: " + responseBody);
                return false;
            }
            return true;
        }
    }

    // Gestion des Types de Gardiennage
    public List<TypeGardiennage> getTypesGardiennage() {
        LOGGER.info("Récupération des types de gardiennage");
        
        Request request = new Request.Builder()
            .url(TYPE_GARDIENNAGE_ENDPOINT)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOGGER.warning("Erreur lors de la récupération des types de gardiennage : " + response.code());
                return null;
            }

            String responseBody = response.body().string();
            Type listType = new TypeToken<List<TypeGardiennage>>(){}.getType();
            List<TypeGardiennage> types = new Gson().fromJson(responseBody, listType);
            
            LOGGER.info("Nombre de types de gardiennage récupérés : " + (types != null ? types.size() : 0));
            return types;
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de la récupération des types de gardiennage : " + e.getMessage());
            return null;
        }
    }

    public List<Tarif> getTarificationsByPension(int pensionId) {
        LOGGER.info("Récupération des tarifications pour la pension " + pensionId);
        
        Request request = new Request.Builder()
            .url(TARIFICATION_ENDPOINT + "/pension/" + pensionId)
            .get()
            .addHeader("Accept", ACCEPT)
            .addHeader("Authorization", "Bearer " + authToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOGGER.warning("Erreur lors de la récupération des tarifications : " + response.code());
                return null;
            }

            String responseBody = response.body().string();
            Type listType = new TypeToken<List<Tarif>>(){}.getType();
            List<Tarif> tarifs = new Gson().fromJson(responseBody, listType);
            
            LOGGER.info("Nombre de tarifications récupérées pour la pension " + pensionId + " : " + (tarifs != null ? tarifs.size() : 0));
            return tarifs;
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de la récupération des tarifications : " + e.getMessage());
            return null;
        }
    }
}
