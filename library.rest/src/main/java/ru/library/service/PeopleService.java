package ru.library.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.library.models.Person;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PeopleService {
    private static final String BASE_URL = "http://localhost:8080/api";

    @Inject
    private EntityManager entityManager;

    public List<Person> getAllPersons() throws Exception {
        URL url = new URL(BASE_URL + "/persons");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder jsonOutput = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            jsonOutput.append(output);
        }

        conn.disconnect();

        Gson gson = new Gson();
        List<Person> persons = gson.fromJson(jsonOutput.toString(), new TypeToken<List<Person>>() {}.getType());

        return persons;
    }

    public void addPerson(Person person) throws Exception {
        URL url = new URL(BASE_URL + "/persons");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        Gson gson = new Gson();
        String jsonInputString = gson.toJson(person);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != 201) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        conn.disconnect();
    }

    public void updatePerson(Person person) throws Exception {
        URL url = new URL(BASE_URL + "/persons/" + person.getId());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        Gson gson = new Gson();
        String jsonInputString = gson.toJson(person);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        conn.disconnect();
    }

    public void deletePerson(int id) throws Exception {
        URL url = new URL(BASE_URL + "/persons/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        conn.disconnect();
    }

    public Person getPersonById(int id) throws Exception {
        URL url = new URL(BASE_URL + "/persons/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder jsonOutput = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            jsonOutput.append(output);
        }

        conn.disconnect();

        Gson gson = new Gson();
        Person person = gson.fromJson(jsonOutput.toString(), Person.class);

        return person;
    }
}