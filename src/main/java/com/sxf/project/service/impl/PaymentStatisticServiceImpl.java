package com.sxf.project.service.impl;


import com.sxf.project.dto.PaymentStatisticDTO;
import com.sxf.project.dto.PaymentStatisticsResponseDTO;
import com.sxf.project.entity.MonthlySalaryPayment;
import com.sxf.project.entity.PurchasingDepartment;
import com.sxf.project.entity.ReportPayment;
import com.sxf.project.entity.Worker;
import com.sxf.project.repository.MonthlySalaryPaymentRepository;
import com.sxf.project.repository.PurchasingDepartmentRepository;
import com.sxf.project.repository.ReportPaymentRepository;
import com.sxf.project.repository.WorkerRepository;
import com.sxf.project.service.PaymentStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentStatisticServiceImpl implements PaymentStatisticService {

    @Autowired
    private ReportPaymentRepository reportPaymentRepository;

    @Autowired
    private PurchasingDepartmentRepository purchasingDepartmentRepository;

    @Autowired
    private MonthlySalaryPaymentRepository monthlySalaryPaymentRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public List<PaymentStatisticDTO> getPaymentsByFilialId(Long filialId) {
        List<PaymentStatisticDTO> payments = new ArrayList<>();

        // Retrieve and classify ReportPayments
        List<ReportPayment> reportPayments = reportPaymentRepository.findByFilialId(filialId);
        for (ReportPayment reportPayment : reportPayments) {
            String entityName = reportPayment.getReport().getName();
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(reportPayment.getId(), reportPayment.getNewPayment(), "income", "ReportPayment", entityName);
            payments.add(paymentDTO);
        }

        // Retrieve and classify PurchasingDepartment payments
        List<PurchasingDepartment> purchases = purchasingDepartmentRepository.findByFilialId(filialId);
        for (PurchasingDepartment purchase : purchases) {
            String entityName = purchase.getName();
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(purchase.getId(), purchase.getPayment(), "outcome", "PurchasingDepartment", entityName);
            payments.add(paymentDTO);
        }

        // Retrieve and classify MonthlySalaryPayments
        List<MonthlySalaryPayment> salaryPayments = monthlySalaryPaymentRepository.findByFilialId(filialId);
        for (MonthlySalaryPayment salaryPayment : salaryPayments) {
            Worker worker = workerRepository.findById(salaryPayment.getMonthlySalary().getWorker().getId()).orElse(null); // Assuming MonthlySalary has a getWorker method
            String entityName = worker != null ? worker.getName() : "Unknown Worker";
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(salaryPayment.getId(), salaryPayment.getPaymentAmount(), "outcome", "MonthlySalaryPayment", entityName);
            payments.add(paymentDTO);
        }

        return payments;
    }
    @Override
    public List<PaymentStatisticDTO> getPayments() {
        List<PaymentStatisticDTO> payments = new ArrayList<>();

        // Retrieve and classify ReportPayments
        List<ReportPayment> reportPayments = reportPaymentRepository.findAll();
        for (ReportPayment reportPayment : reportPayments) {
            String entityName = reportPayment.getReport().getName();
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(reportPayment.getId(), reportPayment.getNewPayment(), "income", "ReportPayment", entityName);
            payments.add(paymentDTO);
        }

        // Retrieve and classify PurchasingDepartment payments
        List<PurchasingDepartment> purchases = purchasingDepartmentRepository.findAll();
        for (PurchasingDepartment purchase : purchases) {
            String entityName = purchase.getName();
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(purchase.getId(), purchase.getPayment(), "outcome", "PurchasingDepartment", entityName);
            payments.add(paymentDTO);
        }

        // Retrieve and classify MonthlySalaryPayments
        List<MonthlySalaryPayment> salaryPayments = monthlySalaryPaymentRepository.findAll();
        for (MonthlySalaryPayment salaryPayment : salaryPayments) {
            Worker worker = workerRepository.findById(salaryPayment.getMonthlySalary().getWorker().getId()).orElse(null); // Assuming MonthlySalary has a getWorker method
            String entityName = worker != null ? worker.getName() : "Unknown Worker";
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(salaryPayment.getId(), salaryPayment.getPaymentAmount(), "outcome", "MonthlySalaryPayment", entityName);
            payments.add(paymentDTO);
        }

        return payments;
    }

    @Override
    public PaymentStatisticsResponseDTO getAllIncome() {
        List<PaymentStatisticDTO> payments = new ArrayList<>();
        double totalIncome = 0.0;

        // Retrieve and classify ReportPayments (income)
        List<ReportPayment> reportPayments = reportPaymentRepository.findAll();
        for (ReportPayment reportPayment : reportPayments) {
            String entityName = reportPayment.getReport().getName(); // Assuming Report has a getName method
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(reportPayment.getId(), reportPayment.getNewPayment(), "income", "ReportPayment", entityName);
            payments.add(paymentDTO);
            totalIncome += reportPayment.getNewPayment();
        }

        PaymentStatisticsResponseDTO response = new PaymentStatisticsResponseDTO();
        response.setPayments(payments);
        response.setTotal(totalIncome);
        return response;
    }

    @Override
    public PaymentStatisticsResponseDTO getAllOutcome() {
        List<PaymentStatisticDTO> payments = new ArrayList<>();
        double totalOutcome = 0.0;

        // Retrieve and classify PurchasingDepartment payments (outcome)
        List<PurchasingDepartment> purchases = purchasingDepartmentRepository.findAll();
        for (PurchasingDepartment purchase : purchases) {
            String entityName = purchase.getName();
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(purchase.getId(), purchase.getPayment(), "outcome", "PurchasingDepartment", entityName);
            payments.add(paymentDTO);
            totalOutcome += purchase.getPayment();
        }

        // Retrieve and classify MonthlySalaryPayments (outcome)
        List<MonthlySalaryPayment> salaryPayments = monthlySalaryPaymentRepository.findAll();
        for (MonthlySalaryPayment salaryPayment : salaryPayments) {
            Worker worker = workerRepository.findById(salaryPayment.getMonthlySalary().getWorker().getId()).orElse(null); // Assuming MonthlySalary has a getWorker method
            String entityName = worker != null ? worker.getName() : "Unknown Worker";
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(salaryPayment.getId(), salaryPayment.getPaymentAmount(), "outcome", "MonthlySalaryPayment", entityName);
            payments.add(paymentDTO);
            totalOutcome += salaryPayment.getPaymentAmount();
        }

        PaymentStatisticsResponseDTO response = new PaymentStatisticsResponseDTO();
        response.setPayments(payments);
        response.setTotal(totalOutcome);
        return response;
    }

    @Override
    public PaymentStatisticsResponseDTO getIncomeByFilialId(Long filialId) {
        List<PaymentStatisticDTO> payments = new ArrayList<>();
        double totalIncome = 0.0;

        // Retrieve and classify ReportPayments (income)
        List<ReportPayment> reportPayments = reportPaymentRepository.findByFilialId(filialId);
        for (ReportPayment reportPayment : reportPayments) {
            String entityName = reportPayment.getReport().getName(); // Assuming Report has a getName method
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(reportPayment.getId(), reportPayment.getNewPayment(), "income", "ReportPayment", entityName);
            payments.add(paymentDTO);
            totalIncome += reportPayment.getNewPayment();
        }

        PaymentStatisticsResponseDTO response = new PaymentStatisticsResponseDTO();
        response.setPayments(payments);
        response.setTotal(totalIncome);
        return response;
    }

    @Override
    public PaymentStatisticsResponseDTO getOutcomeByFilialId(Long filialId) {
        List<PaymentStatisticDTO> payments = new ArrayList<>();
        double totalOutcome = 0.0;

        // Retrieve and classify PurchasingDepartment payments (outcome)
        List<PurchasingDepartment> purchases = purchasingDepartmentRepository.findByFilialId(filialId);
        for (PurchasingDepartment purchase : purchases) {
            String entityName = purchase.getName();
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(purchase.getId(), purchase.getPayment(), "outcome", "PurchasingDepartment", entityName);
            payments.add(paymentDTO);
            totalOutcome += purchase.getPayment();
        }

        // Retrieve and classify MonthlySalaryPayments (outcome)
        List<MonthlySalaryPayment> salaryPayments = monthlySalaryPaymentRepository.findByFilialId(filialId);
        for (MonthlySalaryPayment salaryPayment : salaryPayments) {
            Worker worker = workerRepository.findById(salaryPayment.getMonthlySalary().getWorker().getId()).orElse(null); // Assuming MonthlySalary has a getWorker method
            String entityName = worker != null ? worker.getName() : "Unknown Worker";
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(salaryPayment.getId(), salaryPayment.getPaymentAmount(), "outcome", "MonthlySalaryPayment", entityName);
            payments.add(paymentDTO);
            totalOutcome += salaryPayment.getPaymentAmount();
        }

        PaymentStatisticsResponseDTO response = new PaymentStatisticsResponseDTO();
        response.setPayments(payments);
        response.setTotal(totalOutcome);
        return response;
    }
}
