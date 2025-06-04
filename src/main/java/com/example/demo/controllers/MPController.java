package com.example.demo.controllers;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MPController {

    @Value("${access_token}")
    private String mercadoPagoAccessToken;

    @GetMapping("/mercado")
    public String mercado() throws MPException, MPApiException {

        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id("1234")
                        .title("Games")
                        .description("PS5")
                        .pictureUrl("http://picture.com/PS5")
                        .categoryId("games")
                        .quantity(2)
                        .currencyId("BRL")
                        .unitPrice(new BigDecimal("4000"))
                        .build();
        List<PreferenceItemRequest> items = new ArrayList<>();

        items.add(itemRequest);

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items).build();

        PreferenceClient client = new PreferenceClient();

        Preference preference = client.create(preferenceRequest);

        return preference.getSandboxInitPoint();
    }

}
