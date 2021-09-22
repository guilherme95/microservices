package br.com.guilherme.controller;

import br.com.guilherme.repository.BookRepository;
import br.com.guilherme.model.Book;
import br.com.guilherme.proxy.CambioProxy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("book-service")
public class BookController {

    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CambioProxy cambioProxy;

    @Operation(summary = "Find a specific book by your id")
    @GetMapping(value="/{id}/{currency}")
    public Book findBook(@PathVariable("id") Long id, @PathVariable("currency") String currency){

        var book = bookRepository.getById(id);
        if(book==null) throw new RuntimeException("Book not found");

        var cambio = cambioProxy.getCambio(book.getPrice(), "USD", currency);

        var port = environment.getProperty("local.server.port");
        book.setEnvironment("Book port" + port + " Cambio port" + cambio.getEnvironment());
        book.setPrice(cambio.getConvertedValue());

        return book;
    }

    /*
    @GetMapping(value="/{id}/{currency}")
    public Book findBook(@PathVariable("id") Long id, @PathVariable("currency") String currency){

        var book = bookRepository.getById(id);
        if(book==null) throw new RuntimeException("Book not found");

        HashMap<String, String> params = new HashMap<>();

        params.put("amount", book.getPrice().toString());
        params.put("from", "USD");
        params.put("to", currency);

        var response = new RestTemplate().getForEntity("http://localhost:8000/cambio-service/{amount}/{from}/{to}",
                Cambio.class,
                params);

        var cambio = response.getBody();


        var port = environment.getProperty("local.server.port");
        book.setEnvironment(port);
        book.setPrice(cambio.getConvertedValue());

        return book;
//        return new Book(1L, "Nigel", new Date(), Double.valueOf(13.7), "Dockerzão", currency, port);
    }
    */
}
