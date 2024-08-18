package ru.library.service;

import ru.library.models.Book;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import ru.library.models.Person;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

public class BooksService {

    private static final String BASE_URL = "http://localhost:8080/api";

    @Inject
    private EntityManager entityManager;

    public List<Book> getAllBooks() throws Exception {
        URL url = new URL(BASE_URL + "/books");
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

        // Используем Gson для парсинга JSON в список объектов Book
        Gson gson = new Gson();
        List<Book> books = gson.fromJson(jsonOutput.toString(), new TypeToken<List<Book>>() {}.getType());

        return books;
    }

    public void addBook(Book book) throws Exception {
        URL url = new URL(BASE_URL + "/books"); // Endpoint для добавления книги
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Преобразуем объект Book в JSON
        Gson gson = new Gson();
        String jsonInputString = gson.toJson(book);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != 201) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        conn.disconnect();
    }

    public void updateBook(Book book) throws Exception {
        URL url = new URL(BASE_URL + "/books/" + book.getId());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        Gson gson = new Gson();
        String jsonInputString = gson.toJson(book);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        conn.disconnect();
    }

    public void deleteBook(Long bookId) throws Exception {
        URL url = new URL(BASE_URL + "/books/" + bookId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        conn.disconnect();
    }

    public Book getBookById(long id) throws Exception {
        URL url = new URL(BASE_URL + "/books/" + id);
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
        Book book = gson.fromJson(jsonOutput.toString(), Book.class);

        return book;
    }

    public Person getBookOwner(int id) throws Exception {
        URL url = new URL(BASE_URL + "/books/" + id + "/owner");
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
        Person owner = gson.fromJson(jsonOutput.toString(), Person.class);

        return owner;
    }

    public List<Book> searchByTitle(String query) {
        // Создаем запрос с использованием JPA для поиска книг по заголовку
        TypedQuery<Book> typedQuery = entityManager.createQuery(
                "SELECT b FROM Book b WHERE b.title LIKE :query", Book.class);
        typedQuery.setParameter("query", query + "%");

        return typedQuery.getResultList();
    }

    //возможность сформировать отчет о количестве выданных читателю книг за выбранный период.
    public List<Book> getBooksIssuedToPersonInPeriod(Person person, Date startDate, Date endDate) {
        String queryString = "SELECT b FROM Book b WHERE b.owner = :person AND b.takenAt BETWEEN :startDate AND :endDate";
        TypedQuery<Book> query = entityManager.createQuery(queryString, Book.class);
        query.setParameter("person", person);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    @Transactional
    public void release(int id) {
        Book book = entityManager.find(Book.class, id);
        if (book != null) {
            book.setOwner(null);
            book.setTakenAt(null);
            entityManager.merge(book);
        }
    }

    @Transactional
    public void assign(int id, Person selectedPerson) {
        Book book = entityManager.find(Book.class, id);
        if (book != null) {
            book.setAuthor(selectedPerson.getFullName());
            book.setTakenAt(new Date());
            entityManager.merge(book);
        }
    }

}