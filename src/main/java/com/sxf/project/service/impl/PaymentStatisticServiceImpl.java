package com.sxf.project.service.impl;


import com.sxf.project.dto.PaymentStatisticDTO;
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
    private WorkerRepository workerRepository; // Add repository to fetch Worker details

    @Override
    public List<PaymentStatisticDTO> getPaymentsByFilialId(Long filialId) {
        List<PaymentStatisticDTO> payments = new ArrayList<>();

        // Retrieve and classify ReportPayments
        List<ReportPayment> reportPayments = reportPaymentRepository.findByFilialId(filialId);
        for (ReportPayment reportPayment : reportPayments) {
            String entityName = reportPayment.getReport().getName(); // Assuming Report has a getName method
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(reportPayment.getId(), reportPayment.getNewPayment(), "ReportPayment", "income", entityName);
            payments.add(paymentDTO);
        }

        // Retrieve and classify PurchasingDepartment payments
        List<PurchasingDepartment> purchases = purchasingDepartmentRepository.findByFilialId(filialId);
        for (PurchasingDepartment purchase : purchases) {
            String entityName = purchase.getName();
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(purchase.getId(), purchase.getPayment(), "PurchasingDepartment", "outcome", entityName);
            payments.add(paymentDTO);
        }

        // Retrieve and classify MonthlySalaryPayments
        List<MonthlySalaryPayment> salaryPayments = monthlySalaryPaymentRepository.findByFilialId(filialId);
        for (MonthlySalaryPayment salaryPayment : salaryPayments) {
            Worker worker = workerRepository.findById(salaryPayment.getMonthlySalary().getWorker().getId()).orElse(null); // Assuming MonthlySalary has a getWorker method
            String entityName = worker != null ? worker.getName() : "Unknown Worker";
            PaymentStatisticDTO paymentDTO = new PaymentStatisticDTO(salaryPayment.getId(), salaryPayment.getPaymentAmount(), "MonthlySalaryPayment", "outcome", entityName);
            payments.add(paymentDTO);
        }

        return payments;
    }
}