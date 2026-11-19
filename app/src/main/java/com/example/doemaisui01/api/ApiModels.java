package com.example.doemaisui01.api;

/**
 * Modelos do JSON. Os nomes dos campos sao iguais aos DTOs do back-end.
 * Datas viajam como String: LocalDateTime "2026-12-01T08:00:00", LocalDate "1998-05-20".
 * Enums viajam como String (ex.: "O_POSITIVE", "ELIGIBLE", "SCHEDULED").
 */
public class ApiModels {

    // ---------- Campanha ----------
    public static class Campaign {
        public Long idCampaign;
        public String campaignName;
        public String location;
        public String startDate;
        public String endDate;
    }

    // ---------- Doador ----------
    public static class DonorCreate {
        public String userName;
        public String email;
        public String password;
        public DonorCreate(String userName, String email, String password) {
            this.userName = userName; this.email = email; this.password = password;
        }
    }
    public static class DonorUpdate {
        public String userName;
        public String email;
        public DonorUpdate(String userName, String email) {
            this.userName = userName; this.email = email;
        }
    }
    public static class Donor {
        public Long idUser;
        public String userName;
        public String email;
    }

    // ---------- Funcionario ----------
    public static class EmployeeCreate {
        public String userName;
        public String email;
        public String password;
        public String function; // ADMIN, MANAGER, DOCTOR, ASSISTANT
        public EmployeeCreate(String userName, String email, String password, String function) {
            this.userName = userName; this.email = email; this.password = password; this.function = function;
        }
    }
    public static class Employee {
        public Long idUser;
        public String userName;
        public String email;
        public String function;
    }

    // ---------- Ficha do doador ----------
    public static class DonorFormRequest {
        public String birthDate;   // "1998-05-20"
        public String bloodType;   // A_POSITIVE ... O_NEGATIVE
        public Double height;      // metros
        public Double weight;      // kg
        public DonorFormRequest(String birthDate, String bloodType, Double height, Double weight) {
            this.birthDate = birthDate; this.bloodType = bloodType; this.height = height; this.weight = weight;
        }
    }
    public static class DonorFormEvaluation {
        public Long employeeId;
        public String status;      // PENDING, ELIGIBLE, INELIGIBLE, IN_PROGRESS
        public String observation; // ate 500 caracteres
        public DonorFormEvaluation(Long employeeId, String status, String observation) {
            this.employeeId = employeeId; this.status = status; this.observation = observation;
        }
    }
    public static class DonorForm {
        public Long donorId;
        public String donorName;
        public String birthDate;
        public String bloodType;
        public Double height;
        public Double weight;
        public String status;
        public String evaluationDate;
        public String observation;
        public String evaluatedBy;
    }

    // ---------- Inscricao ----------
    public static class EnrollmentRequest {
        public Long donorId;
        public Long campaignId;
        public String enrollmentDate; // precisa ser data futura
        public EnrollmentRequest(Long donorId, Long campaignId, String enrollmentDate) {
            this.donorId = donorId; this.campaignId = campaignId; this.enrollmentDate = enrollmentDate;
        }
    }
    public static class EnrollmentStatus {
        public String status; // SCHEDULED, COMPLETED, CANCELED, NO_SHOW
        public EnrollmentStatus(String status) { this.status = status; }
    }
    public static class Enrollment {
        public Long idEnrollment;
        public Long donorId;
        public String donorName;
        public Long campaignId;
        public String campaignName;
        public String enrollmentDate;
        public String status;
    }
}