package com.logistics.company.application.service;

import com.logistics.company.application.port.in.ManageCompanyUseCase;
import com.logistics.company.application.port.out.CompanyEventPublisher;
import com.logistics.company.application.port.out.CompanyRepository;
import com.logistics.company.domain.model.Company;
import com.logistics.shared.domain.event.CompanyCreatedEvent;
import com.logistics.shared.domain.event.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CompanyApplicationService}.
 *
 * Uses Mockito to mock the repository and event publisher so that the tests
 * run entirely in-process without a database or Kafka broker.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyApplicationService")
class CompanyApplicationServiceTest {

    @Mock  CompanyRepository     repository;
    @Mock  CompanyEventPublisher eventPublisher;

    private CompanyApplicationService service;

    @BeforeEach
    void setUp() {
        service = new CompanyApplicationService(repository, eventPublisher);
    }

    // -----------------------------------------------------------------------
    // createCompany
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("createCompany()")
    class CreateCompany {

        @Test
        @DisplayName("Saves the company and returns it with an assigned ID")
        void create_savesAndReturnsCompany() {
            ManageCompanyUseCase.CreateCompanyCommand cmd = new ManageCompanyUseCase.CreateCompanyCommand(
                    "Acme Corp", Company.Type.SHIPPER,
                    "12-3456789", "Jane Smith", "j@acme.com", "+1-555-0100", null);

            when(repository.existsByName("Acme Corp")).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Company result = service.createCompany(cmd);

            assertThat(result.getName()).isEqualTo("Acme Corp");
            assertThat(result.getType()).isEqualTo(Company.Type.SHIPPER);
            assertThat(result.getStatus()).isEqualTo(Company.Status.ACTIVE);
            assertThat(result.getId()).isNotNull();
        }

        @Test
        @DisplayName("Publishes a CompanyCreatedEvent after successful save")
        void create_publishesCompanyCreatedEvent() {
            ManageCompanyUseCase.CreateCompanyCommand cmd = new ManageCompanyUseCase.CreateCompanyCommand(
                    "Acme Corp", Company.Type.SHIPPER, null, null, null, null, null);

            when(repository.existsByName(any())).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.createCompany(cmd);

            ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
            verify(eventPublisher, times(1)).publish(eventCaptor.capture());

            DomainEvent published = eventCaptor.getValue();
            assertThat(published).isInstanceOf(CompanyCreatedEvent.class);
            assertThat(published.getEventType()).isEqualTo("CompanyCreated");
        }

        @Test
        @DisplayName("Throws DuplicateCompanyNameException when name already exists")
        void create_throwsOnDuplicateName() {
            when(repository.existsByName("Acme Corp")).thenReturn(true);

            ManageCompanyUseCase.CreateCompanyCommand cmd = new ManageCompanyUseCase.CreateCompanyCommand(
                    "Acme Corp", Company.Type.SHIPPER, null, null, null, null, null);

            assertThatThrownBy(() -> service.createCompany(cmd))
                    .isInstanceOf(ManageCompanyUseCase.DuplicateCompanyNameException.class)
                    .hasMessageContaining("Acme Corp");

            verify(repository, never()).save(any());
            verify(eventPublisher, never()).publish(any());
        }
    }

    // -----------------------------------------------------------------------
    // activateCompany / deactivateCompany
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("activateCompany() / deactivateCompany()")
    class StatusTransitions {

        @Test
        @DisplayName("Activates a PENDING company")
        void activates_pendingCompany() {
            Company company = new Company("id-1", "Pending Co", Company.Type.CARRIER, Company.Status.PENDING);
            when(repository.findById("id-1")).thenReturn(Optional.of(company));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.activateCompany("id-1");

            verify(repository).save(argThat(c -> c.getStatus() == Company.Status.ACTIVE));
        }

        @Test
        @DisplayName("Throws CompanyNotFoundException when company does not exist")
        void activate_throwsWhenNotFound() {
            when(repository.findById("not-found")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.activateCompany("not-found"))
                    .isInstanceOf(ManageCompanyUseCase.CompanyNotFoundException.class);
        }
    }

    // -----------------------------------------------------------------------
    // deleteCompany
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("deleteCompany()")
    class DeleteCompany {

        @Test
        @DisplayName("Deletes an existing company")
        void delete_callsRepositoryDeleteById() {
            Company company = new Company("id-2", "To Delete", Company.Type.BOTH, Company.Status.ACTIVE);
            when(repository.findById("id-2")).thenReturn(Optional.of(company));

            service.deleteCompany("id-2");

            verify(repository).deleteById("id-2");
        }

        @Test
        @DisplayName("Throws CompanyNotFoundException for non-existent company")
        void delete_throwsForNonExistent() {
            when(repository.findById("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteCompany("ghost"))
                    .isInstanceOf(ManageCompanyUseCase.CompanyNotFoundException.class);
        }
    }
}
