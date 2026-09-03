package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public void sendTripReminder(String toEmail, String username, String destination, LocalDate startDate) {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                </head>
                <body style="margin:0;padding:0;background:#f0f7f8;font-family:'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f7f8;padding:40px 0;">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 32px rgba(7,73,79,0.12);">
                
                        <!-- HEADER -->
                        <tr>
                          <td style="background:linear-gradient(135deg,#07494F 0%%,#0C7489 100%%);padding:36px 40px;text-align:center;">
                            <div style="font-size:32px;margin-bottom:8px;">✈️</div>
                            <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;letter-spacing:-0.5px;">TravelPlanner</h1>
                            <p style="margin:6px 0 0;color:rgba(255,255,255,0.75);font-size:13px;letter-spacing:0.05em;">TU COMPAÑERO DE VIAJE</p>
                          </td>
                        </tr>
                
                        <!-- BODY -->
                        <tr>
                          <td style="padding:40px 40px 32px;">
                            <p style="margin:0 0 8px;color:#07494F;font-size:15px;">Hola, <strong>%s</strong> 👋</p>
                            <h2 style="margin:0 0 24px;color:#07494F;font-size:22px;font-weight:700;">¡Tu aventura está por comenzar!</h2>
                
                            <!-- CARD DEL VIAJE -->
                            <div style="background:linear-gradient(135deg,#e8f4f5 0%%,#d0ecee 100%%);border-radius:16px;padding:24px;margin-bottom:28px;border-left:4px solid #0C7489;">
                              <p style="margin:0 0 6px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;letter-spacing:0.08em;font-weight:600;">Destino</p>
                              <p style="margin:0 0 16px;color:#07494F;font-size:24px;font-weight:700;">📍 %s</p>
                              <p style="margin:0 0 6px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;letter-spacing:0.08em;font-weight:600;">Fecha de inicio</p>
                              <p style="margin:0;color:#07494F;font-size:16px;font-weight:600;">📅 %s</p>
                            </div>
                
                            <p style="margin:0 0 28px;color:#555;font-size:15px;line-height:1.6;">
                              Faltan solo <strong style="color:#0C7489;">3 días</strong> para tu viaje. 
                              Asegurate de tener todo listo: documentos, itinerario y checklist completo.
                            </p>
                
                            <!-- BOTÓN -->
                            <div style="text-align:center;">
                              <a href="http://localhost:4200/trips" 
                                 style="display:inline-block;background:linear-gradient(135deg,#07494F,#0C7489);color:#ffffff;text-decoration:none;padding:14px 36px;border-radius:999px;font-size:15px;font-weight:700;letter-spacing:0.02em;">
                                Ver mi viaje →
                              </a>
                            </div>
                          </td>
                        </tr>
                
                        <!-- FOOTER -->
                        <tr>
                          <td style="background:#f8fbfb;padding:24px 40px;text-align:center;border-top:1px solid #e0eeef;">
                            <p style="margin:0;color:rgba(7,73,79,0.5);font-size:12px;">
                              © 2026 TravelPlanner · Este es un mensaje automático, no respondas este email.
                            </p>
                          </td>
                        </tr>
                
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(username, destination, startDate.toString());

        sendHtmlEmail(toEmail, "✈️ Tu viaje a " + destination + " comienza en 3 días", html);
    }

    public void sendBudgetAlert(String toEmail, String username, String tripName, Double budget, Double spent) {
        double percentage = (spent / budget) * 100;
        String color = percentage >= 100 ? "#dc2626" : "#d97706";
        String emoji = percentage >= 100 ? "🚨" : "⚠️";

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                </head>
                <body style="margin:0;padding:0;background:#f0f7f8;font-family:'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f7f8;padding:40px 0;">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 32px rgba(7,73,79,0.12);">
                
                        <!-- HEADER -->
                        <tr>
                          <td style="background:linear-gradient(135deg,#07494F 0%%,#0C7489 100%%);padding:36px 40px;text-align:center;">
                            <div style="font-size:32px;margin-bottom:8px;">✈️</div>
                            <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;">TravelPlanner</h1>
                            <p style="margin:6px 0 0;color:rgba(255,255,255,0.75);font-size:13px;letter-spacing:0.05em;">TU COMPAÑERO DE VIAJE</p>
                          </td>
                        </tr>
                
                        <!-- BODY -->
                        <tr>
                          <td style="padding:40px 40px 32px;">
                            <p style="margin:0 0 8px;color:#07494F;font-size:15px;">Hola, <strong>%s</strong> 👋</p>
                            <h2 style="margin:0 0 24px;color:%s;font-size:22px;font-weight:700;">%s Alerta de presupuesto</h2>
                
                            <!-- CARD -->
                            <div style="background:#fff8f0;border-radius:16px;padding:24px;margin-bottom:24px;border-left:4px solid %s;">
                              <p style="margin:0 0 6px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;letter-spacing:0.08em;font-weight:600;">Viaje</p>
                              <p style="margin:0 0 16px;color:#07494F;font-size:20px;font-weight:700;">🧳 %s</p>
                
                              <table width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td style="padding:0 8px 0 0;">
                                    <p style="margin:0 0 4px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;font-weight:600;">Gastado</p>
                                    <p style="margin:0;color:%s;font-size:20px;font-weight:700;">$%.2f</p>
                                  </td>
                                  <td style="padding:0 0 0 8px;">
                                    <p style="margin:0 0 4px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;font-weight:600;">Presupuesto</p>
                                    <p style="margin:0;color:#07494F;font-size:20px;font-weight:700;">$%.2f</p>
                                  </td>
                                </tr>
                              </table>
                
                              <!-- BARRA DE PROGRESO -->
                              <div style="margin-top:16px;">
                                <div style="background:#e5e7eb;border-radius:999px;height:10px;overflow:hidden;">
                                  <div style="background:%s;width:%.0f%%;height:100%%;border-radius:999px;"></div>
                                </div>
                                <p style="margin:6px 0 0;text-align:right;color:%s;font-size:13px;font-weight:700;">%.1f%% utilizado</p>
                              </div>
                            </div>
                
                            <p style="margin:0 0 28px;color:#555;font-size:15px;line-height:1.6;">
                              Te recomendamos revisar tus gastos y ajustar tu planificación si es necesario.
                            </p>
                
                            <div style="text-align:center;">
                              <a href="http://localhost:4200/trips"
                                 style="display:inline-block;background:linear-gradient(135deg,#07494F,#0C7489);color:#ffffff;text-decoration:none;padding:14px 36px;border-radius:999px;font-size:15px;font-weight:700;">
                                Ver mis gastos →
                              </a>
                            </div>
                          </td>
                        </tr>
                
                        <!-- FOOTER -->
                        <tr>
                          <td style="background:#f8fbfb;padding:24px 40px;text-align:center;border-top:1px solid #e0eeef;">
                            <p style="margin:0;color:rgba(7,73,79,0.5);font-size:12px;">
                              © 2026 TravelPlanner · Este es un mensaje automático, no respondas este email.
                            </p>
                          </td>
                        </tr>
                
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(username, color, emoji, color, tripName, color, spent, budget, color, Math.min(percentage, 100), color, percentage);

        sendHtmlEmail(toEmail, emoji + " Alerta de presupuesto - " + tripName, html);
    }

    public void sendPaymentConfirmed(String toEmail, String username, String activityName, Double amount) {
        String html = """
        <!DOCTYPE html>
        <html>
        <head><meta charset="UTF-8"/><meta name="viewport" content="width=device-width, initial-scale=1.0"/></head>
        <body style="margin:0;padding:0;background:#f0f7f8;font-family:'Segoe UI',Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f7f8;padding:40px 0;">
            <tr><td align="center">
              <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 32px rgba(7,73,79,0.12);">
                <tr>
                  <td style="background:linear-gradient(135deg,#07494F 0%%,#0C7489 100%%);padding:36px 40px;text-align:center;">
                    <div style="font-size:32px;margin-bottom:8px;">✈️</div>
                    <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;">TravelPlanner</h1>
                    <p style="margin:6px 0 0;color:rgba(255,255,255,0.75);font-size:13px;letter-spacing:0.05em;">TU COMPAÑERO DE VIAJE</p>
                  </td>
                </tr>
                <tr>
                  <td style="padding:40px 40px 32px;">
                    <p style="margin:0 0 8px;color:#07494F;font-size:15px;">Hola, <strong>%s</strong> 👋</p>
                    <h2 style="margin:0 0 24px;color:#07494F;font-size:22px;font-weight:700;">¡Tu pago fue confirmado! 🎉</h2>
                    <div style="background:linear-gradient(135deg,#e8f4f5 0%%,#d0ecee 100%%);border-radius:16px;padding:24px;margin-bottom:28px;border-left:4px solid #20DF6C;">
                      <p style="margin:0 0 6px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;letter-spacing:0.08em;font-weight:600;">Actividad</p>
                      <p style="margin:0 0 16px;color:#07494F;font-size:20px;font-weight:700;">🎯 %s</p>
                      <p style="margin:0 0 6px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;letter-spacing:0.08em;font-weight:600;">Monto pagado</p>
                      <p style="margin:0;color:#07494F;font-size:24px;font-weight:700;">💰 $%.2f</p>
                    </div>
                    <p style="margin:0 0 28px;color:#555;font-size:15px;line-height:1.6;">
                      Tu reserva quedó confirmada. Podés ver los detalles en la sección de reservaciones.
                    </p>
                    <div style="text-align:center;">
                      <a href="http://localhost:4200/reservaciones"
                         style="display:inline-block;background:linear-gradient(135deg,#07494F,#0C7489);color:#ffffff;text-decoration:none;padding:14px 36px;border-radius:999px;font-size:15px;font-weight:700;">
                        Ver mis reservas →
                      </a>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td style="background:#f8fbfb;padding:24px 40px;text-align:center;border-top:1px solid #e0eeef;">
                    <p style="margin:0;color:rgba(7,73,79,0.5);font-size:12px;">© 2026 TravelPlanner · Este es un mensaje automático, no respondas este email.</p>
                  </td>
                </tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(username, activityName, amount);

        sendHtmlEmail(toEmail, "✅ Pago confirmado - " + activityName, html);
    }

    public void sendPaymentFailed(String toEmail, String username, String activityName) {
        String html = """
        <!DOCTYPE html>
        <html>
        <head><meta charset="UTF-8"/><meta name="viewport" content="width=device-width, initial-scale=1.0"/></head>
        <body style="margin:0;padding:0;background:#f0f7f8;font-family:'Segoe UI',Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f7f8;padding:40px 0;">
            <tr><td align="center">
              <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 32px rgba(7,73,79,0.12);">
                <tr>
                  <td style="background:linear-gradient(135deg,#07494F 0%%,#0C7489 100%%);padding:36px 40px;text-align:center;">
                    <div style="font-size:32px;margin-bottom:8px;">✈️</div>
                    <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;">TravelPlanner</h1>
                    <p style="margin:6px 0 0;color:rgba(255,255,255,0.75);font-size:13px;letter-spacing:0.05em;">TU COMPAÑERO DE VIAJE</p>
                  </td>
                </tr>
                <tr>
                  <td style="padding:40px 40px 32px;">
                    <p style="margin:0 0 8px;color:#07494F;font-size:15px;">Hola, <strong>%s</strong> 👋</p>
                    <h2 style="margin:0 0 24px;color:#dc2626;font-size:22px;font-weight:700;">🚨 No pudimos procesar tu pago</h2>
                    <div style="background:#fff5f5;border-radius:16px;padding:24px;margin-bottom:28px;border-left:4px solid #dc2626;">
                      <p style="margin:0 0 6px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;letter-spacing:0.08em;font-weight:600;">Actividad</p>
                      <p style="margin:0;color:#07494F;font-size:20px;font-weight:700;">🎯 %s</p>
                    </div>
                    <p style="margin:0 0 28px;color:#555;font-size:15px;line-height:1.6;">
                      Tu pago no pudo ser procesado. Podés intentarlo nuevamente desde la sección de reservaciones.
                    </p>
                    <div style="text-align:center;">
                      <a href="http://localhost:4200/reservaciones"
                         style="display:inline-block;background:linear-gradient(135deg,#07494F,#0C7489);color:#ffffff;text-decoration:none;padding:14px 36px;border-radius:999px;font-size:15px;font-weight:700;">
                        Reintentar pago →
                      </a>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td style="background:#f8fbfb;padding:24px 40px;text-align:center;border-top:1px solid #e0eeef;">
                    <p style="margin:0;color:rgba(7,73,79,0.5);font-size:12px;">© 2026 TravelPlanner · Este es un mensaje automático, no respondas este email.</p>
                  </td>
                </tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(username, activityName);

        sendHtmlEmail(toEmail, "❌ Pago no procesado - " + activityName, html);
    }

    public void sendReservationCancelled(String toEmail, String username, String activityName) {
        String html = """
        <!DOCTYPE html>
        <html>
        <head><meta charset="UTF-8"/><meta name="viewport" content="width=device-width, initial-scale=1.0"/></head>
        <body style="margin:0;padding:0;background:#f0f7f8;font-family:'Segoe UI',Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f7f8;padding:40px 0;">
            <tr><td align="center">
              <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 32px rgba(7,73,79,0.12);">
                <tr>
                  <td style="background:linear-gradient(135deg,#07494F 0%%,#0C7489 100%%);padding:36px 40px;text-align:center;">
                    <div style="font-size:32px;margin-bottom:8px;">✈️</div>
                    <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;">TravelPlanner</h1>
                    <p style="margin:6px 0 0;color:rgba(255,255,255,0.75);font-size:13px;letter-spacing:0.05em;">TU COMPAÑERO DE VIAJE</p>
                  </td>
                </tr>
                <tr>
                  <td style="padding:40px 40px 32px;">
                    <p style="margin:0 0 8px;color:#07494F;font-size:15px;">Hola, <strong>%s</strong> 👋</p>
                    <h2 style="margin:0 0 24px;color:#d97706;font-size:22px;font-weight:700;">⚠️ Tu reserva fue cancelada</h2>
                    <div style="background:#fffbeb;border-radius:16px;padding:24px;margin-bottom:28px;border-left:4px solid #d97706;">
                      <p style="margin:0 0 6px;color:rgba(7,73,79,0.6);font-size:12px;text-transform:uppercase;letter-spacing:0.08em;font-weight:600;">Actividad cancelada</p>
                      <p style="margin:0;color:#07494F;font-size:20px;font-weight:700;">🎯 %s</p>
                    </div>
                    <p style="margin:0 0 28px;color:#555;font-size:15px;line-height:1.6;">
                      Tu reserva fue cancelada. Podés explorar otras actividades disponibles para tu viaje.
                    </p>
                    <div style="text-align:center;">
                      <a href="http://localhost:4200/activities"
                         style="display:inline-block;background:linear-gradient(135deg,#07494F,#0C7489);color:#ffffff;text-decoration:none;padding:14px 36px;border-radius:999px;font-size:15px;font-weight:700;">
                        Ver actividades →
                      </a>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td style="background:#f8fbfb;padding:24px 40px;text-align:center;border-top:1px solid #e0eeef;">
                    <p style="margin:0;color:rgba(7,73,79,0.5);font-size:12px;">© 2026 TravelPlanner · Este es un mensaje automático, no respondas este email.</p>
                  </td>
                </tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(username, activityName);

        sendHtmlEmail(toEmail, "⚠️ Reserva cancelada - " + activityName, html);
    }
}
