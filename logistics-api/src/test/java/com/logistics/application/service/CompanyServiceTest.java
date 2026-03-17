package com.logistics.application.service;

import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.domain.model.Company;
import com.logistics.infrastructure.persistence.repository.InMemoryCompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link CompanyService} using real in-memory repository.
 */
@DisplayName("CompanyService")
class CompanyServiceTest {

    private InMemoryCompanyRepository repository;
    private CompanyService            service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCompanyRepository();
        service    = new CompanyService(repository);
    }

    private ManageCompanyUseCase.CreateCompanyCommand createCmd(String name, Company.Type type) {
        return new ManageCompanyUseCase.CreateCompanyCommand(
                name, type, null, null, null, null, null);
    }

    @Nested
    @DisplayName("createCompany()")
    class Create {

        @Test
        @DisplayName("creates company with ACTIVE status by default")
        void createsActiveCompany() {
            Company result = service.createCompany(
                    createCmd("Acme Corp", Company.Type.SHIPPER));

            assertThat(result.getId()).isNotNull();
            assertThat(result.getName()).isEqualTo("Acme Corp");
            assertThat(result.getStatus()).isEqualTo(Company.Status.ACTIVE);
        }

        @Test
        @DisplayName("persists company so findById returns it")
        void persistsCompany() {
            Company created = service.createCompany(
                    createCmd("Acme Corp", Company.Type.CARRIER));

            Optional<Company> found = service.findById(created.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Acme Corp");
        }

        @Test
        @DisplayName("preserves all contact info from command")
        void preservesContactInfo() {
            var cmd = new ManageCompanyUseCase.CreateCompanyCommand(
                    "Test Corp", Company.Type.BOTH,
                    "TAX-123", "John Doe", "john@test.com", "+1-555-9999", "Notes here");

            Company result = service.createCompany(cmd);

            assertThat(result.getTaxId()).isEqualTo("TAX-123");
            assertThat(result.getContactName()).isEqualTo("John Doe");
            assertThat(result.getContactEmail()).isEqualTo("john@test.com");
            assertThat(result.getNotes()).isEqualTo("Notes here");
        }
    }

    @Nested
    @DisplayName("updateCompany()")
    class Update {

        @Test
        @DisplayName("updates name and type")
        void updatesNameAndType() {
            Company original = service.createCompany(
                    createCmd("Old Name", Company.Type.SHIPPER));

            Company updated = service.updateCompany(
                    new ManageCompanyUseCase.UpdateCompanyCommand(
                            original.getId(), "New Name", Company.Type.CARRIER,
                            null, null, null, null, null));

            assertThat(updated.getName()).isEqualTo("New Name");
            assertThat(updated.getType()).isEqualTo(Company.Type.CARRIER);
        }

        @Test
        @DisplayName("throws CompanyNotFoundException when company does not exist")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.updateCompany(
                    new ManageCompanyUseCase.UpdateCompanyCommand(
                            "nonexistent", "Name", Company.Type.BOTH,
                            null, null, null, null, null)))
                    .isInstanceOf(ManageCompanyUseCase.CompanyNotFoundException.class)
                    .hasMessageContaining("nonexistent");
        }
    }

    @Nested
    @DisplayName("activateCompany() / deactivateCompany()")
    class Lifecycle {

        @Test
        @DisplayName("deactivates then reactivates company")
        void deactivatesThenReactivates() {
            Company company = service.createCompany(
                    createCmd("Flip Corp", Company.Type.BOTH));

            service.deactivateCompany(company.getId());
            assertThat(service.findById(company.getId()).get().getStatus())
                    .isEqualTo(Company.Status.INACTIVE);

            service.activateCompany(company.getId());
            assertThat(service.findById(company.getId()).get().getStatus())
                    .isEqualTo(Company.Status.ACTIVE);
        }
    }

    @Nested
    @DisplayName("deleteCompany()")
    class Delete {

        @Test
        @DisplayName("removes company from repository")
        void removesCompany() {
            Company company = service.createCompany(
                    createCmd("To Delete", Company.Type.CARRIER));

            service.deleteCompany(company.getId());

            assertThat(service.findById(company.getId())).isEmpty();
        }

        @Test
        @DisplayName("throws when company not found")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.deleteCompany("nonexistent"))
                    .isInstanceOf(ManageCompanyUseCase.CompanyNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAll() and findByStatus()")
    class Query {

        @Test
        @DisplayName("findAll returns all companies ordered by name")
        void findAllReturnsSortedByName() {
            service.createCompany(createCmd("Zeta Corp", Company.Type.BOTH));
            service.createCompany(createCmd("Alpha Corp", Company.Type.SHIPPER));

            List<Company> all = service.findAll();

            assertThat(all).extracting(Company::getName)
                    .containsExactly("Alpha Corp", "Zeta Corp");
        }

        @Test
        @DisplayName("findByStatus filters correctly")
        void findByStatusFilters() {
            Company active   = service.createCompany(createCmd("Active Co", Company.Type.CARRIER));
            Company inactive = service.createCompany(createCmd("Inactive Co", Company.Type.CARRIER));
            service.deactivateCompany(inactive.getId());

            List<Company> activeList = service.findByStatus(Company.Status.ACTIVE);

            assertThat(activeList).extracting(Company::getId)
                    .containsExactly(active.getId());
        }
    }
}
