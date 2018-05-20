package io.github.sudhanshugupta.n26.transaction.management.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sudhanshugupta.n26.transaction.management.config.TestBeanConfiguration;
import io.github.sudhanshugupta.n26.transaction.management.domain.Transaction;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
@ContextConfiguration(classes = TestBeanConfiguration.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldAddTransaction() throws Exception {
        Transaction transaction = createTransaction(10, 0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(transaction)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldThrowNoContentWhenOldTransaction() throws Exception {
        Transaction transaction = createTransaction(10, 64);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(transaction)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldThrowBadRequestWhenFutureTransaction() throws Exception {
        Transaction transaction = Transaction.builder()
                .timestamp(Duration.of(System.currentTimeMillis(), ChronoUnit.MILLIS)
                        .plus(2, ChronoUnit.MINUTES).toMillis())
                .amount(10.0)
                .build();

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(transaction)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnStatistics() throws Exception {
        performTransaction(createTransaction(10d, 0));
        performTransaction(createTransaction(20d, 1));
        performTransaction(createTransaction(30d, 2));

        mockMvc.perform(get("/statistics").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("count", is(3)))
                .andExpect(jsonPath("sum", is(60.0)))
                .andExpect(jsonPath("avg", is(20.00000000)))
                .andExpect(jsonPath("max", is(30.0)))
                .andExpect(jsonPath("min", is(10.0)));
    }

    @Test
    public void shouldReturnZeroStatistics() throws Exception {

        mockMvc.perform(get("/statistics").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("count", is(0)))
                .andExpect(jsonPath("sum", is(0.0)))
                .andExpect(jsonPath("avg", is("NaN")))
                .andExpect(jsonPath("max", is("NaN")))
                .andExpect(jsonPath("min", is("NaN")));
    }

    private Transaction createTransaction(double amount, int delayInSec) {
        return Transaction.builder()
                .timestamp(Duration.of(System.currentTimeMillis(), ChronoUnit.MILLIS).minus(delayInSec, ChronoUnit.SECONDS).toMillis())
                .amount(amount)
                .build();
    }

    private void performTransaction(Transaction transaction) throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(transaction)))
                .andExpect(status().isCreated());
    }
}