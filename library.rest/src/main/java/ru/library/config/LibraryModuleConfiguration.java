package ru.library.config;

import com.google.inject.AbstractModule;
import ru.library.service.BooksService;
import ru.library.service.PeopleService;

public class LibraryModuleConfiguration extends AbstractModule {
    @Override
    protected void configure() {
        bind(BooksService.class);
        bind(PeopleService.class);
    }
}