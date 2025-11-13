package com.example.demo.services;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Reservation.Response.ReservationResponseDTO;
import com.example.demo.entities.ReservationEntity;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MPService {

    private final ActivityService activityService;

    @Value("${access_token}")
    private String mercadoPagoAccessToken;

    public MPService(ActivityService activityService) {
        this.activityService = activityService;
    }

    public String mercado(ReservationEntity reservation) throws MPException, MPApiException {

        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);

        ActivityResponseDTO activity = activityService.findById(reservation.getActivity().getId());

        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success("https://www.seu-site/reservation/confirmar-pago?reservaId=" + reservation.getId())
                        .pending("https://www.seu-site/pending")
                        .failure("https://www.seu-site/failure")
                        .build();

        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id(String.valueOf(reservation.getId()))
                        .title(activity.getName())
                        .description(activity.getDescription())
                        .categoryId(String.valueOf(activity.getCategory()))
                        .quantity(1)
                        .currencyId("ARG")
                        .unitPrice(BigDecimal.valueOf(activity.getPrice()))
                        .build();
        List<PreferenceItemRequest> items = new ArrayList<>();

        items.add(itemRequest);

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(String.valueOf(reservation.getId()))
                .build();

        PreferenceClient client = new PreferenceClient();

        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint();
    }

}
