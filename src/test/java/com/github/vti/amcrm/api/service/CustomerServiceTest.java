package com.github.vti.amcrm.api.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.TestFileUtils;
import com.github.vti.amcrm.api.Client;
import com.github.vti.amcrm.api.exception.ConflictException;
import com.github.vti.amcrm.api.exception.NotFoundException;
import com.github.vti.amcrm.api.service.request.CreateCustomerRequest;
import com.github.vti.amcrm.api.service.request.PatchCustomerRequest;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.infra.TestDatabase;
import com.github.vti.amcrm.infra.customer.dto.CustomerSummary;
import com.github.vti.amcrm.infra.photo.LocalPhotoStorage;
import com.github.vti.amcrm.infra.photo.PhotoStorage;
import com.github.vti.amcrm.infra.registry.DatabaseRepositoryRegistry;
import com.github.vti.amcrm.infra.registry.DatabaseViewRegistry;
import com.github.vti.amcrm.infra.registry.ViewRegistry;

@Tag("integration")
public class CustomerServiceTest {

    private CustomerService service;
    private Client client;
    private final String baseUrl = "http://localhost:4567";

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        DataSource dataSource = TestDatabase.setupDatabase(tmpDir);
        RepositoryRegistry repositoryRegistry = new DatabaseRepositoryRegistry(dataSource);
        ViewRegistry viewRegistry = new DatabaseViewRegistry(dataSource, baseUrl);
        PhotoStorage photoStorage = new LocalPhotoStorage(tmpDir, Paths.get("customer"));

        service = Mockito.spy(new CustomerService(repositoryRegistry, viewRegistry, photoStorage));

        client = Client.user(TestData.getRandomId());
        Mockito.doReturn(client).when(service).getClient();
    }

    @Test
    void throwsOnUnknownId() {
        assertThrows(
                NotFoundException.class, () -> service.getCustomerSummary(TestData.getRandomId()));
    }

    @Test
    void returnsCustomerSummary() {
        CreateCustomerRequest request = TestFactory.newCreateCustomerRequest();
        service.createCustomer(request);

        CustomerSummary customer = service.getCustomerSummary(request.getId());

        assertEquals(request.getId(), customer.getId());
        assertEquals(request.getName(), customer.getName());
        assertEquals(request.getSurname(), customer.getSurname());
    }

    @Test
    void returnsEmptyList() {
        List<CustomerSummary> customers =
                service.getCustomerList(Optional.of(100), Optional.empty());

        assertEquals(0, customers.size());
    }

    @Test
    void returnsList() {
        CreateCustomerRequest request1 = TestFactory.newCreateCustomerRequest();
        service.createCustomer(request1);

        CreateCustomerRequest request2 = TestFactory.newCreateCustomerRequest();
        service.createCustomer(request2);

        CreateCustomerRequest request3 = TestFactory.newCreateCustomerRequest();
        service.createCustomer(request3);

        List<CustomerSummary> customers =
                service.getCustomerList(Optional.of(100), Optional.empty());

        assertEquals(3, customers.size());
    }

    @Test
    void returnsListLimited() {
        CreateCustomerRequest request1 = TestFactory.newCreateCustomerRequest();
        service.createCustomer(request1);

        CreateCustomerRequest request2 = TestFactory.newCreateCustomerRequest();
        service.createCustomer(request2);

        CreateCustomerRequest request3 = TestFactory.newCreateCustomerRequest();
        service.createCustomer(request3);

        List<CustomerSummary> customers = service.getCustomerList(Optional.of(2), Optional.empty());

        assertEquals(2, customers.size());
    }

    @Test
    void createsNewCustomer() {
        CreateCustomerRequest request = TestFactory.newCreateCustomerRequest();

        CustomerSummary customer = service.createCustomer(request);

        assertEquals(request.getId(), customer.getId());
        assertEquals(request.getName(), customer.getName());
        assertEquals(request.getSurname(), customer.getSurname());
    }

    @Test
    void throwsOnInvalidPhoto() {
        CreateCustomerRequest request = TestFactory.newCreateCustomerRequestWithInvalidPhoto();

        assertThrows(ConflictException.class, () -> service.createCustomer(request));
    }

    @Test
    void createsNewCustomerWithPhoto() throws Exception {
        CreateCustomerRequest request = TestFactory.newCreateCustomerRequestWithPhoto();

        CustomerSummary customer = service.createCustomer(request);

        assertTrue(customer.getPhotoLocation().matches(".*/customer/.*?-32x32.jpg"));
        assertTrue(customer.getPhotoLocation().startsWith(baseUrl), customer.getPhotoLocation());
    }

    @Test
    void throwsWhenCustomerAlreadyExists() {
        CreateCustomerRequest request = TestFactory.newCreateCustomerRequest();

        service.createCustomer(request);

        assertThrows(ConflictException.class, () -> service.createCustomer(request));
    }

    @Test
    void throwsWhenPatchingUnknownCustomer() {
        PatchCustomerRequest request = new PatchCustomerRequest("Johnny", "Silver");

        assertThrows(
                NotFoundException.class,
                () -> service.patchCustomer(TestData.getRandomId(), request));
    }

    @Test
    void patchesCustomer() {
        CreateCustomerRequest createRequest = TestFactory.newCreateCustomerRequest();
        service.createCustomer(createRequest);

        PatchCustomerRequest request = new PatchCustomerRequest("Johnny", "Silver");
        CustomerSummary customer = service.patchCustomer(createRequest.getId(), request);

        assertEquals(createRequest.getId(), customer.getId());
        assertEquals(request.getName(), customer.getName());
        assertEquals(request.getSurname(), customer.getSurname());
    }

    @Test
    void patchesCustomerWithPhoto() throws Exception {
        CreateCustomerRequest createRequest = TestFactory.newCreateCustomerRequest();
        service.createCustomer(createRequest);

        String photoBlob = TestFileUtils.readFileBase64(TestData.getPhotoFile());

        PatchCustomerRequest request = new PatchCustomerRequest(null, null, photoBlob);
        CustomerSummary customer = service.patchCustomer(createRequest.getId(), request);

        assertTrue(
                customer.getPhotoLocation().matches(".*/customer/.*?-32x32.jpg"),
                customer.getPhotoLocation());
        assertTrue(customer.getPhotoLocation().startsWith(baseUrl), customer.getPhotoLocation());
    }

    @Test
    void throwsWhenPatchesDeletedCustomer() {
        CreateCustomerRequest createRequest = TestFactory.newCreateCustomerRequest();
        service.createCustomer(createRequest);

        service.deleteCustomer(createRequest.getId());

        PatchCustomerRequest request = new PatchCustomerRequest("Johnny", "Silver");

        assertThrows(
                NotFoundException.class,
                () -> service.patchCustomer(createRequest.getId(), request));
    }

    @Test
    void throwsWhenPatchesWithInvalidPhoto() {
        CreateCustomerRequest createRequest = TestFactory.newCreateCustomerRequest();
        service.createCustomer(createRequest);

        PatchCustomerRequest request = new PatchCustomerRequest("Johnny", "Silver", "foobar");

        assertThrows(
                ConflictException.class,
                () -> service.patchCustomer(createRequest.getId(), request));
    }

    @Test
    void throwsWhenDeletingUnknownCustomer() {
        assertThrows(NotFoundException.class, () -> service.deleteCustomer(TestData.getRandomId()));
    }

    @Test
    void deletesCustomer() {
        CreateCustomerRequest createRequest = TestFactory.newCreateCustomerRequest();
        service.createCustomer(createRequest);

        service.deleteCustomer(createRequest.getId());

        assertThrows(
                NotFoundException.class, () -> service.getCustomerSummary(createRequest.getId()));
    }

    @Test
    void throwsWhenDeletingAlreadyDeletedCustomer() {
        CreateCustomerRequest createRequest = TestFactory.newCreateCustomerRequest();
        service.createCustomer(createRequest);

        service.deleteCustomer(createRequest.getId());

        assertThrows(NotFoundException.class, () -> service.deleteCustomer(createRequest.getId()));
    }

    // TODO: not sure what the behavior should be
    // @Test
    // void idOfDeletedCustomerBecomesAvailableAgain() {
    //    service.createCustomer(new CreateCustomerRequest("1", "John", "Doe"));

    //    service.deleteCustomer("1");

    //    assertDoesNotThrow(() -> service.createCustomer(new CreateCustomerRequest("1", "John",
    // "Doe")));
    // }
}
