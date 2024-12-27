package com.example.appointment.system.backend.service;

import com.example.appointment.system.backend.model.Schedule;
import com.example.appointment.system.backend.repository.ScheduleRepository;
import com.example.appointment.system.backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 8 * * ?") // Ежедневно в 8 утра
    public void sendDailyNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        processNotifications(tomorrow, "Напоминание: запись на завтра");
    }

    @Scheduled(cron = "0 * * * * ?") // Каждый час
    public void sendHourlyNotifications() {
        LocalDate today = LocalDate.now();
        LocalTime nextHour = LocalTime.now().plusHours(1);
        processNotifications(today, "Напоминание: запись через час", nextHour);
    }

    private void processNotifications(LocalDate date, String subject) {
        processNotifications(date, subject, null);
    }

    private void processNotifications(LocalDate date, String subject, LocalTime specificTime) {
        List<Schedule> schedules = scheduleRepository.findByDate(date);
        schedules.forEach(schedule -> schedule.getSlots().stream()
                .filter(slot -> slot.isBooked() && slot.getPatientId() != null)
                .filter(slot -> specificTime == null || LocalTime.parse(slot.getTime()).equals(specificTime))
                .forEach(slot -> {
                    UUID patientId = UUID.fromString(slot.getPatientId());
                    userRepository.findById(patientId).ifPresent(patient -> {
                        String email = patient.getEmail();
                        String message = String.format(
                                "Уважаемый %s! Напоминаем, что у вас запись ко врачу на %s в %s.",
                                patient.getName(), schedule.getDate(), slot.getTime()
                        );
                        sendEmail(email, subject, message);
                    });
                })
        );
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
